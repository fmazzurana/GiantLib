package commons;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Utils {
	
	//-----------------------------------------------------------------------------------------------------
	// Time
	//-----------------------------------------------------------------------------------------------------
	public static String elapsedTime(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		final long hr = TimeUnit.MILLISECONDS.toHours(elapsedTime);
        final long min = TimeUnit.MILLISECONDS.toMinutes(elapsedTime - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(elapsedTime - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(elapsedTime - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Strings 
	//-----------------------------------------------------------------------------------------------------
	public static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}
	
	public static String stringOfChar(char c, int n) {
		return new String(new char[n]).replace('\0', c);
	}
	  
	//-----------------------------------------------------------------------------------------------------
	// Directory & Files 
	//-----------------------------------------------------------------------------------------------------
	public static File isValidDir(String parent, String dir, boolean create) {
		if (isEmptyString(dir))
			return null;
		if (!isEmptyString(parent))
			parent += "/";
		File path = new File(parent+dir);
		if (path.exists()) {
			return path.isDirectory() ? path : null;
		} else if (create == true) {
			path.mkdirs();
			return path;
		} else
			return null;
	}
	
	public static String getFileExtension(File file) {
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        return pos > 0 ? fileName.substring(pos+1) : "";
    }

	/**
	 * This method reads a file into a string.
	 * 
	 * @param path Pathname of the file to be read
	 * @return String containing all the file
	 * @throws MyException 
	 */
	public static String readFile(String path) throws MyException { 
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			throw new MyException(e.getMessage());
		}
	}
}
