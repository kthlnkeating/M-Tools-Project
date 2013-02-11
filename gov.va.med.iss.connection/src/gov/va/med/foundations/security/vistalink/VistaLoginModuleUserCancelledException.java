package gov.va.med.foundations.security.vistalink;

/**
 * Represents a user cancellation of Login. When attempting a logon, you can trap for this specific exception, in
 * addition to the more general <code>VistaLoginModuleException</code> and <code>LoginException</code> exceptions.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class VistaLoginModuleUserCancelledException extends VistaLoginModuleException {

	/**
	 * Constructor for VistaLoginModuleUserCancelledException.
	 * @param msg String describing exception.
	 */
	VistaLoginModuleUserCancelledException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaLoginModuleUserCancelledException.
	 * @param nestedException an exception to nest
	 */
	VistaLoginModuleUserCancelledException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaLoginModuleUserCancelledException.
	 * @param msg String describing exception.
	 * @param nestedException an exception to nest.
	 */
	VistaLoginModuleUserCancelledException(String msg, Throwable nestedException) {
		super(msg, nestedException);
	}

}