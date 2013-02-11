package gov.va.med.foundations.security.vistalink;

public class DialogLogonData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private String accessCode = "";
	private String verifyCode = "";
	private boolean changeVerify = false;
	private boolean buttonResponse = false;
	private String messageDisplayText = "This is some Display Text\n"+
						"12345678911234567892123456789312345678941234567895123456789612345678971234567898"+
						"\nAnd a second line.";
	
	public DialogLogonData() {
		setAccessCode("");
		setVerifyCode("");
		setChangeVerify(false);
		setButtonResponse(false);
	}
	
	public void setAccessCode(String value) {
		accessCode = value;
	}
	
	public void setVerifyCode(String value) {
		verifyCode = value;
	}
	
	public void setButtonResponse(boolean value) {
		buttonResponse = value;
	}
	
	public String getAccessCode() {
		return accessCode;
	}
	
	public String getVerifyCode() {
		return verifyCode;
	}
	
	public boolean getButtonResponse() {
		return buttonResponse;
	}
	
	public void setMessageDisplayText(String text) {
		messageDisplayText = text;
	}
	
	public String getMessageDisplayText() {
		return messageDisplayText;
	}
	
	public boolean getChangeVerify() {
		return changeVerify;
	}
	
	public void setChangeVerify(boolean value) {
		changeVerify = value;
	}


}
