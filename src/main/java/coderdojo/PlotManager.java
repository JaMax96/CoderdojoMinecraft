package coderdojo;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlotManager {

    private final RegionGenerator plotGenerator;
    private Logger logger;

    public PlotManager(DataService dataService, Logger logger) {
        this.plotGenerator = new RegionGenerator(0, 0, 20000, 20000, "plotGenerator", dataService);
        this.logger = logger;
    }

    public void playerJoined(Player player) {
        if (!getRegionManager(player).hasRegion(getPlotName(player))) {
            ProtectedRegion plot = createPlot(player);
            getRegionManager(player).addRegion(plot);
            Utils.teleportPlayerToCoords(player, plot);
        }
    }

    private ProtectedRegion createPlot(Player player) {
        ProtectedRegion region = createRegion(player);
        getRegionManager(player).addRegion(region);
        resetPlot(player.getWorld(), region);
        Utils.setupBarrier(player.getWorld(), region);
        return region;
    }

    private RegionManager getRegionManager(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(player.getWorld()));
    }

    private void resetPlot(World world, ProtectedRegion region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .maxBlocks(-1)
                .world(BukkitAdapter.adapt(world)).build()) {
//            editSession.setBlocks(new CuboidRegion(
//                            BlockVector3.at(min.getBlockX(), 0, min.getBlockZ()),
//                            BlockVector3.at(max.getBlockX(), 0, max.getBlockZ())),
//                    BlockTypes.BEDROCK.getDefaultState());
//            editSession.setBlocks(new CuboidRegion(
//                            BlockVector3.at(min.getBlockX(), 1, min.getBlockZ()),
//                            BlockVector3.at(max.getBlockX(), 19, max.getBlockZ())),
//                    BlockTypes.DIRT.getDefaultState());
            editSession.setBlocks(new CuboidRegion(
                            BlockVector3.at(min.getBlockX(), 20, min.getBlockZ()),
                            BlockVector3.at(max.getBlockX(), 20, max.getBlockZ())),
                    BlockTypes.IRON_BLOCK.getDefaultState());
//            editSession.setBlocks(new CuboidRegion(
//                            BlockVector3.at(min.getBlockX(), 21, min.getBlockZ()),
//                            BlockVector3.at(max.getBlockX(), world.getMaxHeight(), max.getBlockZ())),
//                    BlockTypes.AIR.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            logger.log(Level.WARNING, "too many blocks changed for reset", e);
        }
    }

    private ProtectedRegion createRegion(Player player) {
        BlockVector3[] regionVectors = plotGenerator.nextRegion();
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(getPlotName(player), false, regionVectors[0], regionVectors[1]);
        region.getOwners().addPlayer(WorldGuardPlugin.inst().wrapPlayer(player));
        region.setFlag(Flags.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
        return region;
    }

    private String getPlotName(Player player) {
        return "playerplot-" + player.getUniqueId().toString();
    }

    public void sendHome(Player player) {
        Utils.teleportPlayerToCoords(player, getPlot(player));
    }

    public ProtectedRegion getPlot(Player player) {
        return getRegionManager(player).getRegion(getPlotName(player));
    }

    public void resetPlot(Player player) {
        player.sendMessage("Sorry, reset is currently disabled because the plot is too big");
//        resetPlot(player.getWorld(), getPlot(player));
    }

}
