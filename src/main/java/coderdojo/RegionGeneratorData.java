package coderdojo;

import java.io.Serializable;

public class RegionGeneratorData implements Serializable {

    public int x, z;

    public RegionGeneratorData() {
        x = 0;
        z = 0;
    }

    public RegionGeneratorData(int startX, int startZ) {
        this.x = startX;
        this.z = startZ;
    }
}
