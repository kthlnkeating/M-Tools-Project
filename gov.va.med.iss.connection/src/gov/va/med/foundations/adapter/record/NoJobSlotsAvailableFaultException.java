package gov.va.med.foundations.adapter.record;

/**
 * This exception represents the case where on the M side 
 * there are no license slots available to start another process.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class NoJobSlotsAvailableFaultException
	extends VistaLinkFaultException {

	/**
	 * Constructor for NoJobSlotsAvailableFaultException.
	 * @param msg
	 * @param nestedException
	 */
	public NoJobSlotsAvailableFaultException(
		VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}


}
