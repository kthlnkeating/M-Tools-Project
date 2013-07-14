package gov.va.med.foundations.security.vistalink;

import java.util.Map;

@SuppressWarnings("rawtypes")
/**
 * Implements response-specific fields for an AV.UpdateVC security message 
 * @see SecurityResponse
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityDataChangeVcResponse extends SecurityResponse {

	private boolean needDivisionSelection;
	private Map divisionList;

	/**
	 * 
	 * @see gov.va.med.foundations.security.vistalink.SecurityResponse#SecurityResponse(int, String)
	 */
	SecurityDataChangeVcResponse(
		boolean needDivisionSelection,
		Map divisionList,
		SecurityResponse responseData) {

		super(responseData);
		this.divisionList = divisionList;
		this.needDivisionSelection = needDivisionSelection;
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
	 * @return boolean whether division selection is needed
	 */
	boolean getNeedDivisionSelection() {
		return needDivisionSelection;
	}
}
