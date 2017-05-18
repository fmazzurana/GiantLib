package database;

@SuppressWarnings("serial")
public class DbException extends Exception {

	public DbException() {}
	
	public DbException(String msg) {
		super(msg);
	}
	
	public DbException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
