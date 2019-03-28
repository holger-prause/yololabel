package playground.imagemarker.io;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Holger on 20.04.2018.
 */
public class Config {
    private static final String LAST_DIR_KEY = "last.dir";
    String labelFileName = "predefined_labels.txt";
    Properties config = new Properties();
    Path labelFile;
    Path configFile;

    public Config() {
        try {
            Path userHome = Paths.get(System.getProperty("user.home"));
            Path configFolderPath = userHome;
            configFolderPath = configFolderPath.resolve("yololabel");
            labelFile = configFolderPath.resolve(labelFileName);
            configFile = configFolderPath.resolve("config.properties");

            //initialize config
            if(!Files.exists(configFolderPath)) {
                try {
                    Files.createDirectory(configFolderPath);
                    URI lblTemplateUri = this.getClass().getResource("/"+labelFileName).toURI();
                    List<String> labels
                            = Files.readAllLines(Paths.get(lblTemplateUri), Charset.forName("UTF-8"));
                    Files.write(labelFile, labels);

                    config.put(LAST_DIR_KEY, userHome.toAbsolutePath().toString());
                    config.store(new FileOutputStream(configFile.toFile()), "last dir");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                config.load(new FileInputStream(configFile.toFile()));
                Files.readAllLines(labelFile, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getLabelFile() {
        return labelFile;
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
            config.store(new FileOutputStream(configFile.toFile()), "last dir");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
