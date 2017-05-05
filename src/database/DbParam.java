package database;

import java.sql.Timestamp;

public class DbParam {

	public enum dbType { DB_INTEGER, DB_LONG, DB_STRING, DB_BYTEARRAY, DB_TIMESTAMP };
	
	// properties
	private dbType type;
	private int v_int;
	private long v_long;
	private String v_string;
	private byte[] v_byteArray;
	private Timestamp v_timestamp;
	
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
	public DbParam(byte[] value) {
		type = dbType.DB_BYTEARRAY;
		v_byteArray = value;
	}
	public DbParam(Timestamp value) {
		type = dbType.DB_TIMESTAMP;
		v_timestamp = value;
	}
	
	// public methods
	public dbType getType() {
		return type;
	}
	public boolean isInt() {
		return type == dbType.DB_INTEGER;
	}
	public boolean isLong() {
		return type == dbType.DB_LONG;
	}
	public boolean isString() {
		return type == dbType.DB_STRING;
	}
	public boolean isByteArray() {
		return type == dbType.DB_BYTEARRAY;
	}
	public boolean isTimestamp() {
		return type == dbType.DB_TIMESTAMP;
	}
	
	// getters
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
	public byte[] getAsByteArray() throws DBException {
		if (isByteArray())
			return v_byteArray;
		else
			throw new DBException("The value is not of type byte array.");
	}
	public Timestamp getAsTimestamp() throws DBException {
		if (isTimestamp())
			return v_timestamp;
		else
			throw new DBException("The value is not of type timestamp.");
	}
}
