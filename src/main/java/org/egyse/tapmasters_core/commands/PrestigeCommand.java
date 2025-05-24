package org.egyse.tapmasters_core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.jetbrains.annotations.NotNull;

public class PrestigeCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player p)) {
            pl.sendMessage(commandSender, pl.getConfig().getString("messages.player-only"), true);
            return true;
        }

        pl.prestigeUtil.openGui(p);

        return true;
    }
}
