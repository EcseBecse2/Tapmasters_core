package org.egyse.tapmasters_core.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Currency;
import org.egyse.tapmasters_core.models.GGEvent;
import org.egyse.tapmasters_core.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GgEventUtil implements Listener {

    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private GGEvent event = new GGEvent();

    public void startEvent() {
        if (event.isRunning()) return;

        event.setRunning(true);
        timeStop();

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            for (String l : pl.getConfig().getStringList("gg-event")) {
                pl.sendCenteredMessage(p, ChatColor.translateAlternateColorCodes('&', l));
            }
        }
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        if (!event.isRunning()) return;
        if (!e.getMessage().contains("gg")) return;
        Player p = e.getPlayer();
        List<UUID> players = event.getPlayers();
        if (players.contains(p.getUniqueId())) return;

        players.add(p.getUniqueId());
        event.setPlayers(players);

        // give reward
        User user = pl.localDataManager.getOnlineUser(p.getUniqueId());
        user.setGem(user.getGem() + 200);

        Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                pl.localDataManager.updateOnlineUser(user)
        );

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou just received &c200 " + Currency.GEM + "s&f."));
    }

    private void timeStop() {
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            event.setRunning(false);
            event.setPlayers(new ArrayList<>());
        }, 300L);
    }

}
