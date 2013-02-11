package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;

/**
 * This exception class is thrown when a VistaLinkManagedConnection 
 * <br>object has exceeded its maximum allowable connection handles
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class ConnectionHandlesExceededException
	extends VistaLinkResourceException {

	/**
	 * Constructor for ConnectionHandlesExceededException.
	 * @param reason
	 */
	public ConnectionHandlesExceededException(String reason) {
		super(reason);
	}

	/**
	 * Constructor for ConnectionHandlesExceededException.
	 * @param e
	 */
	public ConnectionHandlesExceededException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for ConnectionHandlesExceededException.
	 * @param reason
	 * @param errorCode
	 */
	public ConnectionHandlesExceededException(
		String reason,
		String errorCode) {
		super(reason, errorCode);
	}

	/**
	 * Constructor for ConnectionHandlesExceededException.
	 * @param reason
	 * @param errorCode
	 * @param e
	 */
	public ConnectionHandlesExceededException(
		String reason,
		String errorCode,
		Exception e) {
		super(reason, errorCode, e);
	}

	/**
	 * Constructor for ConnectionHandlesExceededException.
	 * @param reason
	 * @param e
	 */
	public ConnectionHandlesExceededException(String reason, Exception e) {
		super(reason, e);
	}

}
