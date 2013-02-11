package gov.va.med.foundations.security.vistalink;

import javax.security.auth.callback.Callback;

/**
 * VistaLoginModule callback for marshalling user input for a "change verify code" event
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class CallbackChangeVc implements Callback {

	private String oldVerifyCode;
	private String newVerifyCode;
	private String newVerifyCodeCheck;
	private String message;
	private String cvcHelpText;
	private int selectedOption;
	private int timeoutInSeconds;
	private boolean enteredVerifyCodeWasNull;

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
	 * 
	 * @param message The message prompt to display to users for why they must change verify codes
	 * @param cvcHelpText help text from M to use for CVC help button
	 * @param timeoutInSeconds the timeout 
	 * @param enteredVerifyCodeWasNull whether the user had to enter a verify code or not
	 */
	CallbackChangeVc(
		String message,
		String cvcHelpText,
		int timeoutInSeconds,
		boolean enteredVerifyCodeWasNull) {
			
		oldVerifyCode = "";
		newVerifyCode = "";
		newVerifyCodeCheck = "";
		this.message = message;
		this.cvcHelpText = cvcHelpText;
		this.timeoutInSeconds = timeoutInSeconds;
		this.enteredVerifyCodeWasNull = enteredVerifyCodeWasNull;
		selectedOption = KEYPRESS_CANCEL;
	}

	/**
	 * Gets whether the verify code entered at logon was null or not
	 * @return true if the verify code entered during login was null, false if not
	 */
	boolean getEnteredVerifyCodeWasNull() {
		return this.enteredVerifyCodeWasNull;
	}
	
	/** 
	 * Gets the message to display to the user about why they need to change their vc.
	 * @return String the message
	 */
	String getMessage() {
		return this.message;
	}

	/**
	 * Gets the part of the message to display to the user from M, if they press help button on cvc dialog
	 * @return String M-returned portion of text to display for CVC help button on CVC dialog
	 */
	String getCvcHelpText() {
		return this.cvcHelpText;
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
	 * sets the old verify code
	 * @param oldVerifyCodeToSet the old verify code to set. Can pass null to set to empty string.
	 */
	void setOldVerifyCode(char[] oldVerifyCodeToSet) {
		if (oldVerifyCodeToSet == null) {
			oldVerifyCode = "";
		} else {
			oldVerifyCode = new String(oldVerifyCodeToSet);
		}
	}

	/**
	 * returns the old verify code
	 * @return String the old verify code
	 */
	String getOldVerifyCode() {
		return oldVerifyCode;
	}

	/**
	 * sets the verify code
	 * @param newVerifyCodeToSet the verify code to set. Can pass null to set to empty string.
	 */
	void setNewVerifyCode(char[] newVerifyCodeToSet) {
		if (newVerifyCodeToSet == null) {
			newVerifyCode = "";
		} else {
			newVerifyCode = new String(newVerifyCodeToSet);
		}
	}

	/**
	 * returns the new verify code
	 * @return String the new verify code
	 */
	String getNewVerifyCode() {
		return newVerifyCode;
	}

	/**
	 * sets the new verify code "check"
	 * @param newVerifyCodeCheckToSet the new verify code "check". Can pass null to set to empty string.
	 */
	void setNewVerifyCodeCheck(char[] newVerifyCodeCheckToSet) {
		if (newVerifyCodeCheckToSet == null) {
			newVerifyCodeCheck = "";
		} else {
			newVerifyCodeCheck = new String(newVerifyCodeCheckToSet);
		}
	}

	/**
	 * returns the new verify code "check"
	 * @return String new verify code "check"
	 */
	String getNewVerifyCodeCheck() {
		return newVerifyCodeCheck;
	}

	/**
	 * returns the timeout setting passed in when the callback was created
	 * @return int the timeout setting
	 */
	int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

}
