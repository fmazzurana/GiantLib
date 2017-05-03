package database;

import java.util.ArrayList;
import java.util.List;

public class DbParamsList {

	// properties
	private List<DbParam> params;
	
	// constructor
	public DbParamsList() {
		params = new ArrayList<DbParam>();
	}
	
	// public methods
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
}
