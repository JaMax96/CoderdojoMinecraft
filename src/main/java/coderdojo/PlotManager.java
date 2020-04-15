package coderdojo;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
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

import java.util.concurrent.atomic.AtomicInteger;

public class PlotManager {

    private AtomicInteger counter = new AtomicInteger(0);
    private RegionGenerator regionGenerator = new RegionGenerator();

    public void playerJoined(Player player) {
        player.sendMessage("Dear " + player.getName() + ", welcome to our server!");

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        ProtectedRegion region = createRegion(player);
        regions.addRegion(region);
        preparePlot(player.getWorld(), region);
        teleportPlayerToPlot(player, region);
    }

    private void preparePlot(World world, ProtectedRegion region) {
        int maxX = Math.max(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int minX = Math.min(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int maxZ = Math.max(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        int minZ = Math.min(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, 3, z).setType(Material.IRON_BLOCK);
            }
        }
    }

    private void teleportPlayerToPlot(Player player, ProtectedRegion region) {
        BlockVector3 middle = region.getMinimumPoint().add(region.getMaximumPoint()).divide(2);
        player.teleport(new Location(player.getWorld(), middle.getX(), 4, middle.getZ()));
    }

    private ProtectedRegion createRegion(Player player) {
        BlockVector3[] regionVectors = regionGenerator.nextRegion();
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("playerplot" + counter.getAndIncrement(), true, regionVectors[0], regionVectors[1]);
        region.getOwners().addPlayer(WorldGuardPlugin.inst().wrapPlayer(player));
        //TODO remove debug thingy
        region.setFlag(Flags.GREET_MESSAGE, "Hi there!");
        setFlags(region);
        return region;
    }

    private static final Flag<?>[] FLAGS = new Flag[]{
            Flags.BUILD,
    };

    private void setFlags(ProtectedCuboidRegion region) {
        for (Flag<?> flag : FLAGS) {
            region.setFlag(flag.getRegionGroupFlag(), RegionGroup.MEMBERS);
        }
    }

    public void sendHome(Player sender) {
        sender.sendMessage("TODO");
    }

    public void init() {

    }
}
