package playground.imagemarker.util;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FileUtil {
	
	private static List<String> imgExtensions = Arrays.asList("jpg", "jpeg", "png");
	public static boolean isImage(Path imgPath) {
		if(imgPath != null) {
			String fileName = imgPath.getFileName().toString();
			int extIdx = fileName.lastIndexOf('.');
			if (extIdx > 0) {
			    String extension = fileName.substring(extIdx+1).toLowerCase();
			    if(imgExtensions.contains(extension)) {
			    	return true;
			    }
			}
		}
		
		return false;
	}
	
	public static String parseBaseName(Path file) {
		String fileName = file.getFileName().toString();
		int extIdx = fileName.lastIndexOf('.');		
		if (extIdx > 0) {
			return fileName.substring(0, extIdx);
		} else {
			return null;
		}
	}

	public static Path getAnnotationPath(Path imgPath) {
        String annName = parseBaseName(imgPath) + ".txt";
        return imgPath.toAbsolutePath().getParent().resolve(annName);
    }
}
