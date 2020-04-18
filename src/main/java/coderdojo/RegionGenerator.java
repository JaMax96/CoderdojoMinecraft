package coderdojo;

import com.sk89q.worldedit.math.BlockVector3;

public class RegionGenerator {

    private static final int START_X = -450;
    private static final int START_Z = -450;
    private static final int END_X = 450;
    private static final int END_Z = 450;
    private static final int PLOT_SIZE = 20;
    private static final int PLOT_DISTANCE = 8;

    private int currentX = START_X;
    private int currentZ = START_Z;

    public BlockVector3[] nextRegion() {
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

        return region;
    }
}
