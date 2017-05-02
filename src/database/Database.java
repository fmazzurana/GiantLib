package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	/**
	 * Tries to establish a database connection
	 * 
	 * @param dbAddress localhost if null or empty
	 * @param dbName
	 * @param dbPort 3306 if <= 0
	 * @param dbUsr
	 * @param dbPwd
	 * @return A valid DB connection
	 * @throws DBException In case of error 'lastErr' is set
	 */
	public Connection Connect(String dbAddress, String dbName, int dbPort, String dbUsr, String dbPwd) throws DBException {
		dbAddress = (dbAddress == null || dbAddress.length() == 0) ? "localhost": dbAddress;
		dbPort = dbPort <= 0 ? 3306: dbPort;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new DBException("MySQL JDBC Driver not found: " + e.getMessage());
		}
		try {
			String connStr = String.format("jdbc:mysql://%s:%d/%s", dbAddress, dbPort, dbName);
			return DriverManager.getConnection(connStr, dbUsr, dbPwd);
		} catch (SQLException e) {
			throw new DBException("Error connecting to the DB: " + e.getMessage());
		}
	}
	
	/**
	 * Tries to close a database connection
	 * 
	 * @param connection Connection to be closed
	 * @throws DBException
	 */
	public void Disconnect(Connection dbConn) throws DBException {
		try {
			if (dbConn != null)
				dbConn.close();
		} catch (SQLException e) {
			throw new DBException("Error closing DB connection: " + e.getMessage());
		}
	}
	

	
//	// TODO
//	
//	/**
//	 * Executes the given SQL statement (INSERT, UPDATE, or DELETE)
//	 * 
//	 * @param sql
//	 * @throws MyException
//	 */
//	protected void ExecuteUpdate(Connection dbConn, String sql) throws DBException {
//		try {
//			Statement stmt = dbConn.createStatement();
//			stmt.executeUpdate(sql);
//		} catch (SQLException e) {
//			throw new DBException(String.format("Query execution error: %s (%s)", e.getMessage(), sql));
//		}
//	}
//	
//	/**
//	 * Executes the given SQL statement (SELECT)
//	 * 
//	 * @param q
//	 * @return
//	 * @throws MyException
//	 */
//	protected ResultSet ExecuteQuery(Connection dbConn, String q) throws DBException {
//		ResultSet rs = null;
//		try {
//			Statement stmt = dbConn.createStatement();
//			rs = stmt.executeQuery(q);
//		} catch (SQLException e) {
//			throw new DBException(String.format("Query execution error: %s (%s)", e.getMessage(), q));
//		}
//		return rs;
//	}
//	
//	/**
//	 * Prepares a sql statement
//	 * 
//	 * @param sql SQL statement to be prepared
//	 * @return Prepared statement
//	 * @throws DBException
//	 */
//	protected PreparedStatement PrepareStatement(Connection dbConn, String sql) throws DBException {
//		try {
//			return dbConn.prepareStatement(sql);
//		} catch (SQLException e) {
//			throw new DBException("Unable to prepare the statement: " + e.getMessage());
//		}
//	}

}
