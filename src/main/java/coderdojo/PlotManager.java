package coderdojo;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlotManager {

    private RegionGenerator regionGenerator = new RegionGenerator();

    public void playerJoined(Player player) {
        player.sendMessage("Dear " + player.getName() + ", welcome to our server!");
        if (!getRegionManager(player).hasRegion(getPlotName(player))) {
            ProtectedRegion plot = createPlot(player);
            getRegionManager(player).addRegion(plot);
            teleportPlayerToPlot(player, plot);
        }
    }

    private ProtectedRegion createPlot(Player player) {
        ProtectedRegion region = createRegion(player);
        getRegionManager(player).addRegion(region);
        resetPlot(player.getWorld(), region);
        return region;
    }

    private RegionManager getRegionManager(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(player.getWorld()));
    }

    private void resetPlot(World world, ProtectedRegion region) {
        int maxX = Math.max(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int minX = Math.min(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int maxZ = Math.max(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        int minZ = Math.min(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, 0, z).setType(Material.BEDROCK);
                for (int y = 1; y < 20; y++) {
                    world.getBlockAt(x, y, z).setType(Material.DIRT);
                }
                world.getBlockAt(x, 20, z).setType(Material.IRON_BLOCK);
                for (int y = 21; y < world.getMaxHeight(); y++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    private void teleportPlayerToPlot(Player player, ProtectedRegion region) {
        BlockVector3 middle = region.getMinimumPoint().add(region.getMaximumPoint()).divide(2);
        World world = player.getWorld();
        int y = 22;
        while (y < world.getMaxHeight()
                && !(world.getBlockAt(middle.getX(), y - 1, middle.getBlockZ()).getType().equals(Material.AIR)
                && world.getBlockAt(middle.getX(), y, middle.getBlockZ()).getType().equals(Material.AIR))) {
            y++;
        }
        player.teleport(new Location(world, middle.getX(), y, middle.getZ()));
    }

    private ProtectedRegion createRegion(Player player) {
        BlockVector3[] regionVectors = regionGenerator.nextRegion();
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(getPlotName(player), false, regionVectors[0], regionVectors[1]);
        region.getOwners().addPlayer(WorldGuardPlugin.inst().wrapPlayer(player));
        region.setFlag(Flags.BUILD.getRegionGroupFlag(), RegionGroup.MEMBERS);
        return region;
    }

    private String getPlotName(Player player) {
        return "playerplot-" + player.getUniqueId().toString();
    }

    public void sendHome(Player player) {
        teleportPlayerToPlot(player, getPlot(player));
    }

    private ProtectedRegion getPlot(Player player) {
        return getRegionManager(player).getRegion(getPlotName(player));
    }

    public void resetPlot(Player player) {
        resetPlot(player.getWorld(), getPlot(player));
    }
}
