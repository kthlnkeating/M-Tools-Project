package gov.va.med.foundations.security.vistalink;

/**
 * If thrown, no reachable listener was found on the path represented by the specified IP address and Port. When
 * attempting a logon, you can trap for this specific exception, in addition to the more general
 * <code>VistaLoginModuleException</code> and <code>LoginException</code> exceptions.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class VistaLoginModuleNoPathToListenerException extends VistaLoginModuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg Exception message
	 * @see java.lang.Throwable#Throwable(String)
	 */
	VistaLoginModuleNoPathToListenerException(String msg) {
		super(msg);
	}

	/**
	 * 
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleNoPathToListenerException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * 
	 * @param msg String exception message
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleNoPathToListenerException(String msg, Throwable nestedException) {
		super(msg, nestedException);
	}

}
