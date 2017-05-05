package database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbParamsList {

	// properties
	private List<DbParam> params;
	
	// constructor
	public DbParamsList() {
		params = new ArrayList<DbParam>();
	}
	
	// --------------------------------------------------------------------------------
	// PUBLICS
	// --------------------------------------------------------------------------------
	public void clear() {
		params.clear();
	}

	public int size() {
		return params.size();
	}

	public DbParam get(int idx) {
		return params.get(idx);
	}
	
	public void add(int value) {
		params.add(new DbParam(value));
	}
	public void add(long value) {
		params.add(new DbParam(value));
	}
	public void add(String value) {
		params.add(new DbParam(value));
	}
	public void add(byte[] value) {
		params.add(new DbParam(value));
	}
	public void add(Timestamp value) {
		params.add(new DbParam(value));
	}
	
	/**
	 * Builds the params string (the sequence of '?')
	 * 
	 * @return
	 */
	public String buildParamList() {
		int nValues = params.size();
		return String.join("", Collections.nCopies(nValues, ",?")).substring(1);
	}
	
	/**
	 * Prepares the statement with the parameters list
	 * 
	 * @param stm
	 * @throws DBException
	 */
	public void prepareParams(PreparedStatement stm) throws DBException {
		try {
			for (int i = 0; i < params.size(); i++) {
				DbParam param = params.get(i);
				switch (param.getType()) {
				case DB_INTEGER:
					stm.setInt(i+1, param.getAsInt());
					break;
				case DB_LONG:
					stm.setLong(i+1, param.getAsLong());
					break;
				case DB_STRING:
					stm.setString(i+1, param.getAsStr());
					break;
				case DB_BYTEARRAY:
					stm.setBytes(i+1, param.getAsByteArray());
					break;
				case DB_TIMESTAMP:
					stm.setTimestamp(i+1, param.getAsTimestamp());
					break;
				default:
					throw new DBException("Unknown parameter type.");
				}
			}
		} catch (SQLException | DBException ex) {
			throw new DBException("Error setting the parameters list.", ex);
		}
	}
}
