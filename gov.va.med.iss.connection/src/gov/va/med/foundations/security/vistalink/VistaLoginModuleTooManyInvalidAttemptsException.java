package gov.va.med.foundations.security.vistalink;

/**
 * If thrown, the user tried to login too many times with invalid credentials. When attempting a logon, you can trap for
 * this specific exception, in addition to the more general <code>VistaLoginModuleException</code> and
 * <code>LoginException</code> exceptions.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public final class VistaLoginModuleTooManyInvalidAttemptsException extends VistaLoginModuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg Exception message
	 * @see java.lang.Throwable#Throwable(String)
	 */
	VistaLoginModuleTooManyInvalidAttemptsException(String msg) {
		super(msg);
	}

	/**
	 * 
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleTooManyInvalidAttemptsException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * 
	 * @param msg String exception message
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleTooManyInvalidAttemptsException(String msg, Throwable nestedException) {
		super(msg, nestedException);
	}

}
