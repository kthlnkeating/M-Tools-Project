package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkResponseVOImpl;

/**
 * Base class for response objects to receive and handle AVSecurity module responses from an M Vista server.
 * @see SecurityResponseFactory
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
class SecurityResponse extends VistaLinkResponseVOImpl {

	// constants used to extract result type
	/**
	 * resulttype representing success.
	 */
	static final int RESULT_SUCCESS = 1;
	/**
	 * resulttype representing failure.
	 */
	static final int RESULT_FAILURE = 0;
	/**
	 * resulttype representing partial success.
	 */
	static final int RESULT_PARTIAL = 2;

	private int resultType;
	private String resultMessage;
	//	private String rawXml;

	/**
	 * Copy constructor
	 * @param responseData the response to process
	 */
	SecurityResponse(SecurityResponse responseData) {
		this.resultType = responseData.getResultType();
		this.resultMessage = responseData.getResultMessage();
		this.rawXml = responseData.getRawResponse();
	}

	/**
	 * 
	 * @param resultType type of result -- success, failure or partialsuccess.
	 * @param resultMessage string returned with result if partialsuccess or failure
	 * @param rawXml raw XML of the response
	 */
	SecurityResponse(int resultType, String resultMessage, String rawXml) {
		this.resultType = resultType;
		this.resultMessage = resultMessage;
		this.rawXml = rawXml;
	}

	/**
	 * 
	 * @return int
	 */
	int getResultType() {
		return resultType;
	}
	/**
	 * 
	 * @return String
	 */
	String getResultMessage() {
		return resultMessage;
	}

}
