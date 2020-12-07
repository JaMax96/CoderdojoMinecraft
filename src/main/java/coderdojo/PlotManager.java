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

    private final RegionGenerator plotGenerator;

    public PlotManager(DataService dataService) {
        this.plotGenerator = new RegionGenerator(-110, -450, 110, 0, "plotGenerator", dataService);
    }

    public void playerJoined(Player player) {
        player.sendMessage("Dear " + player.getName() + ", welcome to our server!");
        if (!getRegionManager(player).hasRegion(getPlotName(player))) {
            ProtectedRegion plot = createPlot(player);
            getRegionManager(player).addRegion(plot);
            teleportPlayerToCoords(player, plot);
        }
    }

    private ProtectedRegion createPlot(Player player) {
        ProtectedRegion region = createRegion(player);
        getRegionManager(player).addRegion(region);
        resetPlot(player.getWorld(), region);
        setupBarrier(player.getWorld(), region);
        return region;
    }

    private RegionManager getRegionManager(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(player.getWorld()));
    }

    private void resetPlot(World world, ProtectedRegion region) {
        int x1 = region.getMinimumPoint().getX();
        int x2 = region.getMaximumPoint().getX();
        int z1 = region.getMinimumPoint().getZ();
        int z2 = region.getMaximumPoint().getZ();
        resetPlot(world, x1, x2, z1, z2);
    }

    private void resetPlot(World world, int x1, int x2, int z1, int z2) {
        int maxX = Math.max(x1, x2);
        int minX = Math.min(x1, x2);
        int maxZ = Math.max(z1, z2);
        int minZ = Math.min(z1, z2);
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

    private void setupBarrier(World world, ProtectedRegion region) {
        int maxX = Math.max(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int minX = Math.min(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int maxZ = Math.max(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        int minZ = Math.min(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        for (int y = 21; y < world.getMaxHeight(); y++) {
            for (int x = minX - 2; x <= maxX + 2; x++) {
                world.getBlockAt(x, y, minZ - 2).setType(Material.BARRIER);
                world.getBlockAt(x, y, maxZ + 2).setType(Material.BARRIER);
            }
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(minX - 2, y, z).setType(Material.BARRIER);
                world.getBlockAt(maxX + 2, y, z).setType(Material.BARRIER);
            }
        }
    }

    private void teleportPlayerToCoords(Player player, ProtectedRegion region) {
        BlockVector3 middle = region.getMinimumPoint().add(region.getMaximumPoint()).divide(2);
        teleportPlayerToCoords(player, middle.getX(), middle.getZ());
    }

    private void teleportPlayerToCoords(Player player, int x, int z) {
        World world = player.getWorld();
        int y = world.getMaxHeight();
        while (y > 0 && world.getBlockAt(x, y - 1, z).getType().equals(Material.AIR)) {
            y--;
        }
        player.teleport(new Location(world, x, y, z));
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
        teleportPlayerToCoords(player, getPlot(player));
    }

    private ProtectedRegion getPlot(Player player) {
        return getRegionManager(player).getRegion(getPlotName(player));
    }

    public void resetPlot(Player player) {
        resetPlot(player.getWorld(), getPlot(player));
    }

}
