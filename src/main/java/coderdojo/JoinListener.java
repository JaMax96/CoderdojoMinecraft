package coderdojo;

import org.bukkit.GameMode;
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
        //TODO maybe not? maybe global field?
        event.getPlayer().setGameMode(GameMode.CREATIVE);
        plotManager.playerJoined(event.getPlayer());
    }
}
