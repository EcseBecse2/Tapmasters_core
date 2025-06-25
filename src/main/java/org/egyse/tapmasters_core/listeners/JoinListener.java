package org.egyse.tapmasters_core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.egyse.tapmasters_core.Tapmasters_core;

public class JoinListener implements Listener {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        pl.localDataManager.userJoined(e.getPlayer());
        pl.clickerGuiUtil.playerJoined(e.getPlayer());
    }
}
