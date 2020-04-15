package coderdojo;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class PlotManager {
    
    private AtomicInteger counter = new AtomicInteger(0);

    public void playerJoined(Player player) {
        player.sendMessage("Dear " + player.getName() + ", welcome to our server!");

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        regions.addRegion(createRegion(player));
    }

    private ProtectedRegion createRegion(Player player) {
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("playerplot" + counter.getAndIncrement(), true, BlockVector3.at(-10, 0, -10), BlockVector3.at(10, 10, 10));
//        region.getOwners().addPlayer(WorldGuardPlugin.inst().wrapPlayer(player));
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
