package gov.va.med.foundations.security.vistalink;

/**
 * Implements response-specific fields for an AV.SetupAndIntroText security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataSetupAndIntroTextResponse extends SecurityResponse {

	private VistaSetupAndIntroTextInfo setupAndIntroTextInfo;
	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecuriytResponse#SecuriytResponse(int, String)
	 */
	SecurityDataSetupAndIntroTextResponse(
		VistaSetupAndIntroTextInfo setupAndIntroTextInfo,
		SecurityResponse responseData) {

		super(responseData);
		this.setupAndIntroTextInfo = setupAndIntroTextInfo;
	}

	/**
	 * 
	 * @return VistaSetupAndIntroTextInfo server information
	 */
	VistaSetupAndIntroTextInfo getSetupAndIntroTextInfo() {
		return setupAndIntroTextInfo;
	}
}
