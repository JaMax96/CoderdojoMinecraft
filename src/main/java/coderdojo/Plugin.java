package coderdojo;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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
        initGlobalRegion();
        plotManager = new PlotManager();
        getServer().getPluginManager().registerEvents(new JoinListener(plotManager), this);
    }

    private void initGlobalRegion() {
        Bukkit.getWorlds().forEach(world -> {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if(!regions.hasRegion("global" + world.getName())){
                ProtectedCuboidRegion region = new ProtectedCuboidRegion("global" + world.getName(), false, BlockVector3.at(-500, 0, -500), BlockVector3.at(500, 255, 500));
                region.setFlag(Flags.BUILD, StateFlag.State.DENY);
                region.setFlag(Flags.POTION_SPLASH, StateFlag.State.DENY);
                region.setPriority(-1);

                regions.addRegion(region);
            }
        });
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
            world.setSpawnFlags(false, false);
            //TODO big enough?
            world.getWorldBorder().setSize(1000);
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
        if (command.getName().equals("reset")) {
            if (sender instanceof Player) {
                plotManager.resetPlot((Player) sender);
            }
        }
        return true;
    }

}