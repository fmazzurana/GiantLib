package net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import commons.MyException;

public class Internet {

	/**
	 * Reads the given Url.
	 * 
	 * @param url Url to be read
	 * @param header String to put in the front of the text. (Optional)
	 * @param footer String to put at the end of the text. (optional)
	 * @return The read text with the header/footer.<br>If the Url gives no text, <b>null</b> is returned.
	 * @throws MyException
	 */
	public static String GetUrl(String url, String header, String footer) throws MyException {
		String text = null;
		if (header == null)	header = "";
		if (footer == null)	footer = "";
		InputStream is = null;
		try {
			is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1)
				sb.append((char) cp);
			text = sb.toString();
			text = text.isEmpty() ? null : header + text + footer;
		} catch (FileNotFoundException ex) {
		} catch (IOException e) {
			throw new MyException(e.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ex) {
				throw new MyException(ex.getMessage());
			}
		}
		return text;
	}

	public static byte[] GetImage(String imageUrl) throws MyException {
		try {
	        int len = -1;
	        byte[] buffer = new byte[1024];
			URL url = new URL(imageUrl);
	        URLConnection urlConnection = url.openConnection();
	        InputStream inputStream = urlConnection.getInputStream();

	        ByteArrayOutputStream os = new ByteArrayOutputStream();
	        while ((len = inputStream.read(buffer)) != -1)
	        	os.write(buffer, 0, len);
	        inputStream.close();
	        os.flush();
	        os.close();
	        return os.toByteArray();
		} catch (IOException e) {
			throw new MyException(e.getMessage());
		}
	}
}
