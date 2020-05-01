package coderdojo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WatchMe {

    private UUID currentPlayer = null;

    public void goWatch(Player sender) {
        if (currentPlayer != null) {
            Player player = Bukkit.getPlayer(currentPlayer);
            if (player != null && player.isOnline()) {
                sender.teleport(player);
                return;
            }
        }
        sender.sendMessage("Sorry, there is noone to watch");
    }

    public void watchMe(Player sender) {
        currentPlayer = sender.getUniqueId();
    }
}
