package gov.va.med.foundations.net;

import gov.va.med.foundations.utilities.FoundationsException;

/**
 * Represents an exception thrown during read/write operations on a socket
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaSocketException extends FoundationsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for VistaSocketException.
	 */
	public VistaSocketException() {
		super();
	}

	/**
	 * Constructor for VistaSocketException.
	 * @param s
	 */
	public VistaSocketException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaSocketException.
	 * @param nestedException
	 */
	public VistaSocketException(Exception nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaSocketException.
	 * @param msg
	 * @param nestedException
	 */
	public VistaSocketException(String msg, Exception nestedException) {
		super(msg, nestedException);
	}

}