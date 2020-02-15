package playground.imagemarker.io;

import java.nio.file.Path;

/**
 * Created by holger on 15.02.2020.
 */
public interface IFileWatchNotifieable {
    public void fileCreated(Path file);
}
