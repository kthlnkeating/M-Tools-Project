package gov.va.mumps.debug.ui.launching;

class ParameterInfo {
	private String name;
	private String value;
	
	public ParameterInfo(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getValueForDisplay() {
		if (value == null) {
			return "<null>";
		}
		if (value.isEmpty()) {
			return "<empty>";
		}
		return value;
	}
	
	public static ParameterInfo[] valueOf(String[] params) {
		if (params == null) {
			return null;
		} else {
			int n = params.length;
			ParameterInfo[] result = new ParameterInfo[n];
			for (int i=0; i<n; ++i) {
				result[i] = new ParameterInfo(params[i], "");
			}
			return result;
		}
	}
}
