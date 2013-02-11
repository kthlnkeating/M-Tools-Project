package gov.va.med.foundations.security.vistalink;

/**
 * User timed out of a login. When attempting a logon, you can trap for this specific exception, in addition to the more
 * general <code>VistaLoginModuleException</code> and <code>LoginException</code> exceptions.
 * 
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class VistaLoginModuleUserTimedOutException extends VistaLoginModuleException {

	/**
	 * Constructor for VistaLoginModuleUserTimedOutException.
	 * @param msg String describing exception.
	 */
	VistaLoginModuleUserTimedOutException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaLoginModuleUserTimedOutException.
	 * @param nestedException an exception to nest
	 */
	VistaLoginModuleUserTimedOutException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaLoginModuleUserTimedOutException.
	 * @param msg String describing exception.
	 * @param nestedException an exception to nest.
	 */
	VistaLoginModuleUserTimedOutException(String msg, Throwable nestedException) {
		super(msg, nestedException);
	}

}