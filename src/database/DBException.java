package database;

@SuppressWarnings("serial")
public class DBException extends Exception {

	public DBException() {}
	
	public DBException(String msg) {
		super(msg);
	}
}
