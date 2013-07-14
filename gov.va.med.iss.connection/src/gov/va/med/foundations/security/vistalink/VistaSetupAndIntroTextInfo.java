package gov.va.med.foundations.security.vistalink;

import java.util.Vector;

/**
 * An object to hold information gathered from a SetupAndGetIntroText call, to then pass
 * to a visual callback to present to the user when prompting for a/v codes
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class VistaSetupAndIntroTextInfo {
	private String introductoryText = "";
	private String serverName = "";
	private String volume = "";
	private String uci = "";
	private String device = "";
	private int logonRetryCount = 5;
	private int timeout = 0;
	private int port = 0;

	/**
	 * Sets the raw Introductory Text string returned from M. This is expected to be 
	 * many actual display lines concatenated into one string separated by &lt;BR&gt;'s.
	 * @param value the introductory text string to set
	 */
	public void setIntroductoryText(String value) {
		introductoryText = value;
	}
	/**
	 * Gets the raw Introductory Text string returned from M. This is typically 
	 * many actual display lines concatenated into one string separated by &lt;BR&gt;'s.
	 * @return String the introductory text as a string
	 */
	public String getIntroductoryText() {
		return introductoryText;
	}
	/**
	 * Sets the server name representing the M system active partition, expected to be as returned from M.
	 * @param value the server name
	 */
	public void setServerName(String value) {
		serverName = value;
	}
	/**
	 * Gets the server name representing the M system active partition, expected to be as returned from M. 
	 * @return String the server name
	 */
	public String getServerName() {
		return serverName;
	}
	/**
	 * Sets the volume name representing the M system active partition, expected to be as returned from M.
	 * @param value the M partition's volume
	 */
	public void setVolume(String value) {
		volume = value;
	}
	/**
	 * Gets the volume name representing the M system active partition, expected to be as returned from M.
	 * @return String the M partition's volume
	 */
	public String getVolume() {
		return volume;
	}
	/**
	 * sets the number of logon retries allowed by Kernel, expected to be as returned from M.
	 * @param count the # of logon retries
	 */
	public void setLogonRetryCount(int count) {
		logonRetryCount = count;
	}
	/**
	 * returns the number of logon retries allowed by Kernel, expected to be as returned from M.
	 * @return int the # of logon retries
	 */
	public int getLogonRetryCount() {
		return logonRetryCount;
	}
	/**
	 * Sets the UCI name representing the M system active partition, expected to be as returned from M.
	 * @param value the M partition UCI name
	 */
	public void setUci(String value) {
		uci = value;
	}
	/**
	 * Gets the UCI name representing the M system active partition, expected to be as returned from M.
	 * @return String the M partition UCI name
	 */
	public String getUci() {
		return uci;
	}
	/**
	 * Sets the Device name representing the M system active partition, expected to be as returned from M.
	 * @param value the device name for the M partition
	 */
	public void setDevice(String value) {
		device = value;
	}
	/**
	 * Gets the Device name representing the M system active partition, expected to be as returned from M.
	 * @return String the device name for the M partition
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * Sets the timeout, expected to be as returned from the M server
	 * @param value timeout in seconds
	 */
	public void setTimeout(int value) {
		timeout = value;
	}
	/**
	 * Gets the timeout from the M system active partition prior to logon, expected to be as returned from M.
	 * @return int
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the port, expected to be the port to which the connection is being made
	 * @param value port of the current connection
	 */
	public void setPort(int value) {
		port = value;
	}

	/**
	 * returns the port value for the server connection
	 * @return int port for the server connection
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Returns a crude "toString" representation of the data held in the object.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Server: "
			+ serverName
			+ " Volume: "
			+ volume
			+ " UCI: "
			+ uci
			+ " Device: "
			+ device
			+ " Intro Text Length: "
			+ introductoryText.length()
			+ " Timeout: "
			+ timeout;
	}

	/**
	 * Returns a vector of Strings representing the introductory text, expected to be as returned
	 * from the M system. The Strings are stored in the Vector in the order returned from the M system.
	 * @return Vector
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getIntroductoryTextLines() {

		String introText = introductoryText;
		Vector introLines = new Vector(25);
		String lineBreakString = "<BR>";
		int lineBreakStringLength = lineBreakString.length();

		int lineNumber = 0;
		int lineStartPos = 0;
		int posNextBreak = introductoryText.indexOf(lineBreakString, lineStartPos);

		while (posNextBreak > -1) {
			introLines.add(lineNumber, introText.substring(lineStartPos, posNextBreak));
			lineStartPos = posNextBreak + lineBreakStringLength;
			posNextBreak = introductoryText.indexOf(lineBreakString, posNextBreak + 1);
			lineNumber++;
		}

		return introLines;
	}
}
