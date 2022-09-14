package coderdojo;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.logging.Level;

public class Plugin extends JavaPlugin {

    private PlotManager plotManager;
    private WatchMe watchMe;
    private GameModeChangeCommand gameModeChangeCommand;

    @Override
    public void onEnable() {
        DataService dataService = new DataService(getDataFolder());
        initSettings();
        initGlobalRegion();
        plotManager = new PlotManager(dataService, getLogger());
        initEventListeners();
        initGameModeHandler();
        watchMe = new WatchMe();
        initCommandCompleters();
        initWorldEditListener();
        gameModeChangeCommand = new GameModeChangeCommand(plotManager);
    }

    private void initWorldEditListener() {
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void onEditSessionEvent(EditSessionEvent event) {
        if (event.getActor() != null && event.getActor().isPlayer()) {
            Player player = Bukkit.getPlayer(event.getActor().getUniqueId());
            if (!player.isOp()) {
                ProtectedRegion plot = plotManager.getPlot(player);
                event.setExtent(new PlotLimitingExtent(event.getExtent(), plot.getMinimumPoint(), plot.getMaximumPoint()));
            }
        }
    }

    private void initCommandCompleters() {
        getCommand("go").setTabCompleter(new GoCommandCompleter());
        getCommand("repair").setTabCompleter(new RepairCommandCompleter());
    }

    private void initEventListeners() {
        getServer().getPluginManager().registerEvents(new JoinListener(plotManager), this);
        getServer().getPluginManager().registerEvents(new ExplodeListener(), this);
    }

    private void initGameModeHandler() {
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(GameModeFlagValueChangedHandler.FACTORY, null);
    }

    private void initGlobalRegion() {
        Bukkit.getWorlds().forEach(world -> {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            String globalName = "global" + world.getName();
            if (!regions.hasRegion(globalName)) {
                ProtectedCuboidRegion region = new ProtectedCuboidRegion(globalName, false, BlockVector3.at(-100000, -1000, -100000), BlockVector3.at(100000, 1000, 100000));
                region.setFlag(Flags.BUILD, StateFlag.State.DENY);
                region.setFlag(Flags.POTION_SPLASH, StateFlag.State.DENY);
                region.setPriority(-1);

                regions.addRegion(region);
            }
            String libraryName = "library" + world.getName();
            if (!regions.hasRegion(libraryName)) {
                ProtectedCuboidRegion region = new ProtectedCuboidRegion(libraryName, false, BlockVector3.at(-400, -1000, -400), BlockVector3.at(-200, 1000, -200));
                region.setPriority(-1);
                regions.addRegion(region);

                try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                        .maxBlocks(-1)
                        .world(BukkitAdapter.adapt(world)).build()) {
                    editSession.setBlocks(new CuboidRegion(
                                    BlockVector3.at(-400, 20, -400),
                                    BlockVector3.at(-200, 20, -200)),
                            BlockTypes.DIAMOND_BLOCK.getDefaultState());
                } catch (MaxChangedBlocksException e) {
                    getLogger().log(Level.WARNING, "too many blocks changed for reset", e);
                }
                Utils.setupBarrier(world, region);
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
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
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
        if (command.getName().equals("repair")) {
            if (sender instanceof Player) {
                plotManager.repairPlot((Player) sender);
            }
        }
        if (command.getName().equals("spectator")) {
            if (sender instanceof Player) {
                gameModeChangeCommand.spectator((Player) sender);
            }
        }
        if (command.getName().equals("creative")) {
            if (sender instanceof Player) {
                gameModeChangeCommand.creative((Player) sender);
            }
        }
        if (command.getName().equals("nightvision")) {
            if (sender instanceof Player) {
                ((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 5, 1));
            }
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
                    Utils.teleportPlayerToCoords(player, -300, -300);
                    break;
                case "plot":
                    if (args.length < 2) {
                        return false;
                    }
                    UUID playerUUID = findUUIDToPlayerName(args[1]);
                    plotManager.teleportToPlot(player, Bukkit.getOfflinePlayer(playerUUID));
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    private UUID findUUIDToPlayerName(String playerName) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equals(playerName)) {
                return player.getUniqueId();
            }
        }
        throw new IllegalStateException("did not find UUID to player: " + playerName);
    }

}