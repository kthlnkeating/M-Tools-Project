package gov.va.med.iss.meditor.utils;

public class RoutineNameDialogData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String textResponse;
	private boolean buttonResponse;
	private boolean upperCase = true;
	private boolean readOnly = false;
//	private String txtServer = "";
//	private String txtPort = "";
	private String txtDirectory ="";
	
	public RoutineNameDialogData() {
		setTextResponse("");
		setButtonResponse(false);
	}
	
	public void setTextResponse(String value) {
		textResponse = value;
	}
	
	public void setButtonResponse(boolean value) {
		buttonResponse = value;
	}
	
	public String getTextResponse() {
		return textResponse;
	}
	
	public boolean getButtonResponse() {
		return buttonResponse;
	}
	
	public void setUpperCase(boolean value) {
		upperCase = value;
	}
	
	public void setDirectory(String value) {
		txtDirectory = value;
	}
	
	public boolean getUpperCase() {
		return upperCase;
	}
	
	public void setReadOnly(boolean value) {
		readOnly = value;
	}
	
	public boolean getReadOnly() {
		return readOnly;
	}
	
	public String getDirectory() {
		return txtDirectory;
	}

}
