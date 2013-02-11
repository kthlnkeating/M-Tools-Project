package gov.va.med.foundations.adapter.record;

import gov.va.med.foundations.utilities.FoundationsException;

/**
 * 
 * Response factory interface used by the connection to parse response string
 * into the response object.
 * 
 * <br> Implementations of this interface need to have a default constructor.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public interface VistaLinkResponseFactory {

	/**
	 * Performs handling of the M side response message.
	 * <br> This method is responsible for parsing String message from M side and generating 
	 * VistaLinkResponseVO implementation. 
	 *  
	 * @param response
	 * @param requestVO
	 * @return
	 * @throws FoundationsException
	 */
	public VistaLinkResponseVO handleResponse(
		String response,
		VistaLinkRequestVO requestVO)
		throws FoundationsException;

}
