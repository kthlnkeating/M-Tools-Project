package gov.va.med.foundations.adapter.record;

/**
 * This exception represents the case where the M side has logins disabled - that is
 * when the site sets the parameter to not allow any logins.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class LoginsDisabledFaultException extends VistaLinkFaultException {

	/**
	 * Constructor for LoginsDisabledFaultException.
	 * @param vistaLinkFaultException
	 */
	public LoginsDisabledFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
