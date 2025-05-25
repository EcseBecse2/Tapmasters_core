package org.egyse.tapmasters_core.utils;

import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.User;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.*;

public class LeaderBoardUtil {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private final Map<String, List<User>> leaderboards = new ConcurrentHashMap<>();
    private final Map<String, Map<UUID, Integer>> positionMaps = new ConcurrentHashMap<>();
    private Timer refreshTimer;

    private final Set<String> CURRENCIES = Set.of(
            "click", "rawclick", "money", "gem",
            "token", "prestige", "prestigepoint"
    );

    public void startRefreshTask() {
        if (refreshTimer != null) refreshTimer.cancel();
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshAllLeaderboards();
            }
        }, 0, 3 * 60 * 1000);
    }

    private void refreshAllLeaderboards() {
        List<User> allUsers = pl.dataManager.getAllUsers();
        CURRENCIES.forEach(currency -> {
            List<User> sorted = sortUsers(currency, allUsers);
            leaderboards.put(currency, sorted);
            positionMaps.put(currency, createPositionMap(sorted));
        });
    }

    private List<User> sortUsers(String currency, List<User> users) {
        Comparator<User> comparator = switch (currency) {
            case "click" -> Comparator.comparingDouble(User::getClick).reversed();
            case "rawclick" -> Comparator.comparingDouble(User::getRawClick).reversed();
            case "money" -> Comparator.comparingDouble(User::getMoney).reversed();
            case "gem" -> Comparator.comparingDouble(User::getGem).reversed();
            case "token" -> Comparator.comparingDouble(User::getToken).reversed();
            case "prestige" -> Comparator.comparingDouble(User::getPrestige).reversed();
            case "prestigepoint" -> Comparator.comparingDouble(User::getPrestigePoint).reversed();
            default -> throw new IllegalArgumentException("Unknown currency");
        };

        return users.stream()
                .sorted(comparator)
                .toList();
    }

    private Map<UUID, Integer> createPositionMap(List<User> sortedUsers) {
        Map<UUID, Integer> map = new HashMap<>();
        for (int i = 0; i < sortedUsers.size(); i++) {
            map.put(sortedUsers.get(i).getUuid(), i + 1);
        }
        return map;
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        if (!identifier.startsWith("top_")) return null;

        String[] parts = identifier.split("_");
        if (parts.length == 4) {
            return handlePositionBased(parts);
        } else if (parts.length == 3) {
            return handlePlayerBased(player, parts);
        }
        return "Invalid format";
    }

    private String handlePositionBased(String[] parts) {
        String currency = parts[1];
        int position;
        try {
            position = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return "Invalid position";
        }
        String type = parts[3];

        if (!CURRENCIES.contains(currency)) return "Invalid currency";
        if (position < 1 || position > 10) return "Invalid position";

        List<User> topList = leaderboards.getOrDefault(currency, Collections.emptyList());
        if (position > topList.size()) return "N/A";

        User user = topList.get(position - 1);
        return switch (type) {
            case "pos" -> String.valueOf(position);
            case "name" -> user.getName();
            case "value" -> getFormattedValue(currency, user);
            default -> "Invalid type";
        };
    }

    private String handlePlayerBased(OfflinePlayer player, String[] parts) {
        String currency = parts[1];
        String type = parts[2];

        if (!CURRENCIES.contains(currency)) return "Invalid currency";

        Map<UUID, Integer> positionMap = positionMaps.getOrDefault(currency, Collections.emptyMap());
        Integer position = positionMap.get(player.getUniqueId());
        if (position == null) return "N/A";

        List<User> sorted = leaderboards.getOrDefault(currency, Collections.emptyList());
        if (position > sorted.size()) return "N/A";

        User user = sorted.get(position - 1);
        return switch (type) {
            case "pos" -> String.valueOf(position);
            case "name" -> user.getName();
            case "value" -> getFormattedValue(currency, user);
            default -> "Invalid type";
        };
    }

    private String getFormattedValue(String currency, User user) {
        return switch (currency) {
            case "click" -> pl.currencyFormatter.formatCurrency(user.getClick());
            case "rawclick" -> formatRawValue(user.getRawClick());
            case "money" -> pl.currencyFormatter.formatCurrency(user.getMoney());
            case "gem" -> pl.currencyFormatter.formatCurrency(user.getGem());
            case "token" -> pl.currencyFormatter.formatCurrency(user.getToken());
            case "prestige" -> String.valueOf((int) Math.floor(user.getPrestige()));
            case "prestigepoint" -> pl.currencyFormatter.formatCurrency(user.getPrestigePoint());
            default -> "N/A";
        };
    }

    private String formatRawValue(double value) {
        return value % 1 == 0 ?
                String.valueOf((long) value) :
                String.format("%.1f", value);
    }
}