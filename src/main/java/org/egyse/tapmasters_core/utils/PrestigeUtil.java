package org.egyse.tapmasters_core.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.egyse.tapmasters_core.API.events.PrestigeEvent;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Currency;
import org.egyse.tapmasters_core.models.Upgrade;
import org.egyse.tapmasters_core.models.User;
import org.egyse.tapmasters_tutorial.models.StepType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PrestigeUtil implements Listener {

    private final Tapmasters_core pl = Tapmasters_core.getInstance();
    private HashMap<UUID, Long> cd = new HashMap<>();

    public PrestigeUtil() {
        defineGuiItems();
        defineGui();
    }

    NamespacedKey prestigeItem = new NamespacedKey(pl, "prestigeItem");

    ItemStack guiPrestigeItem;
    ItemStack guiFillItem;

    double price = 1000000000000D;

    double getPrice(User user) {
        double price = this.price;

        for (int i = 0; i < user.getPrestige(); i++) {
            price = price * 1000;
        }

        return price;
    }

    double calcPoints(User user) {
        return Math.pow(user.getClick() / getPrice(user), 0.75);
    }

    private void defineGuiItems() {
        guiPrestigeItem = new ItemStack(Material.EMERALD);
        ItemMeta meta = guiPrestigeItem.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&1&lPRESTIGE"));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6&lThis will"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fReset &cEVERYTHING &fexcept your"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fArmors"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fPets"));
        //lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fWorkers"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fRaw Clicks"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fTokens"));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&6&lYou will"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fGet &c1 &6&lPrestige"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&e| &fGet &c{points} &1&lPrestige Points"));
        meta.setLore(lore);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(prestigeItem, PersistentDataType.BOOLEAN, true);

        guiPrestigeItem.setItemMeta(meta);
        guiFillItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    }

    Inventory mainGui;
    String mainGuiTitle = ChatColor.translateAlternateColorCodes('&', "&1PRESTIGE");
    int prestigeSlot = 13;

    private void defineGui() {
        // Main gui
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 3*9; i++) slots.add(i);

        mainGui = Bukkit.createInventory(null, 3*9, mainGuiTitle);

        slots.remove(Integer.valueOf(prestigeSlot));
        for (int slot : slots) mainGui.setItem(slot, guiFillItem);
    }

    public void openGui(Player p) {
        if (cd.containsKey(p.getUniqueId())) {
            long c = System.currentTimeMillis();
            if (c-cd.get(p.getUniqueId()) < 3000) {
                pl.sendMessage(p, "&cWait a moment before using this again!", true);
                return;
            }
        }
        cd.put(p.getUniqueId(), System.currentTimeMillis());
        Inventory inv = pl.cloneInv(mainGui, mainGuiTitle);
        User user = pl.localDataManager.getOnlineUser(p.getUniqueId());

        ItemStack item = guiPrestigeItem.clone();

        boolean canAfford = user.getClick() > getPrice(user);
        if (canAfford) {
            List<String> lore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            for (String line : meta.getLore()) {
                lore.add(line.replace("{points}", pl.currencyFormatter.formatCurrency(calcPoints(user))));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            List<String> lore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&cNot enough clicks!"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        inv.setItem(prestigeSlot, item);

        p.openInventory(inv);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        String title = e.getView().getTitle();
        boolean isOurGUI = title.equals(mainGuiTitle);
        if (!isOurGUI) return;

        e.setCancelled(true);

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(prestigeItem, PersistentDataType.BOOLEAN)) return;

        User user = pl.localDataManager.getOnlineUser(p.getUniqueId());

        boolean canAfford = user.getClick() > getPrice(user);
        if (!canAfford) {
            p.sendMessage(ChatColor.RED + "Not enough clicks! Needed: " + pl.currencyFormatter.formatCurrency(getPrice(user)));
            p.closeInventory();
            return;
        }

        PrestigeEvent event = new PrestigeEvent(p, user, calcPoints(user));

        new ArrayList<>(user.getUpgrades()).stream()
                .filter(u -> u.getMultiplied() == Currency.PRESTIGE_POINT)
                .forEach(u -> event.addPrestigePointMultiplier(u.getMultiplier()));

        new ArrayList<>(user.getBoosters()).stream()
                .filter(b -> b.getType() == Currency.PRESTIGE_POINT)
                .forEach(b -> event.addPrestigePointMultiplier(b.getMultiplier()));

        Bukkit.getPluginManager().callEvent(event);
        double points = event.calculatePrestigePointTotal();

        user.setClick(0);
        user.setMoney(0);
        user.setGem(0);
        List<Upgrade> newUpgrades = new ArrayList<>();
        for (Upgrade t : pl.default_upg_no_prestige) {
            newUpgrades.add(new Upgrade(t, true));
        }
        
        for (Upgrade upg : user.getUpgrades()) {
            if (upg.getCurrency() == Currency.PRESTIGE_POINT) newUpgrades.add(upg);
        }
        user.setUpgrades(newUpgrades);

        user.setPrestige(user.getPrestige() + 1);
        user.setPrestigePoint(user.getPrestigePoint() + points);
        pl.tutorial.userManager.logAction(p, StepType.PRESTIGE, 1.0);

        pl.localDataManager.updateOnlineUser(user);

        double prestige = user.getPrestige()-1;
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l" + p.getName() + " &fhas just &1prestiged&f! &7(&3" + (int) prestige + "&f->&3" + (int) user.getPrestige() + "&7)"));
        }
        pl.ggEventUtil.startEvent();

        p.closeInventory();
    }

}
