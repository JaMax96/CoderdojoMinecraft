package coderdojo;

import com.sk89q.worldedit.math.BlockVector3;

public class RegionGenerator {

    private static final int PLOT_SIZE = 30;
    private static final int PLOT_DISTANCE = 8;

    private final int startX;
    private final int endX;
    private final int endZ;
    private final String dataServiceName;
    private final int directionX;
    private final int directionZ;

    private final DataService dataService;
    private RegionGeneratorData data;

    public RegionGenerator(int startX, int startZ, int endX, int endZ, String dataServiceName, DataService dataService) {
        this.startX = startX;
        this.endX = endX;
        this.endZ = endZ;
        this.dataServiceName = dataServiceName;
        this.dataService = dataService;

        this.directionX = endX > startX ? 1 : -1;
        this.directionZ = endZ > startZ ? 1 : -1;

        data = dataService.load(dataServiceName);
        if (data == null) {
            data = new RegionGeneratorData(startX, startZ);
        }
    }

    public BlockVector3[] nextRegion() {
        int currentX = data.x;
        int currentZ = data.z;
        BlockVector3[] region = {
                BlockVector3.at(currentX, 0, currentZ), BlockVector3.at(currentX + (PLOT_SIZE * directionX), 255, currentZ + (PLOT_SIZE * directionZ))
        };
        currentX += ((PLOT_SIZE + PLOT_DISTANCE + 1) * directionX);
        if (isOverflown(currentX, directionX, endX)) {
            currentX = startX;
            currentZ += ((PLOT_SIZE + PLOT_DISTANCE + 1) * directionZ);
            if (isOverflown(currentZ, directionZ, endZ)) {
                throw new IllegalStateException("OUT OF SPACE, AAAAAH! (also i know, the last plot is never handed out, whatever)");
            }
        }

        data.x = currentX;
        data.z = currentZ;
        dataService.save(dataServiceName, data);
        return region;
    }

    private boolean isOverflown(int current, int direction, int end) {
        if (direction == 1) {
            return current + PLOT_SIZE > end;
        } else {
            return current - PLOT_SIZE < end;
        }
    }
}
