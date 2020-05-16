package coderdojo;

import com.sk89q.worldedit.math.BlockVector3;

public class RegionGenerator {

    private static final int START_X = -110;
    private static final int START_Z = -450;
    private static final int END_X = 110;
    private static final int END_Z = 450;
    private static final int PLOT_SIZE = 30;
    private static final int PLOT_DISTANCE = 8;
    public static final String REGION_GENERATOR = "regionGenerator";

    private DataService dataService;
    private RegionGeneratorData data;

    public RegionGenerator(DataService dataService) {
        this.dataService = dataService;
        data = dataService.load(REGION_GENERATOR);
        if (data == null) {
            data = new RegionGeneratorData(START_X, START_Z);
        }
    }

    public BlockVector3[] nextRegion() {
        int currentX = data.x;
        int currentZ = data.z;
        BlockVector3[] region = {
                BlockVector3.at(currentX, 0, currentZ), BlockVector3.at(currentX + PLOT_SIZE, 255, currentZ + PLOT_SIZE)
        };
        currentX += PLOT_SIZE + PLOT_DISTANCE + 1;
        if (currentX + PLOT_SIZE > END_X) {
            currentX = START_X;
            currentZ += PLOT_SIZE + PLOT_DISTANCE + 1;
            if (currentZ + PLOT_SIZE > END_Z) {
                throw new IllegalStateException("OUT OF SPACE, AAAAAH! (also i know, the last plot is never handed out, whatever)");
            }
        }

        data.x = currentX;
        data.z = currentZ;
        dataService.save(REGION_GENERATOR, data);
        return region;
    }
}
