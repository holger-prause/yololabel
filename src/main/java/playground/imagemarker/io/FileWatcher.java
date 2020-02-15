package playground.imagemarker.io;

import playground.imagemarker.util.FileUtil;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by holger on 15.02.2020.
 */
public class FileWatcher extends Thread{
    private IFileWatchNotifieable[] notfiedAbles;
    private WatchService watchService;
    private Path watchDir;
    private WatchKey dirWachKey;
    private boolean notify;


    public FileWatcher(Path watchDir, boolean daemon, IFileWatchNotifieable ... notfiedAbles) {
        this.notfiedAbles = notfiedAbles;
        this.watchDir = watchDir;
        setDaemon(daemon);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            dirWachKey = watchDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        WatchKey key;
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE && notify) {
                        Path filePath = watchDir.resolve((Path) event.context());
                        if(FileUtil.isImage(filePath)) {
                            //time to finish image writing and text file
                            sleep(600);
                            for(IFileWatchNotifieable notifieable: notfiedAbles) {
                                notifieable.fileCreated(filePath);
                            }
                        }
                    }
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeDir(Path imageDirPath) {
        dirWachKey.cancel();
        try {
            this.watchDir = imageDirPath;
            dirWachKey = imageDirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
