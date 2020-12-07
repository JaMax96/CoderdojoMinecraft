package coderdojo;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class Plugin extends JavaPlugin {

    private PlotManager plotManager;
    private WatchMe watchMe;

    @Override
    public void onEnable() {
        DataService dataService = new DataService(getDataFolder());
        initSettings();
        initGlobalRegion();
        plotManager = new PlotManager(dataService);
        initEventListeners();
        initGameModeHandler();
        watchMe = new WatchMe();
        initGoCommandCompleter();
    }

    private void initGoCommandCompleter() {
        getCommand("go").setTabCompleter((sender, command, alias, args) -> {
                    if (args.length > 1) {
                        return Collections.emptyList();
                    } else {
                        return Lists.newArrayList("home", "watch", "library");
                    }
                }
        );
    }

    private void initEventListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(plotManager), this);
        getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    }

    private void initGameModeHandler() {
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(GameModeHandler.FACTORY, null);
    }

    private void initGlobalRegion() {
        Bukkit.getWorlds().forEach(world -> {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if (!regions.hasRegion("global" + world.getName())) {
                ProtectedCuboidRegion region = new ProtectedCuboidRegion("global" + world.getName(), false, BlockVector3.at(-500, -1000, -500), BlockVector3.at(500, 1000, 500));
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
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("gohome")) {
            return handleGoCommand(sender, "home");
        }
        if (command.getName().equals("reset")) {
            if (sender instanceof Player) {
                plotManager.resetPlot((Player) sender);
            }
        }
        if (command.getName().equals("gowatch")) {
            return handleGoCommand(sender, "watch");
        }
        if (command.getName().equals("watchme")) {
            if (sender instanceof Player) {
                watchMe.watchMe((Player) sender);
            }
        }
        if (command.getName().equals("go")) {
            return handleGoCommand(sender, args);
        }
        return true;
    }

    private boolean handleGoCommand(CommandSender sender, String... args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args == null || args.length == 0 || args[0] == null) {
                return false;
            }
            switch (args[0]) {
                case "home":
                    plotManager.sendHome(player);
                    break;
                case "watch":
                    watchMe.goWatch(player);
                    break;
                case "library":
                    //nothing yet
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

}