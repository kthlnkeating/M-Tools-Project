package gov.va.med.foundations.adapter.record;

import gov.va.med.foundations.utilities.FoundationsException;

/**
 * Base request interface
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 *
 */
public interface VistaLinkRequestVO {

	/**
	 * Method getRequestString. 
	 * <br> Return request string to the connection that is written to the socket
	 * / send to MUMPS.
	 * 
	 * @return String
	 * @throws FoundationsException
	 */
	public String getRequestString() throws FoundationsException;

}
