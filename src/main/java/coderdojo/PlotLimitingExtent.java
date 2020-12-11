package coderdojo;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class PlotLimitingExtent extends AbstractDelegateExtent {
    private final CuboidRegion region;

    public PlotLimitingExtent(Extent extent, BlockVector3 min, BlockVector3 max) {
        super(extent);
        region = new CuboidRegion(min, max);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return region.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return region.getMaximumPoint();
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        if (region.contains(location)) {
            return super.setBlock(location, block);
        } else {
            return false;
        }
    }
}
