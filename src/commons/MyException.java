package commons;

@SuppressWarnings("serial")
public class MyException extends Exception {

	public MyException() {}
	
	public MyException(String msg) {
		super(msg);
	}
	
	public MyException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
