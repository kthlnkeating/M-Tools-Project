package gov.va.med.foundations.net;

/**
 * Represents an exception identifying a timeout has occurred during
 * <br>read/write operations
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaSocketTimeOutException extends VistaSocketException {

	/**
	/**
	 * Constructor for VistaSocketTimeOutException.
	 */
	public VistaSocketTimeOutException() {
		super();
	}

	/**
	 * Constructor for VistaSocketTimeOutException.
	 * @param s
	 */
	public VistaSocketTimeOutException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaSocketTimeOutException.
	 * @param nestedException
	 */
	public VistaSocketTimeOutException(Exception nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaSocketTimeOutException.
	 * @param msg
	 * @param nestedException
	 */
	public VistaSocketTimeOutException(String msg, Exception nestedException) {
		super(msg, nestedException);
	}

}
