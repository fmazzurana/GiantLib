package database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.ParamBean;
import commons.Utils;

/**
 * Allows the execution of Stored Procedure & Functions
 * over a connection to a MySql database.
 * 
 * @author Fabrizio Mazzurana
 *
 */
public class DbMySql extends Database {

	// --------------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------------
	//private static final String defUrl    = "localhost";
	private static final String defUrl    = "192.168.178.3";
	//private static final String defUrl    = "fmazzurana.noip.me";
	private static final int    defPort   = 3306;
	private static final String defUsr    = "giant";
	private static final String defPwd    = "Eir3annach";
	private static final String defExtras = "";
	//private static final String defExtras = "autoReconnect=true&useSSL=false";

	private static final String typeString  = "String";
	private static final String typeInteger = "Integer";
	private static final String typeDouble  = "Double";
	private static final String typeBoolean = "Boolean";
	private static final String invalidValue = "The parameter is not a valid %s value: %s.";

	// properties
	private String user, password;
	private String paramReadProc;
	private String paramWriteProc;
	
	/**
	 * Constructor
	 * 
	 * @param dbname
	 * @param url [opt]
	 * @param port [opt]
	 * @param usr [opt]
	 * @param pwd [opt]
	 * @param extras [opt]
	 * @throws DbException
	 */
	public DbMySql(String dbname) throws DbException {
		super("com.mysql.jdbc.Driver");
		init(dbname, defUrl, defPort, defUsr, defPwd, defExtras);
	}
	
	public DbMySql(String dbname, String url, int port, String usr, String pwd, String extras) throws DbException {
		super("com.mysql.jdbc.Driver");
		init(dbname, url, port, usr, pwd, extras);
	}

	private void init(String dbname, String url, int port, String usr, String pwd, String extras) throws DbException {
		if (Utils.isEmptyString(dbname))
			throw new DbException("Null or empty dbname");
		if (Utils.isEmptyString(url))
			throw new DbException("Null or empty url");
		if (port <= 0)
			throw new DbException("Invalid port number");
		// defines the jdbc driver & connections string
		super.connStr = String.format("jdbc:mysql://%s:%d/%s", url, port, dbname);
		if (!Utils.isEmptyString(extras))
			super.connStr += "?" + extras;
		user = usr;
		password = pwd;
		
		// defines the default parameters read/write procedures
		paramReadProc = "p_paramsRead";
		paramWriteProc = "p_paramsWrite";
	}

	// --------------------------------------------------------------------------------
	// PROCEDURES & FUNCTIONS CALL
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


	// --------------------------------------------------------------------------------
	// PARAMETERS
	// --------------------------------------------------------------------------------
	public void setParamReadProcedure(String procName) {
		paramReadProc = procName;
	}

	public void setParamWriteProcedure(String procName) {
		paramWriteProc = procName;
	}
	
	/**
	 * These methods get a parameter from the DB
	 *  
	 * @param name Name of the parameter
	 * @return Parameter's value
	 * @throws DBException
	 */
	public String getParamAsString(String name) throws DbException {
		return paramRead(name, typeString);
	}
	public int getParamAsInteger(String name) throws DbException {
		String value = paramRead(name, typeInteger);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new DbException(String.format(invalidValue, typeInteger, value));
		}
	}
	public double getParamAsDouble(String name) throws DbException {
		String value = paramRead(name, typeDouble);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new DbException(String.format(invalidValue, typeDouble, value));
		}
	}
	public Boolean getParamAsBoolean(String name) throws DbException {
		String value = paramRead(name, typeBoolean);
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * These methods write a parameter to the DB (update or create)
	 *  
	 * @param name Name of the parameter
	 * @param value New value for the parameter
	 * @throws DBException
	 */
	public void setParamAsString(String name, String value) throws DbException {
		paramWrite(typeString, name, value);
	}
	public void setParamAsInteger(String name, int value) throws DbException {
		paramWrite(typeInteger, name, Integer.toString(value));
	}
	public void setParamAsDouble(String name, double value) throws DbException {
		paramWrite(typeDouble, name, Double.toString(value));
	}
	public void setParamAsBoolean(String name, Boolean value) throws DbException {
		paramWrite(typeBoolean, name, Boolean.toString(value));
	}

	
	/**
	 * Main method to read a parameter from the DB
	 * 
	 * @param name
	 * @param type
	 * @return
	 * @throws DbException
	 */
	private String paramRead(String name, String type) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(name);
		List<ParamBean> param = callProcedure(paramReadProc, params, ParamBean.class);
		if (param != null && param.size() == 1) {
			if (param.get(0).getType().equalsIgnoreCase(type))
				return param.get(0).getValue();
			else
				throw new DbException(String.format("The parameter is not of type %s.", type));
		} else
			throw new DbException(String.format("Parameter not found: %s", name));
	}

	/**
	 * Main method to writes a parameter to the DB
	 * 
	 * @param name
	 * @param value
	 * @throws DbException
	 */
	private void paramWrite(String type, String name, String value) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(type);
		params.add(name);
		params.add(value);
		callProcedure(paramWriteProc, params);
	}
}
