package org.egyse.tapmasters_core.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.Currency;
import org.egyse.tapmasters_core.models.User;

import java.util.ArrayList;
import java.util.List;

public class BoosterUtil implements Listener {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private Booster globalBooster = null;
    private ItemStack boostItem;

    private final NamespacedKey boosterKey = new NamespacedKey(pl, "isBooster");
    private final NamespacedKey currencyKey = new NamespacedKey(pl, "booster_currency");
    private final NamespacedKey multiKey = new NamespacedKey(pl, "booster_multi");
    private final NamespacedKey timerKey = new NamespacedKey(pl, "booster_timer");
    private final NamespacedKey globalKey = new NamespacedKey(pl, "global_booster");

    public BoosterUtil() {
        boostItem = new ItemStack(Material.ENDER_EYE);
        ItemMeta itemMeta = boostItem.getItemMeta();
        itemMeta.setDisplayName("{name} &7Booster");
        List<String> lore = new ArrayList<>();
        lore.add("&fMulti: &cx{multi}");
        lore.add("&fTimer: &c{timer}");
        itemMeta.setLore(lore);
        boostItem.setItemMeta(itemMeta);

        Bukkit.getScheduler().runTaskLater(pl, () -> {
            System.out.println("Loading global booster");
            Booster savedBooster = pl.dataManager.getGlobalBooster();
            if (savedBooster != null) {
                this.globalBooster = savedBooster;
                System.out.println("Loaded global booster from database");
            }
        }, 20);
    }

    public void giveItem(Booster b, Player p) {
        ItemStack item = boostItem.clone();
        ItemMeta itemMeta = item.getItemMeta();

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(boosterKey, PersistentDataType.BOOLEAN, true);
        pdc.set(currencyKey, PersistentDataType.STRING, b.getType().name());
        pdc.set(multiKey, PersistentDataType.DOUBLE, b.getMultiplier());
        pdc.set(timerKey, PersistentDataType.INTEGER, b.getTimer());
        pdc.set(globalKey, PersistentDataType.BOOLEAN, b.isGlobalBooster());

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemMeta.getDisplayName()
                .replace("{name}", b.getType().toString())
                .replace("{multi}", b.getMultiplier() + "")
                .replace("{timer}", pl.timerUtil.formatTime(b.getTimer()))));
        List<String> lore = itemMeta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)
                    .replace("{name}", b.getType().toString())
                    .replace("{multi}", b.getMultiplier() + "")
                    .replace("{timer}", pl.timerUtil.formatTime(b.getTimer()))));
        }

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        if (p.getInventory().firstEmpty() > -1) {
            p.getInventory().addItem(item);
        } else {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cItem &4&ldropped &cat your feet because your inventory is &4&lfull&c!"));
        }
    }

    public Booster getBoosterFromItem(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        return new Booster(pdc.get(timerKey, PersistentDataType.INTEGER), Currency.valueOf(pdc.get(currencyKey, PersistentDataType.STRING)), pdc.get(multiKey, PersistentDataType.DOUBLE), pdc.get(globalKey, PersistentDataType.BOOLEAN));
    }

    public void startBoosterTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, () -> {
            for (User user : pl.localDataManager.getOnlineUsers()) { checkBoosters(user); }
            if (globalBooster != null) {
                globalBooster.setTimer(globalBooster.getTimer() - 1);
                if (globalBooster.getTimer() <= 0) {
                    globalBooster = null;
                }
            }
        }, 20L, 20L);
    }

    public void checkBoosters(User user) {
        if (user.getBoosters().isEmpty()) return;
        List<Booster> boosters = user.getBoosters();
        for (int i = 0; i < boosters.size(); i++)
        {
            if (boosters.get(i).getTimer() <= 0) {
                boosters.remove(i);
                i--;
                continue;
            }
            boosters.get(i).setTimer(boosters.get(i).getTimer() - 1);
        }
        user.setBoosters(boosters);
        pl.localDataManager.updateOnlineUser(user);
    }

    public void addBooster(User user, Booster booster) {
        List<Booster> boosters = user.getBoosters();
        for (int i = 0; i < boosters.size(); i++) {
            Booster b = boosters.get(i);
            if (b.getType() == booster.getType()) {
                if (b.getMultiplier() != booster.getMultiplier()) continue;
                b.setTimer(b.getTimer() + booster.getTimer());
                boosters.set(i, b);
                user.setBoosters(boosters);
                pl.localDataManager.updateOnlineUser(user);
                return;
            }
        }
        boosters.add(booster);
        user.setBoosters(boosters);
        pl.localDataManager.updateOnlineUser(user);
    }

    public Booster getGlobalBooster() {
        return globalBooster;
    }

    public boolean setGlobalBooster(Booster globalBooster) {
        if (getGlobalBooster() != null) return false;
        this.globalBooster = globalBooster;
        return true;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) return;
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            if (pdc.has(boosterKey, PersistentDataType.BOOLEAN)) {
                e.setCancelled(true);
                Booster b = getBoosterFromItem(itemStack);
                if (b == null) return;
                if (b.isGlobalBooster()) {
                    if (setGlobalBooster(b)) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            pl.sendMessage(p, "&6{player} &factivated a &cx&f{multi} {type}&f booster for &b{time}&f."
                                            .replace("{player}", e.getPlayer().getName())
                                            .replace("{multi}", b.getMultiplier() + "")
                                            .replace("{type}", b.getType().toString())
                                            .replace("{time}", pl.timerUtil.formatTime(b.getTimer()))
                                    , true);
                        }
                    } else {
                        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere's already an active global booster."));
                    }
                } else {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    addBooster(pl.localDataManager.getOnlineUser(e.getPlayer().getUniqueId()), b);
                    pl.sendMessage(e.getPlayer(), "&fYou activated a &cx&f{multi} {type}&f booster for &b{time}&f."
                                    .replace("{multi}", b.getMultiplier() + "")
                                    .replace("{type}", b.getType().toString())
                                    .replace("{time}", pl.timerUtil.formatTime(b.getTimer()))
                            , true);
                }
            }
        }
    }
}
