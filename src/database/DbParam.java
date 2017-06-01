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
	
	public void modify(int value) {
		type = dbType.DB_INTEGER;
		v_int = value;
	}
	public void modify(long value) {
		type = dbType.DB_LONG;
		v_long = value;
	}
	public void modify(String value) {
		type = dbType.DB_STRING;
		v_string = value;
	}
	public void modify(byte[] value) {
		type = dbType.DB_BYTEARRAY;
		v_byteArray = value;
	}
	public void modify(Timestamp value) {
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
	public int getAsInt() throws DbException {
		if (isInt())
			return v_int;
		else
			throw new DbException("The value is not of type integer.");
	}
	public long getAsLong() throws DbException {
		if (isLong())
			return v_long;
		else
			throw new DbException("The value is not of type long.");
	}
	public String getAsStr() throws DbException {
		if (isString())
			return v_string;
		else
			throw new DbException("The value is not of type string.");
	}
	public byte[] getAsByteArray() throws DbException {
		if (isByteArray())
			return v_byteArray;
		else
			throw new DbException("The value is not of type byte array.");
	}
	public Timestamp getAsTimestamp() throws DbException {
		if (isTimestamp())
			return v_timestamp;
		else
			throw new DbException("The value is not of type timestamp.");
	}
}
