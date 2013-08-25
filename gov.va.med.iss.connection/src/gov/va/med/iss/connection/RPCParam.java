package gov.va.med.iss.connection;

import java.util.List;

public class RPCParam {
	private String type;
	private Object value;
	
	public RPCParam(String type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public static RPCParam valueOf(String value) {
		return new RPCParam("string", value);
	}
	
	public static RPCParam valueOf(List<String> value) {
		return new RPCParam("array", value);
	}
	
	public static RPCParam[] valueOf(String[] value) {
		int n = value.length;
		RPCParam[] result = new RPCParam[n];
		for (int i=0; i<n; ++i) {
			result[i] = valueOf(value[i]);
		}
		return result;
	}
}
