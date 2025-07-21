package org.egyse.tapmasters_core.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalDataManager {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private final Map<UUID, User> onlineUsers = new ConcurrentHashMap<>();
    private final Set<UUID> pendingLoads = ConcurrentHashMap.newKeySet();

    public void userJoined(Player p) {
        UUID uuid = p.getUniqueId();
        System.out.println("Scheduling async load for " + p.getName());
        pendingLoads.add(uuid);

        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            User user = pl.dataManager.getUser(uuid.toString());

            if (user == null) {
                System.out.println("Creating new user for " + p.getName());
                user = new User(uuid, p.getName(), 0, 0, 0, 0, 0, 0, 0);
                pl.dataManager.createUser(user);
            }

            User finalUser = user;
            Bukkit.getScheduler().runTask(pl, () -> {
                if (pendingLoads.remove(uuid) && Bukkit.getPlayer(uuid) != null) {
                    onlineUsers.put(uuid, finalUser);
                    System.out.println("Loaded user for " + p.getName());
                }
            });
        });
    }


    public void userLeft(Player p) {
        UUID uuid = p.getUniqueId();
        pendingLoads.remove(uuid);
        User user = onlineUsers.remove(uuid);

        if (user != null) {
            Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                    pl.dataManager.updateUser(user)
            );
        }
    }

    public User getOnlineUser(UUID uuid) {
        return onlineUsers.get(uuid);
    }

    public void updateOnlineUser(User user) {
        onlineUsers.put(user.getUuid(), user);
    }

    public List<User> getOnlineUsers() { return onlineUsers.values().stream().toList(); }

    public void saveToDatabase(boolean stopped) {
        System.out.println("Starting autosave...");
        List<User> users = new ArrayList<>(onlineUsers.values());

        if (stopped) {
            for (User user : users) {
                pl.dataManager.updateUser(user);
            }
            pl.dataManager.setGlobalBooster(pl.boosterUtil.getGlobalBooster()); System.out.println("saved globalbooster");
            System.out.println("Autosave done.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            for (User user : users) {
                pl.dataManager.updateUser(user);
            }
            pl.dataManager.setGlobalBooster(pl.boosterUtil.getGlobalBooster()); System.out.println("saved globalbooster");
        });
        System.out.println("Autosave done.");
    }
}