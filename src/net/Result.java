package net;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class Result implements Serializable {

	private static final long serialVersionUID = 5944158149565505427L;
	
	// properties
	@JsonProperty("message")
	private String message;

	// publics
	public Result(String msg) {
		this.message = msg;
	}
	
	// getters & setters
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
