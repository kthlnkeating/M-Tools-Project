package gov.va.med.foundations.security.vistalink;

import java.util.Map;
import java.util.Vector;

/**
 * Implements response-specific fields for an AV.Logon security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataLogonResponse extends SecurityResponse {

	private boolean needNewVerifyCode;
	private boolean needDivisionSelection;
	private Map divisionList;
	private Vector postSignInText;
	private String cvcHelpText;

	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecuriytResponse#SecuriytResponse(int, String)
	 */
	SecurityDataLogonResponse(
		boolean needNewVerifyCode,
		boolean needDivisionSelection,
		Map divisionList,
		Vector postSignInText,
		String cvcHelpText,
		SecurityResponse responseData) {

		super(responseData);
		this.needNewVerifyCode = needNewVerifyCode;
		this.needDivisionSelection = needDivisionSelection;
		this.divisionList = divisionList;
		this.postSignInText = postSignInText;
		this.cvcHelpText = cvcHelpText;
	}

	/**
	 * 
	 * @return boolean whether a new verify code is needed
	 */
	boolean getNeedNewVerifyCode() {
		return needNewVerifyCode;
	}

	/**
	 * 
	 * @return TreeMap list of divisions
	 */
	Map getDivisionList() {
		return divisionList;
	}

	/**
	 * 
	 * @return Vector post-sign-in text
	 */
	Vector getPostSignInText() {
		return postSignInText;
	}

	/**
	 * 
	 * @return boolean whether division selection is needed
	 */
	boolean getNeedDivisionSelection() {
		return needDivisionSelection;
	}

	/**
	 * retrieve the help text from M to use for changing the verify code
	 * @return String help text for changing the verify code.
	 */
	String getCvcHelpText() {
		return cvcHelpText;
	}

}
