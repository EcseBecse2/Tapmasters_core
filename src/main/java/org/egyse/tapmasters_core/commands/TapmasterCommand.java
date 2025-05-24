package org.egyse.tapmasters_core.commands;

import org.bukkit.Bukkit;
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

public class TapmasterCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 0) {
            for (String line : pl.getConfig().getStringList("messages.help")) pl.sendMessage(commandSender, line, false);
        } else if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("help")) {
                for (String line : pl.getConfig().getStringList("messages.help"))
                    pl.sendMessage(commandSender, line, false);
            } else if (strings[0].equalsIgnoreCase("admin")) {
                if (commandSender instanceof Player p) {
                    if (!p.isOp()) {
                        pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                        return true;
                    }
                }
                for (String line : pl.getConfig().getStringList("messages.help-admin"))
                    pl.sendMessage(commandSender, line, false);
            } else if (strings[0].equalsIgnoreCase("booster") || strings[0].equalsIgnoreCase("boosters")) {
                if (!(commandSender instanceof Player)) { pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-only"), true); return true; }
                User user = pl.localDataManager.getOnlineUser(((Player) commandSender).getUniqueId());
                if (user == null) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-found"), true);
                    return true;
                }

                StringBuilder boosters = new StringBuilder();
                List<Booster> b  = user.getBoosters();
                for (int i = 0; i < b.size(); i++) {
                    Booster booster = b.get(i);
                    String line = "{type}&7({multi}) &6- &f{time}"
                            .replace("{type}", booster.getType().toString())
                            .replace("{multi}", String.format("%,.2f", booster.getMultiplier()))
                            .replace("{time}", pl.timerUtil.formatTime(booster.getTimer()));

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
                    global = "{type}&7({multi}) &6- &f{time}"
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
            }else { for (String line : pl.getConfig().getStringList("messages.help")) pl.sendMessage(commandSender, line, false); }
        } else if (strings.length == 3) {

            if (!strings[0].equalsIgnoreCase("admin")) { for (String line : pl.getConfig().getStringList("messages.help")) pl.sendMessage(commandSender, line, false); return true; }
            if (commandSender instanceof Player p) {
                if (!p.isOp()) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                    return true;
                }
            }

            if (!strings[1].equalsIgnoreCase("boosters") && !strings[1].equalsIgnoreCase("booster")) {
                for (String line : pl.getConfig().getStringList("messages.help-admin")) pl.sendMessage(commandSender, line, false);
                return true;
            }

            User user = pl.localDataManager.getOnlineUser(Bukkit.getPlayer(strings[2]).getUniqueId());
            if (user == null) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-found"), true);
                return true;
            }

            StringBuilder boosters = new StringBuilder();
            List<Booster> b  = user.getBoosters();
            for (int i = 0; i < b.size(); i++) {
                Booster booster = b.get(i);
                String line = "{type}&7({multi}) &6- &f{time}"
                        .replace("{type}", booster.getType().toString())
                        .replace("{multi}", String.format("%,.2f", booster.getMultiplier()))
                        .replace("{time}", pl.timerUtil.formatTime(booster.getTimer()));

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
                global = "{type}&7({multi}) &6- &f{time}"
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

        } else if (strings.length == 5) {

            if (!strings[0].equalsIgnoreCase("admin")) { for (String line : pl.getConfig().getStringList("messages.help")) pl.sendMessage(commandSender, line, false); return true; }
            if (commandSender instanceof Player p) {
                if (!p.isOp()) {
                    pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                    return true;
                }
            }

            if (!strings[1].equalsIgnoreCase("give") && !strings[1].equalsIgnoreCase("take") && !strings[1].equalsIgnoreCase("set")) {
                for (String line : pl.getConfig().getStringList("messages.help-admin")) pl.sendMessage(commandSender, line, false);
                return true;
            }

            User user = null;
            if (Bukkit.getPlayer(strings[2]) != null) { user = pl.localDataManager.getOnlineUser(Bukkit.getPlayer(strings[2]).getUniqueId()); }

            if (user == null) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-online"), true);
                return true;
            }

            Currency currency = null;
            try {
                currency = Currency.valueOf(strings[3].toUpperCase());
            } catch (Exception e) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-currency"), true);
                return true;
            }

            if (currency != Currency.CLICK && currency != Currency.MONEY && currency != Currency.GEM && currency != Currency.TOKEN && currency != Currency.PRESTIGE_POINT) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-currency"), true);
                return true;
            }

            double num = Double.parseDouble(strings[4]);
            if (num < 0) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.invalid-number"), true);
                return true;
            }

            switch (strings[1].toLowerCase()) {
                case "give" -> {
                    switch (currency) {
                        case CLICK -> {
                            double newValue = user.getClick() + num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setClick(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-give")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case MONEY -> {
                            double newValue = user.getMoney() + num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setMoney(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-give")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case GEM -> {
                            double newValue = user.getGem() + num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setGem(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-give")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case TOKEN -> {
                            double newValue = user.getToken() + num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setToken(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-give")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case PRESTIGE_POINT -> {
                            double newValue = user.getPrestigePoint() + num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setPrestigePoint(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-give")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                    }
                }
                case "take" -> {
                    switch (currency) {
                        case CLICK -> {
                            double newValue = user.getClick() - num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setClick(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-take")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case MONEY -> {
                            double newValue = user.getMoney() - num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setMoney(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-take")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case GEM -> {
                            double newValue = user.getGem() - num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setGem(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-take")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case TOKEN -> {
                            double newValue = user.getToken() - num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setToken(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-take")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case PRESTIGE_POINT -> {
                            double newValue = user.getPrestigePoint() - num;
                            if (newValue < 0 || newValue > Double.MAX_VALUE) {
                                pl.sendMessage(commandSender, pl.getConfig().getString("messages.wrong-number"), true);
                                return true;
                            }

                            user.setPrestigePoint(newValue);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-take")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                    }
                }
                case "set" -> {
                    switch (currency) {
                        case CLICK -> {
                            user.setClick(num);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-set")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case MONEY -> {
                            user.setMoney(num);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-set")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case GEM -> {
                            user.setGem(num);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-set")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case TOKEN -> {
                            user.setToken(num);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-set")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                        case PRESTIGE_POINT -> {
                            user.setPrestigePoint(num);

                            pl.sendMessage(commandSender, pl.getConfig().getString("messages.admin-set")
                                            .replace("{player}", user.getName())
                                            .replace("{num}", pl.currencyFormatter.formatCurrency(num))
                                            .replace("{currency}", currency.toString())
                                    , true);
                        }
                    }
                }
            }

            pl.localDataManager.updateOnlineUser(user);
        } else { for (String line : pl.getConfig().getStringList("messages.help")) pl.sendMessage(commandSender, line, false); }

        return true;
    }
}
