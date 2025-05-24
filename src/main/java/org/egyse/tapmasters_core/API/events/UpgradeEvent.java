package org.egyse.tapmasters_core.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.egyse.tapmasters_core.models.Upgrade;
import org.egyse.tapmasters_core.models.User;
import org.jetbrains.annotations.NotNull;

public class UpgradeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final User user;
    private final Upgrade oldUpgrade;
    private final Upgrade newUpgrade;

    public UpgradeEvent(Player player, User user, Upgrade oldUpgrade, Upgrade newUpgrade) {
        this.player = player;
        this.user = user;
        this.oldUpgrade = oldUpgrade;
        this.newUpgrade = newUpgrade;
    }

    // ======== Standard Methods ========
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
    }

    public Upgrade getOldUpgrade() {
        return oldUpgrade;
    }

    public Upgrade getNewUpgrade() {
        return newUpgrade;
    }
}
