package gov.va.med.foundations.security.vistalink;

import java.util.Map;

import javax.security.auth.callback.Callback;

/**
 * VistaLoginModule callback for marshalling user input for a "select division" event
 * @see VistaLoginModule
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class CallbackSelectDivision implements Callback {

	@SuppressWarnings("rawtypes")
	private Map divisionList;
	private String selectedDivisionIen = "-1";
	private int selectedOption;
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

	private int timeoutInSeconds;

	@SuppressWarnings("rawtypes")
	/**
	 * Creates a callback to display a list of divisions to the user, and collect the user's 
	 * response
	 * @param divisionList the list of divisions to display to the user
	 * @param timeoutInSeconds the timeout the callback should honor
	 */
	CallbackSelectDivision(Map divisionList, int timeoutInSeconds) {

		initialize(divisionList, timeoutInSeconds);
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Creates a callback to display a list of divisions to the user, and collect the user's 
	 * response
	 * @param divisionList the list of divisions to display to the user
	 * @param timeoutInSeconds the timeout the callback should honor
	 */
	private void initialize(Map divisionList, int timeoutInSeconds) {

		this.divisionList = divisionList;
		this.timeoutInSeconds = timeoutInSeconds;
		selectedDivisionIen = "";
		selectedOption = KEYPRESS_CANCEL;

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

	@SuppressWarnings("rawtypes")
	/**
	 * returns the list of divisions to display to the user
	 * @return Map the list of divisions to display to the user
	 */
	Map getDivisionList() {
		return divisionList;
	}

	/**
	 * returns the IEN of the division selected by the user
	 * @return String division selected by the user
	 */
	String getSelectedDivisionIen() {
		return selectedDivisionIen;
	}

	/**
	 * sets the IEN of the division selected by the user
	 * @param selectedDivisionIen IEN selected by the user
	 */
	void setSelectedDivisionIen(String selectedDivisionIen) {
		this.selectedDivisionIen = selectedDivisionIen;
	}

	/**
	 * returns the timeout setting passed in when the callback was created
	 * @return int the timeout setting
	 */
	int getTimeoutInSeconds() {
		return timeoutInSeconds;
	}
}
