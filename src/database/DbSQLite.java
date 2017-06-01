package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbSQLite extends Database {

	public DbSQLite(String filename) throws DbException {
		super("org.sqlite.JDBC");
		super.connStr = String.format("jdbc:sqlite:%s", filename);
	}
	
	public <T> List<T> select(String sqlCmd, Class<T> resBean) throws DbException {
		Connection conn = null;
		Statement  stm = null;
		ResultSet rs = null;

		try {
			conn = Connect();
			stm = conn.createStatement();
			rs = stm.executeQuery(sqlCmd);
			if (rs != null && resBean != null) {
				List<T> result = new ArrayList<T>();
				while (rs.next()) {
					result.add((T) ResultSet2Bean(rs, resBean));
				}
				return result;
			} else
				return null;
		} catch (SQLException ex) {
			throw new DbException("Error executing \""+sqlCmd+"\"", ex);
		} finally {
			Disconnect(rs, stm, conn);
		}
	}
}
