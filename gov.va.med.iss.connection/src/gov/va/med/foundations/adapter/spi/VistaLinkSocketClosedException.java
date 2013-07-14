package gov.va.med.foundations.adapter.spi;

import gov.va.med.foundations.adapter.cci.VistaLinkResourceException;

/**
 * This exception class is thrown when an attempt is made to
 * <br>access the VistaLinkManagedConnection's underlying
 * <br>VistaSocketConnection after its has been closed or invalidated
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkSocketClosedException
	extends VistaLinkResourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for VistaLinkSocketClosedException.
	 * @param reason
	 */
	public VistaLinkSocketClosedException(String reason) {
		super(reason);
	}

	/**
	 * Constructor for VistaLinkSocketClosedException.
	 * @param e
	 */
	public VistaLinkSocketClosedException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for VistaLinkSocketClosedException.
	 * @param reason
	 * @param errorCode
	 */
	public VistaLinkSocketClosedException(String reason, String errorCode) {
		super(reason, errorCode);
	}

	/**
	 * Constructor for VistaLinkSocketClosedException.
	 * @param reason
	 * @param errorCode
	 * @param e
	 */
	public VistaLinkSocketClosedException(
		String reason,
		String errorCode,
		Exception e) {
		super(reason, errorCode, e);
	}

	/**
	 * Constructor for VistaLinkSocketClosedException.
	 * @param reason
	 * @param e
	 */
	public VistaLinkSocketClosedException(String reason, Exception e) {
		super(reason, e);
	}

}
