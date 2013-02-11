package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This exception fault is returned from M, and signifies that the user's login credentials were invalid too many times,
 * and the M system is rejecting further login attempts as a result. This fault exception will never be returned to an
 * application; instead, the application will be returned a
 * <code>VistaLoginModuleTooManyInvalidAttemptsException</code>.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityTooManyInvalidLoginAttemptsFaultException extends SecurityFaultException {

	/**
	 * Constructor for LoginsDisabledFaultException.
	 * @param vistaLinkFaultException the exception to copy into a new exception type
	 */
	SecurityTooManyInvalidLoginAttemptsFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
