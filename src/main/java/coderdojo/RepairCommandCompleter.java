package coderdojo;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepairCommandCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return getAllArguments(args)
                .stream()
                .filter(it -> it.startsWith(args[args.length - 1]))
                .collect(Collectors.toList());
    }

    private List<String> getAllArguments(String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return Collections.emptyList();
    }
}
