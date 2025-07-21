package org.egyse.tapmasters_core.utils;

import com.google.gson.JsonSyntaxException;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.egyse.tapmasters_core.API.events.ClickEvent;
import org.egyse.tapmasters_core.API.events.UpgradeEvent;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.*;
import com.google.gson.Gson;
import org.egyse.tapmasters_tutorial.models.StepType;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClickerGuiUtil implements Listener {

    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    ItemStack item;

    private final Gson gson = new Gson();

    private final NamespacedKey clickerItemKey = new NamespacedKey(pl, "isClickerItem");
    private final NamespacedKey upgradeKey = new NamespacedKey(pl, "upgradeKey");

    public ClickerGuiUtil() {
        defineClickItem();
        defineGuiItems();
        defineGuis();
    }

    private void defineClickItem() {
        item = new ItemStack(Material.NETHER_STAR);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&lClicker Gui"));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eRight-Click to open the gui"));

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(clickerItemKey, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(itemMeta);
    }

    ItemStack guiClickItem;
    ItemStack guiUpgradeItem;

    ItemStack moneyUpgradeItem;
    ItemStack gemUpgradeItem;
    ItemStack prestigePointUpgradeItem;

    ItemStack clickUpgradingItem;
    ItemStack gemUpgradingItem;
    ItemStack prestigePointUpgradingItem;

    ItemStack upgradeFillItem;
    ItemStack guiFillItem;

    public void defineGuiItems() {
        guiClickItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = guiClickItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lClick me to gain clicks"));
        guiClickItem.setItemMeta(meta);

        guiUpgradeItem = new ItemStack(Material.GOLD_BLOCK);
        meta = guiUpgradeItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lUpgrades Menu"));
        guiUpgradeItem.setItemMeta(meta);

        moneyUpgradeItem = new ItemStack(Material.GOLD_INGOT);
        meta = moneyUpgradeItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lMoney upgrades"));
        moneyUpgradeItem.setItemMeta(meta);

        gemUpgradeItem = new ItemStack(Material.DIAMOND);
        meta = gemUpgradeItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lGem upgrades"));
        gemUpgradeItem.setItemMeta(meta);

        prestigePointUpgradeItem = new ItemStack(Material.EMERALD);
        meta = prestigePointUpgradeItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&1&lPrestigePoint upgrades"));
        prestigePointUpgradeItem.setItemMeta(meta);

        clickUpgradingItem = new ItemStack(Material.IRON_INGOT);
        gemUpgradingItem = new ItemStack(Material.DIAMOND);
        prestigePointUpgradingItem = new ItemStack(Material.EMERALD);

        upgradeFillItem = new ItemStack(Material.BARRIER);
        guiFillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }

    Inventory mainGui;
    Inventory upgrade_sample_gui;

    String mainGuiTitle = ChatColor.translateAlternateColorCodes('&', "&bClicker Gui");

    int[] upgradeSlots = new int[] { 20, 21, 22, 23, 24, 29, 30, 31, 32, 33 };
    public void defineGuis() {
        // Main gui
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 3*9; i++) slots.add(i);

        mainGui = Bukkit.createInventory(null, 3*9, mainGuiTitle);

        slots.remove(Integer.valueOf(11));
        slots.remove(Integer.valueOf(15));
        mainGui.setItem(11, guiClickItem);
        mainGui.setItem(15, guiUpgradeItem);
        for (int slot : slots) mainGui.setItem(slot, guiFillItem);

        // Upgrade guis
        // moneyupgrades
        slots.clear();
        for (int i = 0; i < 5*9; i++) slots.add(i);

        upgrade_sample_gui = Bukkit.createInventory(null, 5*9);

        slots.remove(Integer.valueOf(3));
        upgrade_sample_gui.setItem(3, moneyUpgradeItem);
        slots.remove(Integer.valueOf(4));
        upgrade_sample_gui.setItem(4, gemUpgradeItem);
        slots.remove(Integer.valueOf(5));
        upgrade_sample_gui.setItem(5, prestigePointUpgradeItem);

        for (int upgradeSlot : upgradeSlots) {
            upgrade_sample_gui.setItem(upgradeSlot, upgradeFillItem);
            slots.remove(Integer.valueOf(upgradeSlot));
        }

        for (int slot : slots) upgrade_sample_gui.setItem(slot, guiFillItem);
    }

    String moneyUpgradeTitle = "Upgrades (money)";
    String gemUpgradeTitle = "Upgrades (gem)";
    String prestigePointUpgradeTitle = "Upgrades (prestige point)";

    public boolean isClickItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return false;
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.has(clickerItemKey, PersistentDataType.BOOLEAN);
    }

    // Gui functions
    public void openClickerGui(Player p) {
        p.openInventory(mainGui);
    }

    public Inventory fillUpgradeItems(Inventory inv, Player p, Currency currency) {

        User user = pl.localDataManager.getOnlineUser(p.getUniqueId());
        List<Upgrade> upgrades = new ArrayList<>(user.getUpgrades());
        upgrades.removeIf(upgrade -> upgrade.getCurrency() != currency);

        List<ItemStack> upgradeItems = new ArrayList<>();
        for (Upgrade upgrade : upgrades) {
            ItemStack item = null;

            switch (upgrade.getMultiplied()) {
                case CLICK -> {
                    item = clickUpgradingItem.clone();
                }
                case GEM -> {
                    item = gemUpgradingItem.clone();
                }
                case PRESTIGE_POINT -> {
                    item = prestigePointUpgradingItem.clone();
                }
            }

            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    upgrade.getMultiplied().toString() + " &fupgrade &8[&7" + upgrade.getLevel() + "&8]"));

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&e| &fLevel: &6" + ((upgrade.getLevel() == upgrade.getMax_level()) ? "&cMAXED" : upgrade.getLevel())));
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&e| &fMultiplier: &6x" + String.format("%,.1f", upgrade.getMultiplier())));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&e&lLeft-Click &fto &6{current} -> {next} &7({price})".replace("{current}", upgrade.getLevel() + "").replace("{next}", upgrade.getLevel()+1 + "").replace("{price}", pl.currencyFormatter.formatCurrency(upgrade.getCost()))));
            lore.add(ChatColor.translateAlternateColorCodes('&',
                    "&e&lRight-Click &fto &cMAX"));
            meta.setLore(lore);

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(upgradeKey, PersistentDataType.STRING, gson.toJson(upgrade));

            item.setItemMeta(meta);
            upgradeItems.add(item);
        }

        for (int i = 0; i < upgradeSlots.length; i++) {
            if (i >= upgradeItems.size()) break;
            int slot = upgradeSlots[i];

            inv.setItem(slot, upgradeItems.get(i));
        }

        return inv;
    }

    public void openMoneyUpgradeGui(Player p) {
        Inventory localInv = pl.cloneInv(upgrade_sample_gui, moneyUpgradeTitle);

        localInv = fillUpgradeItems(localInv, p, Currency.MONEY);

        p.openInventory(localInv);
    }

    public void openGemUpgradeGui(Player p) {
        Inventory localInv = pl.cloneInv(upgrade_sample_gui, gemUpgradeTitle);

        localInv = fillUpgradeItems(localInv, p, Currency.GEM);

        p.openInventory(localInv);
    }

    public void openPrestigePointUpgradeGui(Player p) {
        Inventory localInv = pl.cloneInv(upgrade_sample_gui, prestigePointUpgradeTitle);

        localInv = fillUpgradeItems(localInv, p, Currency.PRESTIGE_POINT);

        p.openInventory(localInv);
    }

    // Handle click
    @EventHandler
    public void interactEvent(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null) return;
        if (!isClickItem(Objects.requireNonNull(e.getItem()))) return;
        openClickerGui(e.getPlayer());
    }

    // give item when player joins
    public void playerJoined(Player p) {
        p.getInventory().setItem(0, item.clone());
    }

    public void playerClicked(Player p) {
        User user = pl.localDataManager.getOnlineUser(p.getUniqueId());

        ClickEvent event = new ClickEvent(p);

        // ======== Core Multipliers ========
        // Click
        user.getUpgrades().stream()
                .filter(u -> u.getMultiplied() == Currency.CLICK)
                .forEach(u -> event.addClickMultiplier(u.getMultiplier()));

        user.getBoosters().stream()
                .filter(b -> b.getType() == Currency.CLICK)
                .forEach(b -> event.addClickMultiplier(b.getMultiplier()));

        // Gem
        user.getUpgrades().stream()
                .filter(u -> u.getMultiplied() == Currency.GEM)
                .forEach(u -> event.addGemMultiplier(u.getMultiplier()));

        user.getBoosters().stream()
                .filter(b -> b.getType() == Currency.GEM)
                .forEach(b -> event.addGemMultiplier(b.getMultiplier()));

        // Token
        user.getUpgrades().stream()
                .filter(u -> u.getMultiplied() == Currency.TOKEN)
                .forEach(u -> event.addTokenMultiplier(u.getMultiplier()));

        user.getBoosters().stream()
                .filter(b -> b.getType() == Currency.TOKEN)
                .forEach(b -> event.addTokenMultiplier(b.getMultiplier()));

        // Global boosters (example for click, add others similarly)
        Booster globalBooster = pl.boosterUtil.getGlobalBooster();
        if(globalBooster != null) {
            switch(globalBooster.getType()) {
                case CLICK:
                    event.addClickMultiplier(globalBooster.getMultiplier());
                    break;
                case GEM:
                    event.addGemMultiplier(globalBooster.getMultiplier());
                    break;
                case TOKEN:
                    event.addTokenMultiplier(globalBooster.getMultiplier());
                    break;
            }
        }

        // ======== Call Plugins ========
        Bukkit.getPluginManager().callEvent(event);

        // ======== Apply Results ========
        // Clicks
        double clickValue = event.calculateClickTotal();
        user.setClick(user.getClick() + clickValue);
        pl.tutorial.userManager.logAction(p, StepType.GAIN_CLICK, clickValue);

        // Gems (1-100 roll)
        if(Math.random() * 100 < event.getGemChance()) {
            user.setGem(user.getGem() + event.calculateGemTotal());
        }

        // Tokens (1-10000 roll)
        if(Math.random() * 100 < event.getTokenChance()) {
            user.setToken(user.getToken() + event.calculateTokenTotal());
            pl.tutorial.userManager.logAction(p, StepType.GAIN_TOKEN, event.calculateTokenTotal());
        }

        user.setRawClick(user.getRawClick() + 1);

        Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                pl.localDataManager.updateOnlineUser(user)
        );
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) { if (isClickItem(e.getItemDrop().getItemStack())) e.setCancelled(true); }

    @EventHandler
    public void onMoveItemEvent(InventoryMoveItemEvent e) { if (isClickItem(e.getItem())) e.setCancelled(true); }

    @EventHandler
    public void onDragItemEvent(InventoryDragEvent e) { if (isClickItem(e.getOldCursor())) e.setCancelled(true); }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        if ((event.getMainHandItem() != null && isClickItem(event.getMainHandItem())) || (event.getOffHandItem() != null && isClickItem(event.getOffHandItem()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (e.getClick() == ClickType.NUMBER_KEY) {
            int hotbarButton = e.getHotbarButton();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
            if (hotbarItem != null && isClickItem(hotbarItem)) {
                e.setCancelled(true);
                return;
            }
        }

        if (e.getClick() == ClickType.SWAP_OFFHAND) {
            ItemStack cursorItem = e.getCursor();
            if (cursorItem != null && isClickItem(cursorItem)) {
                e.setCancelled(true);
                return;
            }
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        if (isClickItem(clickedItem)) { e.setCancelled(true); return; }
        ItemStack cursorItem = e.getCursor();
        if (cursorItem != null && isClickItem(cursorItem)) { e.setCancelled(true); return; }

        // Check if in one of our GUIs
        String title = e.getView().getTitle();
        boolean isOurGUI = title.equals(mainGuiTitle) ||
                title.equals(moneyUpgradeTitle) ||
                title.equals(gemUpgradeTitle) ||
                title.equals(prestigePointUpgradeTitle);

        if (!isOurGUI) return;

        e.setCancelled(true);

        // Handle navigation items
        if (clickedItem.isSimilar(guiUpgradeItem) ||
                clickedItem.isSimilar(moneyUpgradeItem) ||
                clickedItem.isSimilar(gemUpgradeItem) ||
                clickedItem.isSimilar(prestigePointUpgradeItem) ||
                clickedItem.isSimilar(guiClickItem)) {
            if (clickedItem.isSimilar(guiUpgradeItem) || clickedItem.isSimilar(moneyUpgradeItem)) {
                openMoneyUpgradeGui(player);
            } else if (clickedItem.isSimilar(gemUpgradeItem)) {
                openGemUpgradeGui(player);
            } else if (clickedItem.isSimilar(prestigePointUpgradeItem)) {
                openPrestigePointUpgradeGui(player);
            } else {
                if (e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT) return;
                playerClicked(player);
            }
            return;
        }

        // Handle upgrade purchases
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(upgradeKey, PersistentDataType.STRING)) return;

        try {
            // Get template upgrade from item
            Upgrade templateUpgrade = gson.fromJson(
                    pdc.get(upgradeKey, PersistentDataType.STRING),
                    Upgrade.class
            );

            User user = pl.localDataManager.getOnlineUser(player.getUniqueId());

            // Find matching upgrade in user's list
            Upgrade userUpgrade = null;
            for (Upgrade u : user.getUpgrades()) {
                if (u.getUuid().toString().equals(templateUpgrade.getUuid().toString())) {
                    userUpgrade = u;
                    break;
                }
            }

            if (userUpgrade == null) {
                player.sendMessage(ChatColor.RED + "Upgrade not found!");
                return;
            }

            // Check max level
            if (userUpgrade.getLevel() >= userUpgrade.getMax_level()) {
                player.sendMessage(ChatColor.RED + "Upgrade already maxed!");
                return;
            }

            // Check currency
            double cost = userUpgrade.getCost();
            boolean canAfford = switch (userUpgrade.getCurrency()) {
                case MONEY -> user.getMoney() >= cost;
                case GEM -> user.getGem() >= cost;
                case PRESTIGE_POINT -> user.getPrestigePoint() >= cost;
                default -> false;
            };

            if (!canAfford) {
                player.sendMessage(ChatColor.RED + "Not enough currency!");
                return;
            }

            if (e.getClick() == ClickType.LEFT) {
                // Deduct cost
                switch (userUpgrade.getCurrency()) {
                    case MONEY: user.setMoney(user.getMoney() - cost); break;
                    case GEM: user.setGem(user.getGem() - cost); break;
                    case PRESTIGE_POINT: user.setPrestigePoint(user.getPrestigePoint() - cost); break;
                }

                // Upgrade logic
                userUpgrade.setLevel(userUpgrade.getLevel() + 1);
                userUpgrade.setCost(cost * userUpgrade.getCost_multi());
                userUpgrade.setMultiplier(userUpgrade.getMultiplier() + userUpgrade.getIncrement_multi());

                pl.tutorial.userManager.logAction(player, StepType.UPGRADE, 1.0);
            } else if (e.getClick() == ClickType.RIGHT) {
                double count = 0.0;
                while (canAfford) {
                    count += 1.0;
                    if (userUpgrade.getLevel() >= userUpgrade.getMax_level()) {
                        break;
                    }

                    // Deduct cost
                    switch (userUpgrade.getCurrency()) {
                        case MONEY: user.setMoney(user.getMoney() - cost); break;
                        case GEM: user.setGem(user.getGem() - cost); break;
                        case PRESTIGE_POINT: user.setPrestigePoint(user.getPrestigePoint() - cost); break;
                    }

                    // Upgrade logic
                    userUpgrade.setLevel(userUpgrade.getLevel() + 1);
                    userUpgrade.setCost(cost * userUpgrade.getCost_multi());
                    userUpgrade.setMultiplier(userUpgrade.getMultiplier() + userUpgrade.getIncrement_multi());

                    cost = userUpgrade.getCost();
                    canAfford = switch (userUpgrade.getCurrency()) {
                        case MONEY -> user.getMoney() >= cost;
                        case GEM -> user.getGem() >= cost;
                        case PRESTIGE_POINT -> user.getPrestigePoint() >= cost;
                        default -> false;
                    };
                }
                pl.tutorial.userManager.logAction(player, StepType.UPGRADE, count);
            }



            /*
            // Update ALL slots in the GUI
            Inventory inv = e.getInventory();
            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack slotItem = inv.getItem(slot);
                if (slotItem == null || !slotItem.hasItemMeta()) continue;

                ItemMeta slotMeta = slotItem.getItemMeta();
                PersistentDataContainer slotPdc = slotMeta.getPersistentDataContainer();

                if (slotPdc.has(upgradeKey, PersistentDataType.STRING)) {
                    try {
                        Upgrade slotUpgrade = gson.fromJson(
                                slotPdc.get(upgradeKey, PersistentDataType.STRING),
                                Upgrade.class
                        );

                        // Find matching upgrade using ALL identifiers
                        for (Upgrade u : user.getUpgrades()) {
                            if (u.getMultiplied() == slotUpgrade.getMultiplied() &&
                                    u.getCurrency() == slotUpgrade.getCurrency() &&
                                    u.getBase_cost() == slotUpgrade.getBase_cost()) {

                                // Update display name
                                slotMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        u.getMultiplied().toString() + " &fupgrade &8[&7" + u.getLevel() + "&8]"
                                ));

                                // Update lore with placeholders
                                List<String> lore = new ArrayList<>();
                                lore.add("");
                                lore.add(ChatColor.translateAlternateColorCodes('&',
                                        "&e| &fLevel: &6" + ((u.getLevel() == u.getMax_level()) ? "&cMAXED" : u.getLevel())));
                                lore.add(ChatColor.translateAlternateColorCodes('&',
                                        "&e| &fMultiplier: &6x" + String.format("%,.1f", u.getMultiplier())));
                                lore.add("");
                                lore.add(ChatColor.translateAlternateColorCodes('&',
                                        "&e&lClick &fto &6{current} -> {next} &7({price})".replace("{current}", userUpgrade.getLevel() + "").replace("{next}", userUpgrade.getLevel()+1 + "").replace("{price}", pl.currencyFormatter.formatCurrency(userUpgrade.getCost()))));

                                slotMeta.setLore(lore);

                                // Update PDC
                                slotPdc.set(upgradeKey, PersistentDataType.STRING, gson.toJson(u));
                                slotItem.setItemMeta(slotMeta);
                                inv.setItem(slot, slotItem);
                                break;
                            }
                        }
                    } catch (JsonSyntaxException ignored) {}
                }
            }
            */

            // Force GUI refresh
            Inventory currentInv = player.getOpenInventory().getTopInventory();
            fillUpgradeItems(currentInv, player, userUpgrade.getCurrency());
            player.updateInventory();

            Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                    pl.localDataManager.updateOnlineUser(user)
            );

            Bukkit.getServer().getPluginManager().callEvent(new UpgradeEvent(player, user, templateUpgrade, userUpgrade));

            //player.updateInventory();

        } catch (JsonSyntaxException ex) {
            player.sendMessage(ChatColor.RED + "Error processing upgrade!");
            throw new JsonSyntaxException(ex);
        }
    }

    // on upgrade gui close
    /* @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        String title = e.getView().getTitle();
        if (title.equals(moneyUpgradeTitle) ||
                title.equals(gemUpgradeTitle) ||
                title.equals(prestigePointUpgradeTitle)) {
            Bukkit.getScheduler().runTaskLater(pl, () ->
                    openClickerGui((Player) e.getPlayer()), 1);
        }
    }*/
}
