package org.egyse.tapmasters_core.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ClickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    // Multipliers
    private final List<Double> clickMultipliers = new ArrayList<>();
    private final List<Double> gemMultipliers = new ArrayList<>();
    private final List<Double> tokenMultipliers = new ArrayList<>();

    // Chances (default to 1.0)
    private double gemChance = 1.0;
    private double tokenChance = 1.0;

    public ClickEvent(Player player) {
        this.player = player;
        // Initialize base multipliers
        clickMultipliers.add(1.0);
        gemMultipliers.add(1.0);
        tokenMultipliers.add(1.0);
    }

    // ======== Multiplier Methods ========
    public void addClickMultiplier(double multiplier) {
        if(multiplier > 0) clickMultipliers.add(multiplier);
    }

    public void addGemMultiplier(double multiplier) {
        if(multiplier > 0) gemMultipliers.add(multiplier);
    }

    public void addTokenMultiplier(double multiplier) {
        if(multiplier > 0) tokenMultipliers.add(multiplier);
    }

    // ======== Chance Methods ========
    public void addGemChance(double chance) {
        if(chance > 0) gemChance += chance;
    }

    public void addTokenChance(double chance) {
        if(chance > 0) tokenChance += chance;
    }

    // ======== Calculation Methods ========
    public double calculateClickTotal() {
        return clickMultipliers.stream().reduce(1.0, (a, b) -> a * b);
    }

    public double calculateGemTotal() {
        return gemMultipliers.stream().reduce(1.0, (a, b) -> a * b);
    }

    public double calculateTokenTotal() {
        return tokenMultipliers.stream().reduce(1.0, (a, b) -> a * b);
    }

    // ======== Chance Getters ========
    public double getGemChance() {
        return Math.min(gemChance, 100.0); // Max 100% chance
    }

    public double getTokenChance() {
        return Math.min(tokenChance, 10000.0); // Max 100% (100.00 = 1% when using 1-10000)
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
}