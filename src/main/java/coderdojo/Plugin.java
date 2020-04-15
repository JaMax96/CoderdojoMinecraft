package coderdojo;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private PlotManager plotManager;

    @Override
    public void onEnable() {
        initSettings();
        plotManager = new PlotManager();
        plotManager.init();
        getServer().getPluginManager().registerEvents(new JoinListener(plotManager), this);
    }

    private void initSettings() {
        Bukkit.setDefaultGameMode(GameMode.CREATIVE);
        Bukkit.getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setTime(5000);
            world.setStorm(false);
            world.setSpawnFlags(false,false);
            //TODO big enough?
            world.getWorldBorder().setSize(500);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        System.out.println("called with: " + label);
        if (command.getName().equals("gohome")) {
            if (sender instanceof Player) {
                plotManager.sendHome((Player) sender);
            }
        }
        return true;
    }

}