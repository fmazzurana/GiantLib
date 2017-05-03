package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import commons.MyException;
import commons.MyProperties;

public class Database {

	// properties
	private String user, password, connStr;
	
	/**
	 * Constructor
	 * The property file must contain the 'db.*' properties
	 * 
	 * @param propFile
	 * @throws DBException 
	 */
	public Database(String propFile) throws DBException {
		try {
			MyProperties prop = new MyProperties(propFile);
			String url = prop.getString("db.url", "localhost");
			int port = prop.getInt("db.port", 3306);
			String dbName = prop.getString("db.dbname");
			String extras = prop.getString("db.extraparams", "");
			user = prop.getString("db.username");
			password = prop.getString("db.password");
			connStr = String.format("jdbc:mysql://%s:%d/%s", url, port, dbName);
			if (!extras.isEmpty())
				connStr += "?" + extras;
		} catch (MyException ex) {
			throw new DBException("Error reading the properties file.", ex);
		}
	}
	

	// --------------------------------------------------------------------------------
	// PUBLICS
	// --------------------------------------------------------------------------------

	public List<Object> callProc(String procName) throws Exception {
		return callProc(procName, null);
	}

	public List<Object> callProc(String procName, DbParamsList dbParams) throws Exception {
		Connection conn = null;
		CallableStatement stm = null;
		ResultSet resultSet = null;
		int nValues = 0;
		String params = "";

		if (dbParams != null) {
			nValues = dbParams.size();
			params = String.join("", Collections.nCopies(nValues, ",?")).substring(1);
		}
		
		List<Object> result = new ArrayList<Object>();;
		String cmd = String.format("CALL %s(%s)", procName, params);
		try {
			conn = Connect();
			stm = conn.prepareCall(cmd);
			for (int i = 0; i < nValues; i++) {
				DbParam param = dbParams.get(i);
				if (param.isInt())
					stm.setInt(i+1, param.getAsInt());
				else if (param.isLong())
					stm.setLong(i+1, param.getAsLong());
				else
					stm.setString(i+1, param.getAsStr());
			}
			stm.execute();
			resultSet = stm.getResultSet();
			if (resultSet != null && resultSet.next()) {
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
					result.add(resultSet.getObject(i));
			}
		} catch (Exception e) {
			Exception ex = new Exception(procName + ": " + e.getMessage());
			ex.initCause(e);
			throw ex;
		} finally {
			Disconnect(resultSet, stm, conn);
		}
		return result;
	}

	/**
	 * Execute a stored function
	 * 
	 * @param procName
	 * @param dbParams [opt]
	 * @return The function's result
	 * @throws Exception
	 */
	public int executeFun(String funName) throws Exception {
		return executeFun(funName, null);
	}

	public int executeFun(String funName, DbParamsList dbParams) throws Exception {
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet resultSet = null;
		int nValues = 0;
		String params = "";

		if (dbParams != null) {
			nValues = dbParams.size();
			params = String.join("", Collections.nCopies(nValues, ",?")).substring(1);
		}
		
		int result;
		String cmd = String.format("SELECT %s(%s)", funName, params);
		try {
			conn = Connect();
			stm = (PreparedStatement) conn.prepareStatement(cmd, ResultSet.TYPE_SCROLL_INSENSITIVE);
			for (int i = 0; i < nValues; i++) {
				DbParam param = dbParams.get(i);
				if (param.isInt())
					stm.setInt(i+1, param.getAsInt());
				else if (param.isLong())
					stm.setLong(i+1, param.getAsLong());
				else
					stm.setString(i+1, param.getAsStr());
			}
			resultSet = stm.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getInt(1);
			} else {
				throw new Exception(funName + ": no valid resultset");
			}
		} catch (Exception e) {
			Exception ex = new Exception(funName + ": " + e.getMessage());
			ex.initCause(e);
			throw ex;
		} finally {
			Disconnect(resultSet, stm, conn);
		}
		return result;
	}

	// --------------------------------------------------------------------------------
	// PRIVATES
	// --------------------------------------------------------------------------------

	/**
	 * Tries to establish a database connection
	 * 
	 * @return A valid DB connection
	 * @throws DBException In case of error 'lastErr' is set
	 */
	private Connection Connect() throws DBException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new DBException("MySQL JDBC Driver not found: " + e.getMessage());
		}
		try {
			return DriverManager.getConnection(connStr, user, password);
		} catch (SQLException e) {
			throw new DBException("Error connecting to the DB: " + e.getMessage());
		}
	}
	
	/**
	 * Tries to close a database connection (the statement and the result also)
	 * 
	 * @param rs
	 * @param stm
	 * @param conn
	 */
	private void Disconnect(ResultSet rs, PreparedStatement stm, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {}
		}
		if (stm != null) {
			try {
				stm.close();
			} catch (SQLException e) {}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {}
		}
	}
}
