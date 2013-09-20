package gov.va.mumps.debug.core.model;

public class MStackInfo {
	private MCodeLocation codeLocation;
	private MVariableInfo[] variableInfos;
	
	public MStackInfo(MCodeLocation codeLocation, MVariableInfo[] variableInfos) {
		super();
		this.codeLocation = codeLocation;
		this.variableInfos = variableInfos;
	}
	
	public MCodeLocation getCodeLocation() {
		return codeLocation;
	}
	
	public void setCodeLocation(MCodeLocation codeLocation) {
		this.codeLocation = codeLocation;
	}
	
	public MVariableInfo[] getVariableInfos() {
		return variableInfos;
	}

	public void setVariableInfos(MVariableInfo[] variableInfos) {
		this.variableInfos = variableInfos;
	}
}
