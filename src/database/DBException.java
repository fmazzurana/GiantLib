package database;

@SuppressWarnings("serial")
public class DBException extends Exception {

	public DBException() {}
	
	public DBException(String msg) {
		super(msg);
	}
	
	public DBException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
