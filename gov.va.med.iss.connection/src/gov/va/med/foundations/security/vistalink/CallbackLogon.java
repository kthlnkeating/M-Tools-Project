package gov.va.med.foundations.security.vistalink;

import javax.security.auth.callback.Callback;

/**
 * VistaLoginModule callback for marshalling user input for a "enter access and verify code" event
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class CallbackLogon implements Callback {

	private String accessCode;
	private String verifyCode;
	private VistaSetupAndIntroTextInfo setupInfo;
	private int selectedOption;
	private int tryCount;
	private int timeoutInSeconds;
	private boolean requestCvc;
	private String token = "";

	/**
	 * The value representing a user cancelling the dialog.
	 */
	static final int KEYPRESS_CANCEL = 0;
	/**
	 * The value representing the user pressing KEYPRESS_OK in the dialog.
	 */
	static final int KEYPRESS_OK = 1;
	/**
	 * The value representing the user timing out of the dialog.
	 */
	static final int KEYPRESS_TIMEOUT = 2;

	/**
	 * initializes a callback for retrieving access and verify codes
	 * @param setupInfo introductory text and server information to display to the user
	 * @param timeoutInSeconds the timeout that this callback should honor
	 * @param tryCount what logon attempt # this logon is, e.g. 1 (first), 2, 3, etc.
	 */
	CallbackLogon(VistaSetupAndIntroTextInfo setupInfo, int timeoutInSeconds, int tryCount) {

		initialize(setupInfo, timeoutInSeconds, tryCount);

	}

	/**
	 * initializes a callback for retrieving access and verify codes
	 * @param setupInfo introductory text and server information to display to the user
	 * @param timeoutInSeconds the timeout that this callback should honor
	 * @param tryCount what logon attempt # this logon is, e.g. 1 (first), 2, 3, etc.
	 */
	private void initialize(VistaSetupAndIntroTextInfo setupInfo, int timeoutInSeconds, int tryCount) {

		accessCode = "";
		verifyCode = "";
		this.setupInfo = setupInfo;
		this.timeoutInSeconds = timeoutInSeconds;
		this.requestCvc = false;
		selectedOption = KEYPRESS_CANCEL;
		this.tryCount = tryCount;
	}
	/** 
	 * Sets how the user closed the dialog.
	 * @param settingOkOrCancelOrTimeout KEYPRESS_OK or KEYPRESS_CANCEL or KEYPRESS_TIMEOUT.
	 */
	void setSelectedOption(int settingOkOrCancelOrTimeout) {
		selectedOption = settingOkOrCancelOrTimeout;
	}
	/**
	 * Gets how the user closed the dialog.
	 * @return int KEYPRESS_OK or KEYPRESS_CANCEL or KEYPRESS_TIMEOUT
	 */
	int getSelectedOption() {
		return selectedOption;
	}
	/**
	 * Sets the access code that the callback should return.
	 * @param accessCode access code to set. Can pass null to set to empty string.
	 */
	void setAccessCode(char[] accessCode) {
		if (accessCode == null) {
			this.accessCode = "";
		} else {
			this.accessCode = new String(accessCode);
		}
	}

	/**
	 * returns the access code that was set into the callback.
	 * @return String access code that was set into the callback
	 */
	String getAccessCode() {
		return accessCode;
	}
	/**
	 * Sets the verify code that the callback should return.
	 * @param verifyCode verify code to set. Can pass null to set to empty string.
	 */
	void setVerifyCode(char[] verifyCode) {
		if (verifyCode == null) {
			this.verifyCode = "";
		} else {
			this.verifyCode = new String(verifyCode);
		}
	}

	/**
	 * returns the verify code that was set into the callback.
	 * @return String verify code that was set into the callback
	 */
	String getVerifyCode() {
		return verifyCode;
	}
	/**
	 * retrieves the introductory text and other server settings 
	 * @return VistaSetupAndIntroTextInfo Introductory text and server information to display
	 */
	VistaSetupAndIntroTextInfo getSetupAndIntroTextInfo() {
		return setupInfo;
	}
	/**
	 * returns the timeout setting passed in when the callback was created
	 * @return int the timeout setting
	 */
	int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

	/**
	 * returns whether the user has requested to change verify code
	 * @param requestCvc true if the user has requested to change their verify code
	 */
	void setRequestCvc(boolean requestCvc) {
		this.requestCvc = requestCvc;
	}

	/**
	 * sets whether the user has requested to change verify code.
	 * @return boolean
	 */
	boolean getRequestCvc() {
		return this.requestCvc;
	}
	
	/**
	 * sets a CCOW token returned by Kernel to be used for logon
	 * @param token token
	 */
	void setToken(String token) {
		this.token = token;
	}

	/**
	 * returns a CCOW token presumably provided by Kernel for use to logon
	 * @return String token
	 */
	String getToken() {
		return token;
	}

	/**
	 * sets the current try count for this login (e.g., this is the 3rd logon try)
	 * @return the try count for this logon
	 */
	int getTryCount() {
		return tryCount;
	}

	/**
	 * return the tryCount stored in this callback
	 * @param tryCount returns the current try count
	 */	
	void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

}
