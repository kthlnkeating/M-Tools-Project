package gov.va.med.iss.meditor.utils;

import java.util.Vector;


/**
 * VistaLoginModule callback base class for marshalling user input for a "display error" or "display information" to
 * user event. Extended by CallbackErrorConfirm and CallbackInformationConfirm.
 * @see VistaLoginModule
 * @see CallbackErrorConfirm
 * @see CallbackInformationConfirm
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class CallbackConfirm {

	@SuppressWarnings("rawtypes")
	private Vector displayMessages;
	private int selectedOption;
	private int timeoutInSeconds;
	private int messageMode;
	private String windowTitle;

	/**
	 * For error message confirmations
	 */
	static final int ERROR_MESSAGE = 0;

	/**
	 * For "success" message confirmations
	 */
	static final int INFORMATION_MESSAGE = 1;

	/**
	 * For help message confirmations
	 */
	static final int HELP_MESSAGE = 2;
	
	/**
	 * For post-sign-in text
	 */
	static final int POST_TEXT_MESSAGE = 3;
	
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
	 * Creates this callback.
	 * @param displayMessages The messages to display
	 * @param messageMode INFORMATION_MESSAGE or ERROR_MESSAGE
	 * @param windowTitle title to use for confirmation window
	 * @param timeoutInSeconds The timeout the callback should honor.
	 */
	@SuppressWarnings("rawtypes")
	CallbackConfirm(Vector displayMessages, int messageMode, String windowTitle, int timeoutInSeconds) {
		this.displayMessages = displayMessages;
		this.timeoutInSeconds = timeoutInSeconds;
		this.messageMode = messageMode;
		this.selectedOption = KEYPRESS_CANCEL;
		this.windowTitle = windowTitle;
	}

	/**
	 * returns the error messages set into the callback
	 * @return Vector the error message set into the callback
	 */
	@SuppressWarnings("rawtypes")
	Vector getDisplayMessages() {
		return displayMessages;
	}

	/** 
	 * Sets how the user closed the dialog.
	 * @param settingOkOrCancelOrTimeout KEYPRESS_OK or KEYPRESS_CANCEL or KEYPRESS_TIMEOUT.
	 */
	void setSelectedOption(int settingOkOrCancelOrTimeout) {
		selectedOption = settingOkOrCancelOrTimeout;
	}

	/**
	 * Gets the message mode (INFORMATION_MESSAGE or ERROR_MESSAGE)
	 * @return int
	 */
	int getMessageMode() {
		return messageMode;
	}
	
	/**
	 * Gets the title of the window
	 * @return String
	 */
	String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * Gets how the user closed the dialog.
	 * @return int KEYPRESS_OK or KEYPRESS_CANCEL or KEYPRESS_TIMEOUT
	 */
	int getSelectedOption() {
		return selectedOption;
	}

	/**
	 * returns the timeout setting passed in when the callback was created
	 * @return int the timeout setting
	 */
	int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

}
