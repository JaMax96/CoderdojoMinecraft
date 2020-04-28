package coderdojo;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;

public class DataService {

    private File dataFolder;

    public DataService(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new RuntimeException("could not create data folder!");
        }
    }

    public RegionGeneratorData loadRegionGeneratorData() {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new FileInputStream(getRegionGeneratorDataFile()));
            RegionGeneratorData data = (RegionGeneratorData) in.readObject();
            in.close();
            return data;
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveRegionGeneratorData(RegionGeneratorData data) {
        try {
            File regionGeneratorDataFile = getRegionGeneratorDataFile();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new FileOutputStream(regionGeneratorDataFile));
            out.writeObject(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getRegionGeneratorDataFile() {
        return dataFolder.toPath().resolve("regionGeneratorData.yml").toFile();
    }
}
