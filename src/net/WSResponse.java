package net;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commons.MyException;
import commons.Utils;

public class WSResponse {

	final static Logger logger = LogManager.getLogger(WSResponse.class);
	
	public static Response OK(Object value) {
		return response(Status.OK, value);
	}
	
	public static Response Error(StackTraceElement caller, Status status, String msg) {
		logger.error("{}: {}", caller, msg);
		return response(status, new Result(msg));
	}
	
	/**
	 * 
	 * @param caller caller name
	 * @param msg message to be logged/returned
	 * @return Response
	 * @throws MyException 
	 */
	public static Response ServerError(StackTraceElement caller, String msg) {
		logger.fatal("{}: {}", caller, msg);
		return response(Status.INTERNAL_SERVER_ERROR, new Result(msg));
	}
	
	// --------------------------------------------------------------------------------------------
	// Privates
	// --------------------------------------------------------------------------------------------
	private static Response response(Status status, Object value) {
		String json = "";
		try {
			if (value != null)
				json = Utils.object2JSonString(value);
		} catch (MyException e) {
			json = e.getMessage();
		}
		return Response.status(status)
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Credentials", "true")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600")
						.entity(json).build();
	}
}
