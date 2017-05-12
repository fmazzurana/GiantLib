package commons;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;

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

	public static String replaceDatePattern(String s, Date date) {
		SimpleDateFormat sdfAnno = new SimpleDateFormat("yyyy");    
		SimpleDateFormat sdfData = new SimpleDateFormat("yyyyMMdd");    
		SimpleDateFormat sdfOra  = new SimpleDateFormat("HHmmss");
		s = s.replaceAll("<anno>", sdfAnno.format(date));
		s = s.replaceAll("<data>", sdfData.format(date));
		s = s.replaceAll("<ora>", sdfOra.format(date));
		return s;
	}
	
	//-----------------------------------------------------------------------------------------------------
	// Strings 
	//-----------------------------------------------------------------------------------------------------
	public static boolean isEmptyString(String s) {
		return s == null || s.trim().isEmpty();
	}
	
	public static String stringOfChar(char c, int n) {
		if (n > 0) {
			char[] chars = new char[n];
			Arrays.fill(chars, c);
			return String.valueOf(chars);
		} else
			return "";
	}
	
	/**
	 * Checks for a valid string representation of an Id.
	 * 
	 * If the input string is null the returned Id is 0,
	 * otherwise the number must be an integer greater than 0. 
	 * 
	 * @param s string to be verified
	 * @return The id (even 0) or -1 if the id is bad
	 */
	public static long isValidId(String s) {
		if (s == null)
			return 0;
		long id = -1;
		try {
			id = Long.parseLong(s);
		} catch (NumberFormatException e) {
		}
		return id > 0 ? id : -1;
	}
	  
	//-----------------------------------------------------------------------------------------------------
	// Directory & Files 
	//-----------------------------------------------------------------------------------------------------
	public static File isValidDir(String parent, String dir, boolean create) {
		dir = dir.trim();
		if (isEmptyString(dir))
			return null;
		parent = parent.trim();
		if (!isEmptyString(parent) && !parent.endsWith(File.separator))
			parent += File.separator;
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

	public static void removeDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files)
					removeDirectory(aFile);
			}
		}
		dir.delete();
	}
	
	public static void clearDirectory(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files)
					removeDirectory(aFile);
			}
		} else if (!dir.exists())
			dir.mkdir();
		else {
			dir.delete();
			dir.mkdir();
		}
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
	  
	//-----------------------------------------------------------------------------------------------------
	// JSON 
	//-----------------------------------------------------------------------------------------------------
	public static String object2JSonString(Object value) throws MyException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(value);
		} catch (IOException e) {
			throw new MyException(e.getMessage());
		}
	}
	
	public static <T> T JSonString2Object(String jsonString, Class<T> bean) throws MyException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonString, bean);
		} catch (IOException e) {
			throw new MyException(e.getMessage());
		}
	}

//	
//	@SuppressWarnings("unchecked")
//	public  T[] fromJSON(String json) throws MyException {
//		ObjectMapper mapper = new ObjectMapper();
//		//mapper.disable(Feature.USE_GETTERS_AS_SETTERS);
//		T[] lista = null;
//		if (json != null && !json.isEmpty()) {
//			try {
//				lista = (T[]) mapper.readValue(json, (Class<T>) this.getClass());
//			} catch (Exception e) {
//				throw new MyException(e.getMessage());
//			}
//		}
//		return lista;
//	}
	
//	public JSONObject toJSONObject() {
//		ObjectMapper mapper = new ObjectMapper();
//		//mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		return mapper.convertValue(this, JSONObject.class);
//	}
}
