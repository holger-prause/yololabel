package playground.imagemarker.io;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Holger on 20.04.2018.
 */
public class Config {
    private static final String LAST_DIR_KEY = "last.dir";
    File configFile = new File("config.properties");
    Properties config = new Properties();

    public Config() {
        try {
            if (configFile.exists()) {
                config.load(new FileInputStream(configFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<File> getLastDir() {
        String path = config.getProperty(LAST_DIR_KEY);
        if(path != null) {
            return Optional.of(new File(path));
        }

        return Optional.empty();
    }

    public void setLastDir(File lastDir) {
        config.put(LAST_DIR_KEY, lastDir.getAbsolutePath());
        try {
            config.store(new FileOutputStream(configFile), "last dir");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
