package org.egyse.tapmasters_core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

public class AccountCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length > 1) {
            pl.sendMessage(commandSender, pl.getConfig().getString("messages.help"), false);
            return true;
        }

        if (strings.length == 0) {

            if (commandSender instanceof Player p) {
                User user = pl.localDataManager.getOnlineUser(p.getUniqueId());

                for (String line : pl.getConfig().getStringList("messages.profile")) {
                    pl.sendMessage(
                            commandSender,
                            line
                                    .replace("{player}", user.getName())
                                    .replace("{clicks}", pl.currencyFormatter.formatCurrency(user.getClick()))
                                    .replace("{raw_clicks}", "" + user.getRawClick())
                                    .replace("{money}", pl.currencyFormatter.formatCurrency(user.getMoney()))
                                    .replace("{gem}", pl.currencyFormatter.formatCurrency(user.getGem()))
                                    .replace("{token}", pl.currencyFormatter.formatCurrency(user.getToken()))
                                    .replace("{prestige}", "" + user.getPrestige())
                                    .replace("{prestige_point}", pl.currencyFormatter.formatCurrency(user.getPrestigePoint()))
                            ,
                            false
                    );
                }

            } else {
                pl.sendMessage(commandSender, pl.getConfig().getString("player-only"), true);
            }
            return true;

        }

        User user = null;
        if (Bukkit.getPlayer(strings[0]) != null) { user = pl.localDataManager.getOnlineUser(Bukkit.getPlayer(strings[0]).getUniqueId()); }

        if (user == null) {
            pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-not-online"), true);
            return true;
        }

        for (String line : pl.getConfig().getStringList("messages.profile")) {
            pl.sendMessage(
                    commandSender,
                    line
                            .replace("{player}", user.getName())
                            .replace("{clicks}", pl.currencyFormatter.formatCurrency(user.getClick()))
                            .replace("{raw_clicks}", "" + user.getRawClick())
                            .replace("{money}", pl.currencyFormatter.formatCurrency(user.getMoney()))
                            .replace("{gem}", pl.currencyFormatter.formatCurrency(user.getGem()))
                            .replace("{token}", pl.currencyFormatter.formatCurrency(user.getToken()))
                            .replace("{prestige}", "" + user.getPrestige())
                            .replace("{prestige_point}", pl.currencyFormatter.formatCurrency(user.getPrestigePoint()))
                    ,
                    false
            );
        }

        return true;
    }
}
