package net;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import commons.MyException;

public class RestClient {

	private Client client;
	
	public RestClient() {
		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);
//		client.setConnectTimeout(connectionTimeout);
//		client.setReadTimeout(readTimeout);
	}
	
	public String get(String url) throws MyException {
		//WebResource webResource = client.resource(UriBuilder.fromUri(url).build());
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new MyException("GET: HTTP error code:" + response.getStatus());
		}
		return response.getEntity(String.class);
	}
	
	public String post(String url, String json) throws MyException {
		WebResource webResource = client.resource(UriBuilder.fromUri(url).build());
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		if (response.getStatus() != 201) {
			throw new MyException("POST: HTTP error code:" + response.getStatus());
		}
		return response.getEntity(String.class);
	}
}
