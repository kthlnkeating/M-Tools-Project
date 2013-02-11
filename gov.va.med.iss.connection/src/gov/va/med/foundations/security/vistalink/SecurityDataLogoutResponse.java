package gov.va.med.foundations.security.vistalink;

/**
 * Implements response-specific fields for an AV.Logout security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataLogoutResponse extends SecurityResponse {

	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecuriytResponse#SecuriytResponse(int, String)
	 */
	SecurityDataLogoutResponse(SecurityResponse responseData) {
		super(responseData);
	}

}
