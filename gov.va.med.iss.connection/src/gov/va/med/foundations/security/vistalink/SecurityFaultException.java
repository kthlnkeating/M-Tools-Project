package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This fault exception class is used for all security-related errors returned from the M system. It represents an error
 * that happened on the M system, that VistaLink does not provide a specific java exception for. It will not be returned
 * directly to an application calling a Vista login. Instead, it would be nested within a
 * <code>VistaLoginModuleException</code> which is directly returned to an application calling a Vista login.
 * <p>Calling <code>getMessage</code> on a <code>VistaLoginModuleException</code> might, for example, reveal nested
 * exceptions. For example:
 * <pre>
 * ERROR: gov.va.med.foundations.security.vistalink.VistaLoginModuleException: Security fault occured on the M system.;
 * nested  exception is:  gov.va.med.foundations.security.vistalink.SecurityFaultException:  Fault Code: 'Client'; Fault
 * String: 'Unexpected Message Format'; Fault Actor: '';   Code: '183002'; Type: ''; Message: 'Security message action
 * 'AV.SetupAndIntroText' is an unknown security action.'
 * </pre>
 * @see VistaLoginModuleException
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
class SecurityFaultException extends VistaLinkFaultException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkFaultException#VistaLinkFaultException(VistaLinkFaultException)
	 */
	SecurityFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
