package gov.va.med.iss.mdebugger.vo;

public class StackVO {

	private String stackName;
	private String caller;
	
	public StackVO(String stackName, String caller) {
		super();
		this.stackName = stackName;
		this.caller = caller;
	}

	public String getStackName() {
		return stackName;
	}

	public String getCaller() {
		return caller;
	}

}
