package org.egyse.tapmasters_core.models;

import java.util.UUID;

public class Upgrade {
    private final UUID uuid;
    private Currency multiplied;
    private int level;
    private int base_cost;
    private double cost;
    private double cost_multi;
    private Currency currency;
    private double multiplier;
    private double increment_multi;
    private int max_level;

    public Upgrade(Currency multiplied, int level, int base_cost, double cost, double cost_multi, Currency currency, double multiplier, double increment_multi, int max_level) {
        this.uuid = UUID.randomUUID();
        this.multiplied = multiplied;
        this.level = level;
        this.base_cost = base_cost;
        this.cost = cost;
        this.cost_multi = cost_multi;
        this.currency = currency;
        this.increment_multi = increment_multi;
        this.multiplier = multiplier;
        this.max_level = max_level;
    }

    public Upgrade(Upgrade other, boolean newUuid) {
        if (!newUuid) this.uuid = other.uuid;
        else this.uuid = UUID.randomUUID();
        this.multiplied = other.multiplied;
        this.level = other.level;
        this.base_cost = other.base_cost;
        this.cost = other.cost;
        this.cost_multi = other.cost_multi;
        this.currency = other.currency;
        this.multiplier = other.multiplier;
        this.increment_multi = other.increment_multi;
        this.max_level = other.max_level;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Currency getMultiplied() {
        return multiplied;
    }

    public void setMultiplied(Currency multiplied) {
        this.multiplied = multiplied;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getBase_cost() {
        return base_cost;
    }

    public void setBase_cost(int base_cost) {
        this.base_cost = base_cost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCost_multi() {
        return cost_multi;
    }

    public void setCost_multi(double cost_multi) {
        this.cost_multi = cost_multi;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getIncrement_multi() {
        return increment_multi;
    }

    public void setIncrement_multi(double increment_multi) {
        this.increment_multi = increment_multi;
    }

    public int getMax_level() {
        return max_level;
    }

    public void setMax_level(int max_level) {
        this.max_level = max_level;
    }
}
