package database;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DbCommon {

	protected String connStr;
	
	public DbCommon(String driver) throws DbException {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new DbException("JDBC Driver not found: "+driver, e);
		}
	}
	
	// --------------------------------------------------------------------------------
	// PRIVATES
	// --------------------------------------------------------------------------------

	/**
	 * Tries to establish a database connection
	 * 
	 * @param user [opz]
	 * @param password [opz]
	 * @return A valid DB connection
	 * @throws DbException In case of error 'lastErr' is set
	 */
	protected Connection Connect() throws DbException {
		try {
			return DriverManager.getConnection(connStr);
		} catch (SQLException e) {
			throw new DbException("Error connecting to the DB: " + e.getMessage());
		}
	}
	protected Connection Connect(String user, String password) throws DbException {
		try {
			return DriverManager.getConnection(connStr, user, password);
		} catch (SQLException e) {
			throw new DbException("Error connecting to the DB: " + e.getMessage());
		}
	}

	/**
	 * Tries to close a database connection (the statement and the result also)
	 * 
	 * @param rs
	 * @param stm
	 * @param conn
	 */
	protected void Disconnect(ResultSet rs, PreparedStatement stm, Connection conn) {
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
	protected void Disconnect(ResultSet rs, Statement stm, Connection conn) {
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

	// --------------------------------------------------------------------------------
	// BEAN MANAGEMENT
	// --------------------------------------------------------------------------------

	// A class (bean) may have properties of the following types:
	// - boolean
	// - byte
	// - short
	// - int
	// - long
	// - double
	// - bytes[]
	// - String
	// - java.sql.Date
	// - java.sql.Time
	// - java.sql.Timestamp

	/**
	 * Converts a resultset row into an object of the given class
	 * The target class must have the 'set<Property>' methods, where '<property>' are the columns names
	 * 
	 * @param rs DB resultset
	 * @param bean The destination bean class
	 * @return An object of class bean
	 * @throws DbException
	 */
	protected <T> T ResultSet2Bean(ResultSet rs, Class<T> bean) throws DbException {
		T entity = null;
		try {
			entity = (T)bean.newInstance();
			Method[] methods = entity.getClass().getMethods();
			ResultSetMetaData metaData = rs.getMetaData();
			int count = metaData.getColumnCount();
			for (int i = 1; i <= count; i++) {
				// for all columns...
			    for (Method setter : methods) {
			    	// checks if the setter exists...
			        if (setter.getName().equalsIgnoreCase("set" + metaData.getColumnLabel(i))) { 
			        	switch(metaData.getColumnType(i)) {
		        		// boolean
	        			case java.sql.Types.BOOLEAN:
		        			setter.invoke(entity, rs.getBoolean(i));
		        			break;
		        		// byte
		        		case java.sql.Types.TINYINT:
		        			setter.invoke(entity, rs.getByte(i));
		        			break;
		        		// short
		        		case java.sql.Types.SMALLINT:
		        			setter.invoke(entity, rs.getShort(i));
		        			break;
		        		// int
		        		case java.sql.Types.INTEGER:
		        			setter.invoke(entity, rs.getInt(i));
		        			break;
		        		// long
		        		case java.sql.Types.BIT:
		        		case java.sql.Types.BIGINT:
		        			setter.invoke(entity, rs.getLong(i));
		        			break;
		        		// double
		        		case java.sql.Types.FLOAT:
		        		case java.sql.Types.DOUBLE:
		        		case java.sql.Types.REAL:
		        		case java.sql.Types.NUMERIC:
		        		case java.sql.Types.DECIMAL:
		        			setter.invoke(entity, rs.getDouble(i));
		        			break;
		        		// bytes[]
		        		case java.sql.Types.BLOB:
		        		case java.sql.Types.CLOB:
		        		case java.sql.Types.BINARY:
		        		case java.sql.Types.VARBINARY:
		        		case java.sql.Types.LONGVARBINARY:
		        			setter.invoke(entity, rs.getBytes(i));
		        			break;
		        		// String
		        		case java.sql.Types.CHAR:
		        		case java.sql.Types.NCHAR:
		        		case java.sql.Types.VARCHAR:
		        		case java.sql.Types.NVARCHAR:
		        		case java.sql.Types.LONGVARCHAR:
		        		case java.sql.Types.LONGNVARCHAR:
		        			if(rs.getString(i) == null || rs.getString(i) == "")
		        				setter.invoke(entity, "");
		        			else
		        				setter.invoke(entity, rs.getString(i).trim());
		        			break;
		        		// java.sql.Date
		        		case java.sql.Types.DATE:
		        			setter.invoke(entity, rs.getDate(i));
		        			break;
		        		// java.sql.Time
		        		case java.sql.Types.TIME:
		        		case java.sql.Types.TIME_WITH_TIMEZONE:
		        			setter.invoke(entity, rs.getTime(i));
		        			break;
		        		// java.sql.Timestamp
		        		case java.sql.Types.TIMESTAMP:
		        		case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
		        			setter.invoke(entity, rs.getTimestamp(i));
		        			break;
		        		default:
		        			throw new DbException("Datatype not implemented !");
			        	}
			        	break;
			        }
			    }			 
			}
		} catch (InstantiationException | IllegalAccessException | SQLException | IllegalArgumentException | InvocationTargetException e) {
			throw new DbException(e.getMessage());
		}
		return entity;
	}
	/*
	Remaining java.sql.Types
	------------------------
	static int ARRAY  SQL type ARRAY.
	static int DATALINK  SQL type DATALINK.
	static int DISTINCT  SQL type DISTINCT.
	static int JAVA_OBJECT  SQL type JAVA_OBJECT.
	static int NCLOB  SQL type NCLOB.
	static int NULL  SQL value NULL.
	static int OTHER  SQL type is database-specific
	static int REF  SQL type REF.
	static int REF_CURSOR  SQL type REF CURSOR.
	static int ROWID  SQL type ROWID
	static int SQLXML  SQL type XML.
	static int STRUCT  SQL type STRUCT.

	ResultSet
	---------
	Array 	getArray(int columnIndex)

	InputStream 	getAsciiStream(int columnIndex)
	InputStream 	getBinaryStream(int columnIndex)

	java.math.BigDecimal 	getBigDecimal(int columnIndex)

	Blob 	getBlob(int columnIndex)

	java.io.Reader 	getCharacterStream(int columnIndex)
	java.io.Reader 	getNCharacterStream(int columnIndex)

	Clob 	getClob(int columnIndex)

	NClob 	getNClob(int columnIndex)

	Object 	getObject(int columnIndex)
	Object 	getObject(int columnIndex, Map<String,Class<?>> map)

	<T> T 	getObject(int columnIndex, Class<T> type)

	Ref 	getRef(int columnIndex)

	java.sql.RowId 	getRowId(int columnIndex)

	java.sql.SQLXML 	getSQLXML(int columnIndex)
	*/
}
