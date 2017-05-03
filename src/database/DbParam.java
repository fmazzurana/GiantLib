package database;

public class DbParam {

	private enum dbType { DB_INTEGER, DB_LONG, DB_STRING };
	
	// properties
	private dbType type;
	private int v_int;
	private long v_long;
	private String v_string;
	
	// constructors
	public DbParam(int value) {
		type = dbType.DB_INTEGER;
		v_int = value;
	}
	public DbParam(long value) {
		type = dbType.DB_LONG;
		v_long = value;
	}
	public DbParam(String value) {
		type = dbType.DB_STRING;
		v_string = value;
	}
	
	// public methods
	public boolean isInt() {
		return type == dbType.DB_INTEGER;
	}
	public boolean isLong() {
		return type == dbType.DB_LONG;
	}
	public boolean isString() {
		return type == dbType.DB_STRING;
	}
	
	public int getAsInt() throws DBException {
		if (isInt())
			return v_int;
		else
			throw new DBException("The value is not of type integer.");
	}
	public long getAsLong() throws DBException {
		if (isLong())
			return v_long;
		else
			throw new DBException("The value is not of type long.");
	}
	public String getAsStr() throws DBException {
		if (isString())
			return v_string;
		else
			throw new DBException("The value is not of type string.");
	}
}
