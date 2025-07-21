package org.egyse.tapmasters_core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GGEvent {
    private boolean running;
    private List<UUID> players;

    public GGEvent() {
        this.running = false;
        this.players = new ArrayList<>();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }
}
