package coderdojo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private PlotManager plotManager;

    public JoinListener(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plotManager.playerJoined(event.getPlayer());
    }
}
