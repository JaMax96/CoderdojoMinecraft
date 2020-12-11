package coderdojo;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {

    public static void teleportPlayerToCoords(Player player, ProtectedRegion region) {
        BlockVector3 middle = region.getMinimumPoint().add(region.getMaximumPoint()).divide(2);
        teleportPlayerToCoords(player, middle.getX(), middle.getZ());
    }

    public static void teleportPlayerToCoords(Player player, int x, int z) {
        World world = player.getWorld();
        int y = world.getMaxHeight();
        while (y > 0 && world.getBlockAt(x, y - 1, z).getType().equals(Material.AIR)) {
            y--;
        }
        player.teleport(new Location(world, x, y, z));
    }

    public static void setupBarrier(World world, ProtectedRegion region) {
        int maxX = Math.max(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int minX = Math.min(region.getMinimumPoint().getX(), region.getMaximumPoint().getX());
        int maxZ = Math.max(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        int minZ = Math.min(region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ());
        for (int y = 21; y < world.getMaxHeight(); y++) {
            for (int x = minX - 1; x <= maxX + 1; x++) {
                world.getBlockAt(x, y, minZ - 1).setType(Material.STRUCTURE_VOID);
                world.getBlockAt(x, y, maxZ + 1).setType(Material.STRUCTURE_VOID);
            }
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(minX - 1, y, z).setType(Material.STRUCTURE_VOID);
                world.getBlockAt(maxX + 1, y, z).setType(Material.STRUCTURE_VOID);
            }
        }
    }
}
