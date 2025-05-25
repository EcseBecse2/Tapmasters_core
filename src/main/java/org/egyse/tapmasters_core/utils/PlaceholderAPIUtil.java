package org.egyse.tapmasters_core.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderAPIUtil extends PlaceholderExpansion {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public @NotNull String getIdentifier() {
        return "tapmasters";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EcseBecse2";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        User u = pl.localDataManager.getOnlineUser(player.getUniqueId());
        if (u == null) return null;

        // click
        if (identifier.equals("click_raw")) {
            return String.valueOf(u.getClick());
        }
        if (identifier.equals("click") || identifier.equals("click_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getClick());
        }

        // raw click
        if (identifier.equals("rawclick_raw")) {
            return String.valueOf(u.getRawClick());
        }
        if (identifier.equals("rawclick") || identifier.equals("rawclick_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getRawClick());
        }

        // money
        if (identifier.equals("money_raw")) {
            return String.valueOf(u.getMoney());
        }
        if (identifier.equals("money") || identifier.equals("money_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getMoney());
        }

        // gem
        if (identifier.equals("gem_raw")) {
            return String.valueOf(u.getGem());
        }
        if (identifier.equals("gem") || identifier.equals("gem_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getGem());
        }

        // token
        if (identifier.equals("token_raw")) {
            return String.valueOf(u.getToken());
        }
        if (identifier.equals("token") || identifier.equals("token_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getToken());
        }

        // prestige
        if (identifier.equals("prestige_raw")) {
            return String.valueOf(u.getPrestige());
        }
        if (identifier.equals("prestige") || identifier.equals("prestige_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getPrestige());
        }

        // prestigepoint
        if (identifier.equals("prestigepoint_raw")) {
            return String.valueOf(u.getPrestigePoint());
        }
        if (identifier.equals("prestigepoint") || identifier.equals("prestigepoint_formatted")) {
            return pl.currencyFormatter.formatCurrency(u.getPrestigePoint());
        }

        // prestige progress
        if (identifier.equals("prestige_progress_percent")) {
            double percent = Math.min(Math.round(((double) u.getClick() / (double) pl.prestigeUtil.price) * 100) / 10.0, 100.0);
            DecimalFormat df = new DecimalFormat("#0.0");
            return df.format(percent) + "%";
        }

        if (identifier.equals("prestige_progress_bar")) {
            String start = "&8[";
            String end = "&8]";

            StringBuilder text = new StringBuilder(start);

            double ratio = (double) u.getClick() / (double) pl.prestigeUtil.price;
            double percent = Math.min(Math.round(ratio * 100 * 10) / 10.0, 100.0);

            for (int i = 1; i <= 5; i++) {
                if (percent >= (i - 1) * 20) {
                    text.append("&a▌");
                } else {
                    text.append("&7▌");
                }
            }

            text.append(end);
            return text.toString();
        }

        // %tapmasters_top_{currency}_{position}_{name/value}% & %tapmasters_top_{currency}_{pos/name/value}%
        return pl.leaderBoardUtil.onRequest(player, identifier);
    }
}
