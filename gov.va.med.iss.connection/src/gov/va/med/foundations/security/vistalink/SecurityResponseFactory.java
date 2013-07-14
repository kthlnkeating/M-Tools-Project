package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkRequestVO;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

//import x.gov.va.med.iss.log4j.*;

/**
 * Performs XML parsing on a security message returned from M, creating response objects of the correct class
 * to match the type of response received, and parsing information from the message into the appropriate fields
 * on the appropriate response object type.
 * @see SecurityResponse
 * @see SecurityDataChangeVcResponse
 * @see SecurityDataLogonResponse
 * @see SecurityDataLogoutResponse
 * @see SecurityDataSelectDivisionResponse
 * @see SecurityDataSetupAndIntroTextResponse
 * @see SecurityDataUserDemographicsResponse.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class SecurityResponseFactory extends VistaLinkResponseFactoryImpl {

	/**
	 * Defines the message type for a security response
	 */
	static final String GOV_VA_MED_SECURITY_RESPONSE = "gov.va.med.foundations.security.response";
	/**
	 * Defines the message type for a security errors response
	 */
	static final String GOV_VA_MED_SECURITY_ERROR = "gov.va.med.foundations.security.error";

	/**
	 * XML attribute value for a fault message type
	 */
	static final String GOV_VA_MED_SECURITY_FAULT = "gov.va.med.foundations.security.fault";

	/**
	 * String test value 
	 */
	static final String ERROR_MSG_SEC =
		XmlUtilities.XML_HEADER + "<VistaLink messageType=\"" + GOV_VA_MED_SECURITY_FAULT;
	//			+ "\" >";

	// Initialize Logger instance to be used by this class
	private static final Logger LOGGER = Logger.getLogger(SecurityResponseFactory.class);

	/**
	 * The string returned from the M server in a resulttype representing success.
	 */
	static final String RESULT_SUCCESS_STRING = "success";
	/**
	 * The string returned from the M server in a resulttype representing failure.
	 */
	static final String RESULT_FAILURE_STRING = "failure";
	/**
	 * The string returned from the M server in a resulttype representing partial success.
	 */
	static final String RESULT_PARTIAL_STRING = "partialSuccess";

	/**
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#parseMessageBody(String, 
	 * String, Document, String, VistaLinkRequestVO)
	 */
	protected VistaLinkResponseVO parseMessageBody(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		VistaLinkRequestVO requestVO)
		throws FoundationsException {

		String securityMsgRequestType = null;
		@SuppressWarnings("unused")
		String securityVersion = null;

		if ((messageType.equals(GOV_VA_MED_SECURITY_ERROR)) || (messageType.equals(GOV_VA_MED_SECURITY_RESPONSE))) {

			try {
				// get the security version (for future use with version checking)
				XPath xpath = new DOMXPath("/VistaLink/SecurityInfo/.");
				Node securityInfoNode = (Node) xpath.selectSingleNode(doc);
				securityVersion = ((Attr) (securityInfoNode.getAttributes()).getNamedItem("version")).getValue();

				if (messageType.equals(GOV_VA_MED_SECURITY_RESPONSE)) {

					// get the message action (response type)
					xpath = new DOMXPath("/VistaLink/Response/.");
					Node responseNode = (Node) xpath.selectSingleNode(doc);
					securityMsgRequestType = ((Attr) (responseNode.getAttributes()).getNamedItem("type")).getValue();

				} else if (messageType.equals(GOV_VA_MED_SECURITY_ERROR)) {
					handleFault(doc, messageType);
				}

			} catch (JaxenException e) {

				throw new FoundationsException("Error parsing security response", e);

			} catch (VistaLinkFaultException e) {

				throw new FoundationsException("Error parsing security response", e);

			}

		} else {

			throw new FoundationsException("Unknown Response Message Type Returned: " + messageType);
		}

		SecurityResponse responseData = getResponseData(securityMsgRequestType, rawXml, doc);
		return responseData;

	}

	/**
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#doesResponseIndicateFault(String)
	 */
	protected boolean doesResponseIndicateFault(String rawXml) {
		return rawXml.startsWith(ERROR_MSG_SEC);
	}

	/**
	 * 
	 * @return VistaAVSecurityResponseSimple result object
	 * @throws JaxenException errors encountered during parsing are passed on up
	 * @throws FoundationsException errors encountered during parsing are passed on up
	 */
	private SecurityResponse getResponseData(String securityMsgRequestType, String rawXml, Document xdoc)
		throws FoundationsException {

		try {
			if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_SETUP_AND_INTRO_TEXT)) {
				return getSetupAndIntroTextData(rawXml, xdoc);
			} else if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_LOGON)) {
				return getLogonData(rawXml, xdoc);
			} else if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_SELECT_DIVISION)) {
				return getSelectDivisionData(rawXml, xdoc);
			} else if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_UPDATE_VC)) {
				return getUpdateVerifyCodeData(rawXml, xdoc);
			} else if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_LOGOUT)) {
				return getLogoutData(rawXml, xdoc);
			} else if (securityMsgRequestType.equals(SecurityRequestFactory.MSG_ACTION_USER_DEMOGRAPHICS)) {
				return getUserDemographicsData(rawXml, xdoc);
			} else {
				throw new FoundationsException("Unknown security message action: " + securityMsgRequestType);
			}
		} catch (JaxenException e) {

			throw new FoundationsException("Failure while parsing security response", e);

		}
	}

	/**
	 * Do message parsing for Logon responses
	 */
	@SuppressWarnings("rawtypes")
	private SecurityDataLogonResponse getLogonData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_LOGON);
		boolean needNewVerifyCode = false;
		boolean needDivisionSelection = false;
		TreeMap divisionList = null;
		Vector postSignInText = null;
		String cvcHelpText = "";

		if (responseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {

			// if resultType success get post-sign-in text
			postSignInText = subGetPostSignInTextLines(xdoc);

		} else if (responseData.getResultType() == SecurityResponse.RESULT_FAILURE) {

			// if resultType is failure -- no further action?

		} else if (responseData.getResultType() == SecurityResponse.RESULT_PARTIAL) {

			// if resultType is partialsuccess, get post-sign-in text
			postSignInText = subGetPostSignInTextLines(xdoc);

			// first check if we need a new verify code
			XPath xpath = new DOMXPath("/VistaLink/Response/PartialSuccessData/.");
			Node partialNode = (Node) xpath.selectSingleNode(xdoc);
			try {

				needNewVerifyCode =
					Boolean
						.valueOf(((Attr) (partialNode.getAttributes()).getNamedItem("changeVerify")).getValue())
						.booleanValue();
				cvcHelpText = ((Attr) (partialNode.getAttributes()).getNamedItem("cvcHelpText")).getValue();

			} catch (Exception e) {

				// Silence this exception -- if these attributes are not there, we simpley don't need to
				// change verify code

			}

			if (!needNewVerifyCode) {

				// second check if we need to select division
				needDivisionSelection = subGetNeedDivisionSelection(xdoc);
				if (needDivisionSelection) {
					divisionList = subGetDivisionList(xdoc);
					if (divisionList.size() == 0) {

						throw new FoundationsException(
							SecurityRequestFactory.MSG_ACTION_LOGON
								+ " message requested division selection, but provided no divisions to select from.");

					}

				} else {

					throw new FoundationsException(
						SecurityRequestFactory.MSG_ACTION_LOGON
							+ " message was partial success, but without an indicator to either select division or change verify code!");

				}
			}

		} else if (responseData.getResultType() != SecurityResponse.RESULT_SUCCESS) {

			throw new FoundationsException(
				SecurityRequestFactory.MSG_ACTION_LOGON
					+ " message has unknown security response type: "
					+ responseData.getResultType());

		}

		return new SecurityDataLogonResponse(
			needNewVerifyCode,
			needDivisionSelection,
			divisionList,
			postSignInText,
			cvcHelpText,
			responseData);
	}

	/**
	 * Do message parsing for Select Division responses
	 */
	private SecurityDataSelectDivisionResponse getSelectDivisionData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_SELECT_DIVISION);
		SecurityDataSelectDivisionResponse selectDivisionData = new SecurityDataSelectDivisionResponse(responseData);
		return selectDivisionData;

	}

	/**
	 * Do message parsing for Update Verify Code responses
	 */
	@SuppressWarnings("rawtypes")
	private SecurityDataChangeVcResponse getUpdateVerifyCodeData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_UPDATE_VC);
		TreeMap divisionList = null;
		boolean needDivisionSelection = false;

		if (responseData.getResultType() == SecurityResponse.RESULT_FAILURE) {

			// don't have to do anything here

		} else if (responseData.getResultType() == SecurityResponse.RESULT_PARTIAL) {

			if (subGetNeedDivisionSelection(xdoc)) {
				needDivisionSelection = true;
				divisionList = subGetDivisionList(xdoc);
				if (divisionList.size() == 0) {
					throw new FoundationsException(
						SecurityRequestFactory.MSG_ACTION_UPDATE_VC
							+ " message requested division selection, but provided no divisions to select from.");
				}
			}

		} else if (responseData.getResultType() != SecurityResponse.RESULT_SUCCESS) {

			// some kind of failure there was no result type -- do what?
			throw new FoundationsException(
				SecurityRequestFactory.MSG_ACTION_UPDATE_VC
					+ " message has unknown type for Update Verify Code request: "
					+ responseData.getResultType());

		}

		return new SecurityDataChangeVcResponse(needDivisionSelection, divisionList, responseData);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector subGetPostSignInTextLines(Document xdoc) throws JaxenException {

		Vector postSignInTextList = new Vector();

		XPath xpath = new DOMXPath("/VistaLink/Response/PostSignInText/.");
		Node messageNode = (Node) xpath.selectSingleNode(xdoc);
		if (messageNode != null) {
			if (messageNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList messageLineList = messageNode.getChildNodes();
				for (int i = 0; i < messageLineList.getLength(); i++) {
					Node messageLineNode = messageLineList.item(i);
					if (messageLineNode.getNodeName().equals("Line")
						&& (messageLineNode.getNodeType() == Node.ELEMENT_NODE)) {
						NodeList messageLineText = messageLineNode.getChildNodes();
						if (messageLineText.getLength() > 0) {
							for (int j = 0; j < messageLineText.getLength(); j++) {
								postSignInTextList.add(j, ((Text) messageLineText.item(j)).getData());
							}
						}
					}
				}
			}
		}
		return postSignInTextList;
	}

	private SecurityResponse getBaseInformationFromMessage(String rawXml, Document xdoc, String securityMsgRequestType)
		throws JaxenException, FoundationsException {

		int resultValue = -1;
		String resultMessage = "";
		String resultTypeString = "";

		// get result type string	
		XPath xpath = new DOMXPath("/VistaLink/Response/.");
		Node responseNode = (Node) xpath.selectSingleNode(xdoc);
		resultTypeString = ((Attr) (responseNode.getAttributes()).getNamedItem("status")).getValue();

		// set result value, get messages if needed
		if (resultTypeString.equals(RESULT_FAILURE_STRING)) {

			resultValue = SecurityResponse.RESULT_FAILURE;
			xpath = new DOMXPath("/VistaLink/Response/Message/text()");
			Node resultMessageNode = (Node) xpath.selectSingleNode(xdoc);
			if (resultMessageNode != null) {
				if (resultMessageNode.getNodeType() == Node.TEXT_NODE) {
					resultMessage = ((Text) resultMessageNode).getData();
				}
			}

		} else if (resultTypeString.equals(RESULT_PARTIAL_STRING)) {

			resultValue = SecurityResponse.RESULT_PARTIAL;
			xpath = new DOMXPath("/VistaLink/Response/Message/text()");
			Node resultMessageNode = (Node) xpath.selectSingleNode(xdoc);
			if (resultMessageNode != null) {
				if (resultMessageNode.getNodeType() == Node.TEXT_NODE) {
					resultMessage = ((Text) resultMessageNode).getData();
				}
			}

		} else if (resultTypeString.equals(RESULT_SUCCESS_STRING)) {

			resultValue = SecurityResponse.RESULT_SUCCESS;

		} else {

			// some kind of failure there was no result type
			throw new FoundationsException(
				securityMsgRequestType + " message has unknown result type: " + resultTypeString);
		}

		SecurityResponse responseData = new SecurityResponse(resultValue, resultMessage, rawXml);
		return responseData;
	}

	private boolean subGetNeedDivisionSelection(Document xdoc) throws JaxenException {

		boolean returnValue = false;

		XPath xpath = new DOMXPath("/VistaLink/Response/PartialSuccessData/.");
		Node partialNode = (Node) xpath.selectSingleNode(xdoc);
		try {
			returnValue =
				Boolean
					.valueOf(((Attr) (partialNode.getAttributes()).getNamedItem("needDivisionSelection")).getValue())
					.booleanValue();
		} catch (Exception e) {
			// Silence this exception -- if we don't need to select divisions, the attribute won't be there
		}
		return returnValue;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TreeMap subGetDivisionList(Document xdoc) throws JaxenException {
		TreeMap divisionList = new TreeMap();

		XPath xpath = new DOMXPath("/VistaLink/Response/PartialSuccessData/Divisions/.");
		Node divisionsNode = (Node) xpath.selectSingleNode(xdoc);
		if (divisionsNode != null) {
			if (divisionsNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList divisionNodeList = divisionsNode.getChildNodes();
				for (int i = 0; i < divisionNodeList.getLength(); i++) {
					Node divisionNode = divisionNodeList.item(i);
					if ((divisionNode.getNodeName().equals("Division"))
						&& (divisionNode.getNodeType() == Node.ELEMENT_NODE)) {
						String strDivisionIen = ((Attr) (divisionNode.getAttributes()).getNamedItem("ien")).getValue();
						String strDivisionName = ((Attr) (divisionNode.getAttributes()).getNamedItem("divName")).getValue();
						String strDivisionNumber =
							((Attr) (divisionNode.getAttributes()).getNamedItem("divNumber")).getValue();

						boolean isDefaultDivision = false;
						if (((Attr) (divisionNode.getAttributes()).getNamedItem("default")) != null) {
							String isDefaultDivisionString =
								((Attr) (divisionNode.getAttributes()).getNamedItem("default")).getValue();
							isDefaultDivision = isDefaultDivisionString.equals("true");
						}

						divisionList.put(
							strDivisionNumber,
							new VistaInstitution(strDivisionIen, strDivisionName, strDivisionNumber, isDefaultDivision));
					}
				}
			}
		}
		return divisionList;
	}

	/**
	 * Do message parsing for Get User Demographics responses
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private SecurityDataUserDemographicsResponse getUserDemographicsData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_USER_DEMOGRAPHICS);
		Hashtable userDemographicsHashtable = new Hashtable();

		if (responseData.getResultType() == SecurityResponse.RESULT_SUCCESS) {

			//nameinfo node
			XPath xpath = new DOMXPath("/VistaLink/Response/NameInfo/.");
			Node nameInfoNode = (Node) xpath.selectSingleNode(xdoc);
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_NEWPERSON01,
				(String) XmlUtilities.getAttr(nameInfoNode, "newPerson01Name").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_DISPLAY,
				(String) XmlUtilities.getAttr(nameInfoNode, "standardConcatenated").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_FAMILYLAST,
				(String) XmlUtilities.getAttr(nameInfoNode, "familyLast").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_GIVENFIRST,
				(String) XmlUtilities.getAttr(nameInfoNode, "givenFirst").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_MIDDLE,
				(String) XmlUtilities.getAttr(nameInfoNode, "middle").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_PREFIX,
				(String) XmlUtilities.getAttr(nameInfoNode, "prefix").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_SUFFIX,
				(String) XmlUtilities.getAttr(nameInfoNode, "suffix").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_NAME_DEGREE,
				(String) XmlUtilities.getAttr(nameInfoNode, "degree").getValue());

			//userinfo node
			xpath = new DOMXPath("/VistaLink/Response/UserInfo/.");
			Node userInfoNode = (Node) xpath.selectSingleNode(xdoc);
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_DUZ,
				(String) XmlUtilities.getAttr(userInfoNode, "duz").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_DTIME,
				(String) XmlUtilities.getAttr(userInfoNode, "timeout").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_TITLE,
				(String) XmlUtilities.getAttr(userInfoNode, "title").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_SERVICE_SECTION,
				(String) XmlUtilities.getAttr(userInfoNode, "serviceSection").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_LANGUAGE,
				(String) XmlUtilities.getAttr(userInfoNode, "language").getValue());

			//divisioninfo node
			xpath = new DOMXPath("/VistaLink/Response/Division/.");
			Node divisionInfoNode = (Node) xpath.selectSingleNode(xdoc);
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_DIVISION_IEN,
				(String) XmlUtilities.getAttr(divisionInfoNode, "ien").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_DIVISION_STATION_NAME,
				(String) XmlUtilities.getAttr(divisionInfoNode, "divName").getValue());
			userDemographicsHashtable.put(
				VistaKernelPrincipal.KEY_DIVISION_STATION_NUMBER,
				(String) XmlUtilities.getAttr(divisionInfoNode, "divNumber").getValue());

		} else {
			// some kind of failure there was no result type
			throw new FoundationsException(
				SecurityRequestFactory.MSG_ACTION_USER_DEMOGRAPHICS
					+ " message has unknown result type: "
					+ responseData.getResultType());
		}

		SecurityDataUserDemographicsResponse userDemographicsData =
			new SecurityDataUserDemographicsResponse(userDemographicsHashtable, responseData);
		return userDemographicsData;
	}

	/**
	 * Do message parsing for Logout responses
	 */
	private SecurityDataLogoutResponse getLogoutData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_LOGOUT);
		SecurityDataLogoutResponse logoutData = new SecurityDataLogoutResponse(responseData);
		return logoutData;
	}

	/**
	 * Do message parsing for Setup / Get Introductory Text responses
	 */
	private SecurityDataSetupAndIntroTextResponse getSetupAndIntroTextData(String rawXml, Document xdoc)
		throws JaxenException, FoundationsException {

		SecurityResponse responseData =
			getBaseInformationFromMessage(rawXml, xdoc, SecurityRequestFactory.MSG_ACTION_SETUP_AND_INTRO_TEXT);

		XPath xpath = new DOMXPath("/VistaLink/Response/SetupInfo/.");
		Node setupInfoNode = (Node) xpath.selectSingleNode(xdoc);
		VistaSetupAndIntroTextInfo setupAndIntroTextInfo = new VistaSetupAndIntroTextInfo();
		setupAndIntroTextInfo.setServerName((String) XmlUtilities.getAttr(setupInfoNode, "serverName").getValue());
		setupAndIntroTextInfo.setVolume((String) XmlUtilities.getAttr(setupInfoNode, "volume").getValue());
		setupAndIntroTextInfo.setUci((String) XmlUtilities.getAttr(setupInfoNode, "uci").getValue());
		setupAndIntroTextInfo.setDevice((String) XmlUtilities.getAttr(setupInfoNode, "device").getValue());
		try {
			setupAndIntroTextInfo.setTimeout(
				Integer.valueOf((String) XmlUtilities.getAttr(setupInfoNode, "dtime").getValue()).intValue());
		} catch (java.lang.NumberFormatException e) {
			String errMsg = "No timeout value was returned from the M server.";
			if (LOGGER.isEnabledFor(Level.ERROR)) {
				LOGGER.error(errMsg, e);
			}
			throw new FoundationsException(errMsg, e);
			// default to the default timeout -- NOT -- changed to throw an exception.
			//			setupAndIntroTextInfo.setTimeout(DEFAULT_TIMEOUT);
		}
		try {
			setupAndIntroTextInfo.setLogonRetryCount(
				Integer.valueOf((String) XmlUtilities.getAttr(setupInfoNode, "numberAttempts").getValue()).intValue());
		} catch (java.lang.NumberFormatException e) {
			String errMsg = "No login retry count was returned from the M server.";
			if (LOGGER.isEnabledFor(Level.ERROR)) {
				LOGGER.error(errMsg, e);
			}
			throw new FoundationsException(errMsg, e);
			// default to the default retry count -- NOT -- changed to throw an exception.
			//			setupAndIntroTextInfo.setLogonRetryCount(DEFAULT_RETRY_COUNT);
		}
		NodeList introNodeList = xdoc.getDocumentElement().getElementsByTagName("IntroText");
		if (introNodeList.getLength() == 1) {
			NodeList cdataNodeList = introNodeList.item(0).getChildNodes();
			if ((cdataNodeList.getLength() == 1) && (cdataNodeList.item(0).getNodeType() == Node.CDATA_SECTION_NODE)) {
				setupAndIntroTextInfo.setIntroductoryText(cdataNodeList.item(0).getNodeValue());
			}
		}

		SecurityDataSetupAndIntroTextResponse setupAndIntroTextData =
			new SecurityDataSetupAndIntroTextResponse(setupAndIntroTextInfo, responseData);

		return setupAndIntroTextData;
	}

	/**
	 * Perform Security-specific fault handling and creation of Security specific FaultExceptions.
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#handleSpecificFault(org.w3c.dom.Document, java.lang.String, gov.va.med.foundations.adapter.record.VistaLinkFaultException)
	 */
	protected VistaLinkFaultException handleSpecificFault(
		Document xdoc,
		String messageType,
		VistaLinkFaultException faultException)
		throws VistaLinkFaultException, FoundationsException {

		if ("183005".equals(faultException.getErrorCode())) {
			return new SecurityTooManyInvalidLoginAttemptsFaultException(faultException);
		} else if ("183".equals(faultException.getErrorCode().substring(0, 3))) {
			return new SecurityFaultException(faultException);
		}

		return null;
	}

}
