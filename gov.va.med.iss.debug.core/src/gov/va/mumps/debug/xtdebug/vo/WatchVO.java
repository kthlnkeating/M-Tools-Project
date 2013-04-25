package gov.va.mumps.debug.xtdebug.vo;

public class WatchVO {

	private String variableName;
	private String prevValue;
	private String newValue;
	
	public WatchVO(String variableName, String prevValue, String newValue) {
		super();
		this.variableName = variableName;
		this.prevValue = prevValue;
		this.newValue = newValue;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getPrevValue() {
		return prevValue;
	}

	public String getNewValue() {
		return newValue;
	}
	
}
