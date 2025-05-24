package org.egyse.tapmasters_core.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.User;

import java.util.*;

public class LocalDataManager {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private final Map<UUID, User> onlineUsers = new HashMap<>();

    public void userJoined(Player p) {
        UUID uuid = p.getUniqueId();
        System.out.println("Loading user for player " + p.getName());
        User user = pl.dataManager.getUser(uuid.toString());

        if (user == null) {
            System.out.println("The player is not found in the database...");
            System.out.println("Creating user for player " + p.getName());
            user = new User(
                    uuid,
                    p.getName(),
                    0, 0, 0, 0, 0, 0, 0
            );
            User finalUser = user;
            Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                    pl.dataManager.createUser(finalUser)
            );
            System.out.println("Created...");
        }

        onlineUsers.put(uuid, user);
    }

    public void userLeft(Player p) {
        UUID uuid = p.getUniqueId();
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

    public User getOfflineUser(UUID uuid) {
        return pl.dataManager.getUser(uuid.toString());
    }

    public void updateOnlineUser(User user) {
        onlineUsers.put(user.getUuid(), user);
    }

    public void updateOfflineUser(User user) {
        pl.dataManager.updateUser(user);
    }

    public List<User> getOnlineUsers() { return onlineUsers.values().stream().toList(); }

    public boolean isOnline(User user) {
        return onlineUsers.containsKey(user.getUuid());
    }

    public void saveToDatabase(boolean stopped) {
        System.out.println("Starting autosave...");
        for (User user : onlineUsers.values()) {
            if (stopped) {
                pl.dataManager.updateUser(user);
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                        pl.dataManager.updateUser(user)
                );
            }
        }
        System.out.println("Saving globalbooster");
        if (pl.boosterUtil.getGlobalBooster() != null) { pl.dataManager.setGlobalBooster(pl.boosterUtil.getGlobalBooster()); System.out.println("saved globalbooster"); }
        System.out.println("Autosave done.");
    }
}