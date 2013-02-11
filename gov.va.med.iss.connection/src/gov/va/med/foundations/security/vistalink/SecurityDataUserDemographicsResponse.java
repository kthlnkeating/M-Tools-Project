package gov.va.med.foundations.security.vistalink;

import java.util.Hashtable;

/**
 * Implements response-specific fields for an AV.GetUserDemographics security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataUserDemographicsResponse extends SecurityResponse {

	private Hashtable userDemographicsHashtable;

	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecuriytResponse#SecuriytResponse(int, String)
	 */
	SecurityDataUserDemographicsResponse(
		Hashtable userDemographicsHashtable,
		SecurityResponse responseData) {

		super(responseData);
		this.userDemographicsHashtable = userDemographicsHashtable;
	}

	/**
	 * returns a Vista Kernel Principal populated with the demographics values returned from Vista
	 * @return VistaKernelPrincipalImpl Kernel principal who's demographic values are populated
	 */
	Hashtable getUserDemographicsHashtable() {
		return userDemographicsHashtable;
	}
}
