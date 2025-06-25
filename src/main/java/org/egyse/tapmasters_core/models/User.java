package org.egyse.tapmasters_core.models;

import org.egyse.tapmasters_core.Tapmasters_core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    private UUID uuid;
    private String name;
    private double click;
    private double rawClick;
    private double money;
    private double gem;
    private double token;
    private double prestige;
    private double prestigePoint;
    private List<Upgrade> upgrades;
    private List<Booster> boosters;

    public User(UUID uuid, String name, double click, double rawClick, double money, double gem, double token, double prestige, double prestigePoint, List<Upgrade> upgrades, List<Booster> boosters) {
        this.uuid = uuid;
        this.name = name;
        this.click = click;
        this.rawClick = rawClick;
        this.money = money;
        this.gem = gem;
        this.token = token;
        this.prestige = prestige;
        this.prestigePoint = prestigePoint;
        this.upgrades = upgrades;
        this.boosters = boosters;
    }

    public User(UUID uuid, String name, double click, double rawClick, double money, double gem, double token, double prestige, double prestigePoint) {
        this.uuid = uuid;
        this.name = name;
        this.click = click;
        this.rawClick = rawClick;
        this.money = money;
        this.gem = gem;
        this.token = token;
        this.prestige = prestige;
        this.prestigePoint = prestigePoint;
        this.upgrades = pl.default_upg;
        this.boosters = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getClick() {
        return click;
    }

    public void setClick(double click) {
        this.click = click;
    }

    public double getRawClick() {
        return rawClick;
    }

    public void setRawClick(double rawClick) {
        this.rawClick = rawClick;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getGem() {
        return gem;
    }

    public void setGem(double gem) {
        this.gem = gem;
    }

    public double getToken() {
        return token;
    }

    public void setToken(double token) {
        this.token = token;
    }

    public double getPrestige() {
        return prestige;
    }

    public void setPrestige(double prestige) {
        this.prestige = prestige;
    }

    public double getPrestigePoint() {
        return prestigePoint;
    }

    public void setPrestigePoint(double prestigepoint) {
        this.prestigePoint = prestigepoint;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public List<Booster> getBoosters() {
        return boosters;
    }

    public void setBoosters(List<Booster> boosters) {
        this.boosters = boosters;
    }
}
