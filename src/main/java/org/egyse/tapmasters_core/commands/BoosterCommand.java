package org.egyse.tapmasters_core.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.Currency;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BoosterCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 0) {

            if (!(commandSender instanceof Player p)) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-only"), true);
                return true;
            }

            User user = pl.localDataManager.getOnlineUser(p.getUniqueId());

            StringBuilder boosters = new StringBuilder();
            List<Booster> b  = user.getBoosters();
            for (int i = 0; i < b.size(); i++) {
                Booster booster = b.get(i);
                String line = "{type} &7(&cx{multi}&7) &6- &f{time}"
                        .replace("{type}", booster.getType().toString())
                        .replace("{multi}", String.format("%,.2f", booster.getMultiplier()))
                        .replace("{time}", (b.get(i).getTimer() > 604800) ? "∞" : pl.timerUtil.formatTime(booster.getTimer()));

                if (i < b.size() - 1) {
                    line += "\n";
                }
                boosters.append(line);
            }

            if (b.isEmpty()) {
                boosters.append("&cNo active boosters");
            }

            String global = "&cNone";
            Booster globalb = pl.boosterUtil.getGlobalBooster();
            if (globalb != null) {
                global = "{type} &7(&cx{multi}&7) &6- &f{time}"
                        .replace("{type}", globalb.getType().toString())
                        .replace("{multi}", String.format("%,.2f", globalb.getMultiplier()))
                        .replace("{time}", pl.timerUtil.formatTime(globalb.getTimer()));
            }

            for (String line : pl.getConfig().getStringList("messages.boosters")) {
                pl.sendMessage(commandSender,
                        line
                                .replace("{boosters}", boosters.toString())
                                .replace("{player}", user.getName())
                                .replace("{global}", global),
                        false);
            }

        } else if (strings.length == 6) {

            if (commandSender instanceof Player p) {
                if (!p.isOp()) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                    return true;
                }
            }

            if (strings[0].equalsIgnoreCase("give")) {

                Player p = Bukkit.getPlayer(strings[1]);
                if (p == null) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-online"), true);
                    return true;
                }

                boolean global = false;
                if (strings[2].equalsIgnoreCase("global")) {
                    global = true;
                } else if (!strings[2].equalsIgnoreCase("personal")) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-booster-type"), true);
                    return true;
                }

                Currency currency = null;
                switch (strings[3].toLowerCase()) {
                    case "click" -> currency = Currency.CLICK;
                    case "money" -> currency = Currency.MONEY;
                    case "gem" -> currency = Currency.GEM;
                    case "token" -> currency = Currency.TOKEN;
                    case "prestige_point" -> currency = Currency.PRESTIGE_POINT;
                    default -> {
                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-currency"), true);
                        return true;
                    }
                }

                double multi = Double.parseDouble(strings[4]);
                if (multi > 1.0) {

                    int timer = Integer.parseInt(strings[5]);
                    if (timer > 0) {

                        Booster b = new Booster(timer, currency, multi, global);
                        pl.boosterUtil.giveItem(b, p);

                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.given-booster").replace("{currency}", currency.toString()).replace("{player}", p.getName()), true);
                        pl.sendMessage(p, pl.getConfig().getString("messages.got-booster").replace("{currency}", currency.toString()), true);

                    } else {
                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-timer"), true);
                        return true;
                    }

                } else {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-multiplier"), true);
                    return true;
                }

            } else {
                for (String line : pl.getConfig().getStringList("messages.help-admin")) {
                    pl.sendMessage(commandSender, line, false);
                }
            }

        } else if (strings.length == 4) {
            if (commandSender instanceof Player p) {
                if (!p.isOp()) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                    return true;
                }
            }

             if (strings[0].equalsIgnoreCase("start")) {

                Currency currency = null;
                switch (strings[1].toLowerCase()) {
                    case "click" -> currency = Currency.CLICK;
                    case "money" -> currency = Currency.MONEY;
                    case "gem" -> currency = Currency.GEM;
                    case "token" -> currency = Currency.TOKEN;
                    case "prestige_point" -> currency = Currency.PRESTIGE_POINT;
                    default -> {
                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-currency"), true);
                        return true;
                    }
                }

                double multi = Double.parseDouble(strings[2]);
                if (multi > 1.0) {

                    int timer = Integer.parseInt(strings[3]);
                    if (timer > 0) {

                        Booster b = new Booster(timer, currency, multi, true);
                        if (pl.boosterUtil.setGlobalBooster(b)) {
                            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                                pl.sendMessage(p, "&6{player} &factivated a &cx&f{multi} {type}&f booster for &b{time}&f."
                                                .replace("{player}", (commandSender instanceof Player) ? p.getName() : "CONSOLE")
                                                .replace("{multi}", b.getMultiplier() + "")
                                                .replace("{type}", b.getType().toString())
                                                .replace("{time}", pl.timerUtil.formatTime(b.getTimer()))
                                        , true);
                            }
                            pl.sendMessage(commandSender, "&aSuccessfully started global booster!", true);
                        } else {
                            pl.sendMessage(commandSender, ChatColor.translateAlternateColorCodes('&', "&cThere's already an active global booster."), true);
                        }

                    } else {
                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-timer"), true);
                        return true;
                    }

                } else {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-multiplier"), true);
                    return true;
                }
             } else if (strings[0].equalsIgnoreCase("giveinfinite") || strings[0].equalsIgnoreCase("giveinfinity")) {
                 Player p = Bukkit.getPlayer(strings[1]);
                 if (p == null) {
                     pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-online"), true);
                     return true;
                 }

                 Currency currency = null;
                 switch (strings[2].toLowerCase()) {
                     case "click" -> currency = Currency.CLICK;
                     case "money" -> currency = Currency.MONEY;
                     case "gem" -> currency = Currency.GEM;
                     case "token" -> currency = Currency.TOKEN;
                     case "prestige_point" -> currency = Currency.PRESTIGE_POINT;
                     default -> {
                         pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-currency"), true);
                         return true;
                     }
                 }

                 double multi = Double.parseDouble(strings[3]);
                 if (multi > 1.0) {

                     Booster b = new Booster(100000000, currency, multi, false);
                     pl.boosterUtil.addBooster(pl.localDataManager.getOnlineUser(p.getUniqueId()), b);

                     pl.sendMessage(p, pl.getConfig().getString("messages.got-infinite-booster"), true);

                 } else {
                     pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-multiplier"), true);
                     return true;
                 }
             }else {
                 for (String line : pl.getConfig().getStringList("messages.help-admin")) {
                     pl.sendMessage(commandSender, line, false);
                 }
             }
        }else {
            for (String line : pl.getConfig().getStringList("messages.help-admin")) {
                pl.sendMessage(commandSender, line, false);
            }
        }

        return true;
    }
}
