package database;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonBean<T> {

	public String toJSonString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (IOException e) {
			return null;
		}
	}
	
//	public JSONObject toJSONObject() {
//		ObjectMapper mapper = new ObjectMapper();
//		//mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		return mapper.convertValue(this, JSONObject.class);
//	}

//	
//	@SuppressWarnings("unchecked")
//	public  T[] fromJSON(String json) throws MyException {
//		ObjectMapper mapper = new ObjectMapper();
//		//mapper.disable(Feature.USE_GETTERS_AS_SETTERS);
//		T[] lista = null;
//		if (json != null && !json.isEmpty()) {
//			try {
//				lista = (T[]) mapper.readValue(json, (Class<T>) this.getClass());
//			} catch (Exception e) {
//				throw new MyException(e.getMessage());
//			}
//		}
//		return lista;
//	}
}
