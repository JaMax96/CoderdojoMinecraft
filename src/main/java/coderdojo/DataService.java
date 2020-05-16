package coderdojo;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataService {

    private File dataFolder;

    public DataService(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new RuntimeException("could not create data folder!");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T load(String dataName) {
        Map<String, Object> loadedData = loadData();
        if (loadedData == null) {
            return null;
        }
        return (T) loadedData.get(dataName);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadData() {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new FileInputStream(getRegionGeneratorDataFile()));
            Map<String, Object> data = (Map<String, Object>) in.readObject();
            in.close();
            return data;
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void save(String dataName, T data) {
        try {
            Map<String, Object> loadedData = loadData();
            if (loadedData == null) {
                loadedData = new HashMap<>();
            }
            loadedData.put(dataName, data);

            File regionGeneratorDataFile = getRegionGeneratorDataFile();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new FileOutputStream(regionGeneratorDataFile));
            out.writeObject(loadedData);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getRegionGeneratorDataFile() {
        return dataFolder.toPath().resolve("data.dat").toFile();
    }
}
