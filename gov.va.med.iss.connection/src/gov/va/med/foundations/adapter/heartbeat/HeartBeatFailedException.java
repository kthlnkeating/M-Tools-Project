package gov.va.med.foundations.adapter.heartbeat;

import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;

/**
 *	This exception class is used to notify the managed connection and 
 *<br> its event listeners that a scheduled heart beat has failed. 
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class HeartBeatFailedException extends VistaLinkResourceException {

	/**
	 * Constructor for HeartBeatFailedException.
	 * @param reason
	 */
	public HeartBeatFailedException(String reason) {
		super(reason);
	}

	/**
	 * Constructor for HeartBeatFailedException.
	 * @param e
	 */
	public HeartBeatFailedException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for HeartBeatFailedException.
	 * @param reason
	 * @param errorCode
	 */
	public HeartBeatFailedException(String reason, String errorCode) {
		super(reason, errorCode);
	}

	/**
	 * Constructor for HeartBeatFailedException.
	 * @param reason
	 * @param errorCode
	 * @param e
	 */
	public HeartBeatFailedException(
		String reason,
		String errorCode,
		Exception e) {
		super(reason, errorCode, e);
	}

	/**
	 * Constructor for HeartBeatFailedException.
	 * @param reason
	 * @param e
	 */
	public HeartBeatFailedException(String reason, Exception e) {
		super(reason, e);
	}

}
