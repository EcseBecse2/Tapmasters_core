package org.egyse.tapmasters_core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.egyse.tapmasters_core.API.events.SellClickEvent;
import org.egyse.tapmasters_core.Tapmasters_core;
import org.egyse.tapmasters_core.models.Booster;
import org.egyse.tapmasters_core.models.Currency;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

public class SellCommand implements CommandExecutor {
    private final Tapmasters_core pl = Tapmasters_core.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            pl.sendMessage(commandSender, pl.getConfig().getString("player-only"), true);
            return true;
        }

        Player player = (Player) commandSender;
        User user = pl.localDataManager.getOnlineUser(player.getUniqueId());

        if (user == null) {
            pl.sendMessage(commandSender, pl.getConfig().getString("messages.plugin-problem"), true);
            return true;
        }

        if (user.getClick() < 10) {
            pl.sendMessage(commandSender, pl.getConfig().getString("messages.not-enough-clicks"), true);
            return true;
        }

        double click = user.getClick();
        SellClickEvent event = new SellClickEvent((Player) commandSender, click);

        user.getUpgrades().stream()
                .filter(u -> u.getMultiplied() == Currency.MONEY)
                .forEach(u -> event.addMoneyMultiplier(u.getMultiplier()));

        user.getBoosters().stream()
                .filter(b -> b.getType() == Currency.MONEY)
                .forEach(b -> event.addMoneyMultiplier(b.getMultiplier()));

        Booster globalBooster = pl.boosterUtil.getGlobalBooster();
        if(globalBooster != null && globalBooster.getType() == Currency.MONEY) {
            event.addMoneyMultiplier(globalBooster.getMultiplier());
        }

        Bukkit.getPluginManager().callEvent(event);

        double money = event.calculateMoneyTotal();
        user.setClick(0);
        user.setMoney(user.getMoney() + money);

        Bukkit.getScheduler().runTaskAsynchronously(pl, () ->
                pl.localDataManager.updateOnlineUser(user)
        );

        double multi = event.getMoneyMultipliers().stream()
                .reduce(1.0, (a, b) -> a * b);

        for (String line : pl.getConfig().getStringList("messages.sell-clicks")) {
            pl.sendMessage(player,
                    line
                            .replace("{click}", pl.currencyFormatter.formatCurrency(click))
                            .replace("{money}", pl.currencyFormatter.formatCurrency(money))
                            .replace("{multi}", pl.currencyFormatter.formatCurrency(multi))
                    ,
                    false);
        }

        return true;
    }
}
