package org.egyse.tapmasters_core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.egyse.tapmasters_core.commands.*;
import org.egyse.tapmasters_core.data.DataManager;
import org.egyse.tapmasters_core.data.UserManager;
import org.egyse.tapmasters_core.data.LocalDataManager;
import org.egyse.tapmasters_core.listeners.JoinListener;
import org.egyse.tapmasters_core.listeners.QuitListener;
import org.egyse.tapmasters_core.models.*;
import org.egyse.tapmasters_core.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Tapmasters_core extends JavaPlugin {
    private static Tapmasters_core instance;

    public List<Upgrade> default_upg = new ArrayList(
            Arrays.asList(
                    new Upgrade(Currency.CLICK, 1, 50, 50, 1.5, Currency.MONEY, 1, 0.2, 25),
                    new Upgrade(Currency.CLICK, 1, 250, 250, 2, Currency.MONEY, 1, 0.5, 75),
                    new Upgrade(Currency.CLICK, 1, 500, 500, 2.25, Currency.MONEY, 1, 1, 150),
                    new Upgrade(Currency.CLICK, 1, 1500, 1500, 2.25, Currency.MONEY, 1, 1.5, 200),
                    new Upgrade(Currency.CLICK, 1, 3000, 3000, 3, Currency.MONEY, 1, 2, 300),
                    new Upgrade(Currency.CLICK, 1, 50, 50, 1.5, Currency.GEM, 1, 2, 50),
                    new Upgrade(Currency.CLICK, 1, 250, 250, 2, Currency.GEM, 1, 3, 100),
                    new Upgrade(Currency.CLICK, 1, 750, 750, 2.5, Currency.GEM, 1, 4, 150),
                    new Upgrade(Currency.CLICK, 1, 1, 1, 1.25, Currency.PRESTIGE_POINT, 1, 2, 300),
                    new Upgrade(Currency.GEM, 1, 250, 250, 2, Currency.MONEY, 1, 2, 25),
                    new Upgrade(Currency.GEM, 1, 1000, 1000, 1.5, Currency.MONEY, 1, 2.5, 100),
                    new Upgrade(Currency.GEM, 1, 50, 50, 2.5, Currency.GEM, 1, 1.5, 250),
                    new Upgrade(Currency.GEM, 1, 1, 1, 2, Currency.PRESTIGE_POINT, 1, 2, 150),
                    new Upgrade(Currency.GEM, 1, 3, 3, 1.5, Currency.PRESTIGE_POINT, 1, 4, 100),
                    new Upgrade(Currency.PRESTIGE_POINT, 1, 1, 1, 1.5, Currency.PRESTIGE_POINT, 1, 1.5, 25),
                    new Upgrade(Currency.PRESTIGE_POINT, 1, 1, 1, 2.5, Currency.PRESTIGE_POINT, 1, 2, 150)
            )
    );

    public DataManager dataManager;
    public LocalDataManager localDataManager;
    public BoosterUtil boosterUtil;
    public TimerUtil timerUtil;
    public ClickerGuiUtil clickerGuiUtil;
    public CurrencyFormatter currencyFormatter;
    public PrestigeUtil prestigeUtil;
    public GgEventUtil ggEventUtil;
    public PlaceholderAPIUtil placeholderAPIUtil;
    public LeaderBoardUtil leaderBoardUtil;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        dataManager = new UserManager(getDataFolder());
        localDataManager = new LocalDataManager();
        boosterUtil = new BoosterUtil();
        timerUtil = new TimerUtil();
        clickerGuiUtil = new ClickerGuiUtil();
        currencyFormatter = new CurrencyFormatter();
        prestigeUtil = new PrestigeUtil();
        ggEventUtil = new GgEventUtil();
        placeholderAPIUtil = new PlaceholderAPIUtil();
        leaderBoardUtil = new LeaderBoardUtil();

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        Bukkit.getPluginManager().registerEvents(boosterUtil, this);
        Bukkit.getPluginManager().registerEvents(clickerGuiUtil, this);
        Bukkit.getPluginManager().registerEvents(prestigeUtil, this);
        Bukkit.getPluginManager().registerEvents(ggEventUtil, this);

        getCommand("tapmaster").setExecutor(new TapmasterCommand());
        getCommand("account").setExecutor(new AccountCommand());
        getCommand("sell").setExecutor(new SellCommand());
        getCommand("booster").setExecutor(new BoosterCommand());
        getCommand("prestige").setExecutor(new PrestigeCommand());

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPIUtil.register();
            System.out.println("Placeholders registered.");
        }


        boosterUtil.startBoosterTimer();
        autoSaveData();
    }

    @Override
    public void onDisable() {
        localDataManager.saveToDatabase(true);
        dataManager.disconnect();
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPIUtil.unregister();
            System.out.println("Placeholders unregistered.");
        }
    }

    public void autoSaveData() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            localDataManager.saveToDatabase(false);
        }, 300L, getConfig().getInt("data.auto-save") * 20L);
    }

    public void sendMessage(CommandSender sender, String message, boolean includePrefix) {
        if (sender instanceof Player p) {
            if (includePrefix) p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix") + message));
            else p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            System.out.println(message);
        }
    }

    public Inventory cloneInv(Inventory inv, String title) {
        Inventory newInv = Bukkit.createInventory(
                inv.getHolder(),
                inv.getSize(),
                title
        );

        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack original = inv.getItem(slot);
            if (original != null) {
                newInv.setItem(slot, original.clone());
            }
        }

        return newInv;
    }

    public static Tapmasters_core getInstance() {
        return instance;
    }
}
