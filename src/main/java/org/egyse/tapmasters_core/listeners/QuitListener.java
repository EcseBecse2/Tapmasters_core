package org.egyse.tapmasters_core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.egyse.tapmasters_core.Tapmasters_core;

public class QuitListener implements Listener {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        pl.localDataManager.userLeft(e.getPlayer());
    }
}
