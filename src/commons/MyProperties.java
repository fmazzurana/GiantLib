package commons;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyProperties {
	
	// properties
	private Properties properties;

	public MyProperties() throws MyException {
		loadFile("application.properties");
	}
	
	public MyProperties(String propFilename) throws MyException {
		loadFile(propFilename);
	}

	// --------------------------------------------------------------------------------
	// PUBLICS
	// --------------------------------------------------------------------------------

	/**
	 * Gets a property as a string.
	 * If specified, the default value is returned when the property is not found.
	 * 
	 * @param propName
	 * @param defValue [opt]
	 * @return The string value
	 * @throws MyException
	 */
	public String getString(String propName) throws MyException {
		return getProp(propName);
	}
	public String getString(String propName, String defValue) {
		try {
			return getProp(propName);
		} catch (MyException e) {
			return defValue;
		}
	}

	/**
	 * Gets a property as an integer.
	 * If specified:
	 * - the default value is returned when the property is not found
	 * - the value is adjusted with the min/max values
	 * 
	 * @param propName
	 * @param minValue [opt]
	 * @param maxValue [opt]
	 * @param defValue [opt]
	 * @return The integer value
	 * @throws MyException
	 */
	public int getInt(String propName) throws MyException {
		String value = getProp(propName);
		return string2int(value);
	}
	public int getInt(String propName, int minValue, int maxValue) throws MyException {
		int value = getInt(propName);
		if (value < minValue)	value = minValue;
		if (value > maxValue)	value = maxValue;
		return value;
	}
	public int getInt(String propName, int defValue) throws MyException {
		String value;
		try {
			value = getProp(propName);
		} catch (MyException e) {
			return defValue;
		}
		return string2int(value);
	}
	public int getInt(String propName, int minValue, int maxValue, int defValue) throws MyException {
		int value = getInt(propName, defValue);
		if (value < minValue)	value = minValue;
		if (value > maxValue)	value = maxValue;
		return value;
	}
	

	/**
	 * Gets a property as a boolean; it accepts:
	 * - t, true, v, vero, y, yes, s, si, 1 as true;
	 * - f, false, falso, n, no, 0 as false
	 * If specified, the default value is returned when the property is not found.
	 * 
	 * @param propName
	 * @param defValue [opt]
	 * @return The boolean value
	 * @throws MyException
	 */
	public boolean getBoolean(String propName) throws MyException {
		String value = getProp(propName);
		return string2bool(value.toLowerCase());
	}
	public boolean getBoolean(String propName, boolean defValue) throws MyException {
		String value;
		try {
			value = getProp(propName);
		} catch (MyException e) {
			return defValue;
		}
		return string2bool(value.toLowerCase());
	}

	// --------------------------------------------------------------------------------
	// PRIVATES
	// --------------------------------------------------------------------------------

	/**
	 * Loads the properties file
	 * 
	 * @param propFilename
	 * @throws MyException
	 */
	private void loadFile(String propFilename) throws MyException {
		properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(propFilename);
			properties.load(input);
		} catch (FileNotFoundException fnf) {
			throw new MyException("Property file not found: " + propFilename, fnf);
		} catch (IOException io) {
			throw new MyException("Unable to load property file: " + propFilename, io);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	/**
	 * Searches for a property in the current properties list
	 * 
	 * @param propName
	 * @return The property value
	 * @throws MyException If the property is not found.
	 */
	private String getProp(String propName) throws MyException {
		String value = properties.getProperty(propName);
		if (value == null)
			throw new MyException("Property not found.");
		return value;
	}

	/**
	 * Converts a string into an integer
	 * 
	 * @param value
	 * @return
	 * @throws MyException
	 */
	private int string2int(String value) throws MyException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			throw new MyException("Property value is not a valid integer: " + value, nfe);
		}
	}

	/**
	 * Converts a string into a boolean
	 * 
	 * @param value
	 * @return
	 * @throws MyException
	 */
	private boolean string2bool(String value) throws MyException {
		if (value.equals("t") || value.equals("true") || value.equals("v") || value.equals("vero") || value.equals("y") || value.equals("yes") || value.equals("s") || value.equals("si") || value.equals("1"))
			return true;
		else if (value.equals("f") || value.equals("false") || value.equals("falso") || value.equals("n") || value.equals("no") || value.equals("0"))
			return false;
		else
			throw new MyException("Property value is not a valid boolean: " + value);
	}
}
