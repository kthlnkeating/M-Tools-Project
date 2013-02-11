package gov.va.med.foundations.security.vistalink;

/**
 * Implements response-specific fields for an AV.SelectDivision security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataSelectDivisionResponse extends SecurityResponse {

	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecuriytResponse#SecuriytResponse(int, String)
	 */
	SecurityDataSelectDivisionResponse(SecurityResponse responseData) {
		super(responseData);
	}

}
