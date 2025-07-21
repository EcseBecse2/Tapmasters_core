package org.egyse.tapmasters_core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.jetbrains.annotations.NotNull;

public class GGEventCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p) {
            if (!p.isOp()) {
                pl.sendMessage(commandSender, pl.getConfig().getString("messages.no-perm"), true);
                return true;
            }
        }

        if (strings.length == 1) {
            if (strings[0].equalsIgnoreCase("start")) {
                pl.ggEventUtil.startEvent();
                pl.sendMessage(commandSender, "&aSuccessfully started GGEvent!", true);
            } else {
                for (String line : pl.getConfig().getStringList("messages.help-admin")) {
                    pl.sendMessage(commandSender, line, false);
                }
            }
        } else {
            for (String line : pl.getConfig().getStringList("messages.help-admin")) {
                pl.sendMessage(commandSender, line, false);
            }
        }

        return true;
    }
}
