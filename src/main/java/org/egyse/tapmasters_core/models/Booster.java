package org.egyse.tapmasters_core.models;

public class Booster {
    private int timer;
    private Currency type;
    private double multiplier;
    private boolean globalBooster;

    public Booster(int timer, Currency type, double multiplier, boolean globalBooster) {
        this.timer = timer;
        this.type = type;
        this.multiplier = multiplier;
        this.globalBooster = globalBooster;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public Currency getType() {
        return type;
    }

    public void setType(Currency type) {
        this.type = type;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isGlobalBooster() {
        return globalBooster;
    }

    public void setGlobalBooster(boolean globalBooster) {
        this.globalBooster = globalBooster;
    }
}
