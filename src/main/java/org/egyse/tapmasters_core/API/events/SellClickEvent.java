package org.egyse.tapmasters_core.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SellClickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double baseClicks;

    // Multipliers
    private final List<Double> moneyMultipliers = new ArrayList<>();

    public SellClickEvent(Player player, double clicks) {
        this.player = player;
        this.baseClicks = clicks;
        moneyMultipliers.add(1.0);
    }

    // ======== Multiplier Methods ========
    public void addMoneyMultiplier(double multiplier) {
        if(multiplier > 0) moneyMultipliers.add(multiplier);
    }

    public double calculateMoneyTotal() {
        return (baseClicks / 4) * moneyMultipliers.stream()
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

    public double getBaseClicks() {
        return baseClicks;
    }

    public List<Double> getMoneyMultipliers() {
        return this.moneyMultipliers;
    }
}
