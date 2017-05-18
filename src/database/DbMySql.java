package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import commons.MyException;
import commons.MyProperties;

/**
 * Allows the execution of Stored Procedure & Functions
 * over a connection to a MySql database.
 * 
 * @author Fabrizio Mazzurana
 *
 */
public class DbMySql extends DbCommon {

	// properties
	private String user, password;
	
	/**
	 * Constructor: gets the db properties.
	 * The property file must contain the 'db.*' properties
	 * 
	 * @param propFile
	 * @throws DbException 
	 */
	public DbMySql(String propFile) throws DbException {
		super("com.mysql.jdbc.Driver");
		
		try {
			MyProperties prop = new MyProperties(propFile);
			String url = prop.getString("db.url", "localhost");
			int port = prop.getInt("db.port", 3306);
			String dbName = prop.getString("db.dbname");
			String extras = prop.getString("db.extraparams", "");
			user = prop.getString("db.username");
			password = prop.getString("db.password");
			super.connStr = String.format("jdbc:mysql://%s:%d/%s", url, port, dbName);
			if (!extras.isEmpty())
				super.connStr += "?" + extras;
		} catch (MyException ex) {
			throw new DbException("Error reading the properties file.", ex);
		}
	}
	

	// --------------------------------------------------------------------------------
	// PUBLICS
	// --------------------------------------------------------------------------------
	/**
	 * Calls a stored procedure
	 *  
	 * @param procName Name of the SP
	 * @param procParams [opt] Parameters to be passed to the SP
	 * @param resBean Resultset bean
	 * @return Result A List of result beans or null
	 * @throws DbException
	 */
	public <T> List<T> callProcedure(String procName) throws DbException {
		return callProcedure(procName, null, null);
	}

	public <T> List<T> callProcedure(String procName, DbParamsList procParams) throws DbException {
		return callProcedure(procName, procParams, null);
	}

	public <T> List<T> callProcedure(String procName, Class<T> resBean) throws DbException {
		return callProcedure(procName, null, resBean);
	}

	public <T> List<T> callProcedure(String procName, DbParamsList procParams, Class<T> resBean) throws DbException {
		Connection conn = null;
		CallableStatement stm = null;
		ResultSet rs = null;

		// builds and execute the statement with the given stored procedure
		String params = procParams == null ?  "" : procParams.buildParamList();
		String cmd = String.format("CALL %s(%s)", procName, params);
		try {
			conn = Connect(user, password);
			stm = conn.prepareCall(cmd);
			if (procParams != null)
				procParams.prepareParams(stm);
			stm.execute();
			rs = stm.getResultSet();
			if (rs != null && resBean != null) {
				List<T> result = new ArrayList<T>();
				while (rs.next()) {
					result.add((T) ResultSet2Bean(rs, resBean));
				}
				return result;
			} else
				return null;
		} catch (SQLException ex) {
			throw new DbException("Error executing "+procName, ex);
		} finally {
			Disconnect(rs, stm, conn);
		}
	}

	/**
	 * Executes a stored function
	 * 
	 * @param funName Name of the SF
	 * @param procParams [opt] Parameters to be passed to the SF
	 * @return The function result
	 * @throws DbException
	 */
	public int execFunctionRetInt(String funName) throws DbException {
		return execFunctionRetInt(funName, null);
	}

	public int execFunctionRetInt(String funName, DbParamsList procParams) throws DbException {
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		// builds and execute the statement with the given stored function
		String params = procParams == null ?  "" : procParams.buildParamList();
		String cmd = String.format("SELECT %s(%s)", funName, params);
		try {
			conn = Connect(user, password);
			stm = (PreparedStatement) conn.prepareStatement(cmd, ResultSet.TYPE_SCROLL_INSENSITIVE);
			if (procParams != null)
				procParams.prepareParams(stm);
			rs = stm.executeQuery();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			} else {
				throw new DbException(funName + ": no valid resultset");
			}
		} catch (SQLException ex) {
			throw new DbException("Error executing "+funName, ex);
		} finally {
			Disconnect(rs, stm, conn);
		}
	}
}
