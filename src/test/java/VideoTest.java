import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Holger on 25.04.2018.
 */
public class VideoTest {

    @Test
    public void testVideo() {
        //System.load("C:\\development\\opencv\\build\\bin\\opencv_ffmpeg341_64.dll");

        boolean is64Bit = System.getProperty("os.arch").indexOf("64") != -1;
        final Path cvLib;
        final Path cvFfmpegLib;
        Path lib = Paths.get("").resolve("lib").resolve("opencv");
        if (is64Bit) {
            cvFfmpegLib = lib.resolve("x64").resolve("opencv_ffmpeg341_64.dll");
            cvLib = lib.resolve("x64").resolve("opencv_java341.dll");
        } else {
            cvFfmpegLib = lib.resolve("x86").resolve("opencv_ffmpeg341.dll");
            cvLib = lib.resolve("x86").resolve("opencv_java341.dll");
        }


        System.err.println(is64Bit);


    }
}
