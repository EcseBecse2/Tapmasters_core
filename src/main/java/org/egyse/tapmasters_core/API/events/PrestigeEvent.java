package org.egyse.tapmasters_core.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.egyse.tapmasters_core.models.Upgrade;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrestigeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final User user;
    private final double points;

    // multi
    private final List<Double> prestigePointMultipliers = new ArrayList<>();

    public PrestigeEvent(Player player, User user, double points) {
        this.player = player;
        this.user = user;
        this.points = points;
    }

    public void addPrestigePointMultiplier(double multiplier) {
        if(multiplier > 0) prestigePointMultipliers.add(multiplier);
    }

    public double calculatePrestigePointTotal() {
        return points * prestigePointMultipliers.stream()
                .reduce(1.0, (a, b) -> a * b);
    }

    // ======== Standard Methods ========
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
    }

    public double getPoints() {
        return points;
    }

}
