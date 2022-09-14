package coderdojo;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeChangeCommand {
    private final PlotManager plotManager;

    public GameModeChangeCommand(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    public void spectator(Player player) {
        if(plotManager.isOnOwnPlot(player)){
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public void creative(Player player) {
        if(plotManager.isOnOwnPlot(player)){
            player.setGameMode(GameMode.CREATIVE);
        }
    }
}
