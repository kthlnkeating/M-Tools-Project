package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.adapter.record.VistaLinkRequestFactory;
import gov.va.med.foundations.utilities.VistaKernelHash;
import gov.va.med.foundations.utilities.VistaKernelHashCountLimitExceededException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates Security module message requests, to be sent to an M VistA server for processing and response.
 * @see SecurityRequest
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */

abstract class SecurityRequestFactory implements VistaLinkRequestFactory {

	private static final String GOV_VA_MED_SECURITY_VERSION = "1.0";
	private static final String VISTALINK_VERSION = "1.0";

	private static final String GOV_VA_MED_SECURITY_REQUEST = "gov.va.med.foundations.security.request";
	private static final String NAMESPACE_XSI = "http://www.w3.org/2001/XMLSchema-instance";

	private static final String SCHEMA_SELECT_DIVISION = "secDivisionRequest.xsd";
	private static final String SCHEMA_LOGON = "secLogonRequest.xsd";
	private static final String SCHEMA_UPDATE_VC = "secUpdateVerifyRequest.xsd";
	private static final String SCHEMA_SIMPLE = "secSimpleRequest.xsd";

	/**
	 * The exact message type string for the setup/intro text request message type
	 */
	public static final String MSG_ACTION_SETUP_AND_INTRO_TEXT = "AV.SetupAndIntroText";
	/**
	 * The exact message type string for the logon request message type
	 */
	public static final String MSG_ACTION_LOGON = "AV.Logon";
	/**
	 * The exact message type string for the logout request message type
	 */
	public static final String MSG_ACTION_LOGOUT = "AV.Logout";
	/**
	 * The exact message type string for the select division request message type
	 */
	public static final String MSG_ACTION_SELECT_DIVISION = "AV.SelectDivision";
	/**
	 * The exact message type string for the update verify code request message type
	 */
	public static final String MSG_ACTION_UPDATE_VC = "AV.UpdateVC";
	/**
	 * The exact message type string for the get user demographics request message type
	 */
	public static final String MSG_ACTION_USER_DEMOGRAPHICS = "AV.GetUserDemographics";
	/**
	 * The exact message type string for the setup/intro text message type
	 */
	static final String CRLF = "\n";

	/**
	 * Returns the XML string for a valid AV.SetupAndIntroText.Request message
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 */
	static SecurityRequest getAVSetupAndIntroTextRequest() throws ParserConfigurationException {
		Document requestDoc = getBaseDoc(MSG_ACTION_SETUP_AND_INTRO_TEXT, SCHEMA_SIMPLE);
		return new SecurityRequest(requestDoc);
	}

	/**
	 * Returns the XML string for a valid AV.Logout.Request message
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 */
	static SecurityRequest getAVLogoutRequest() throws ParserConfigurationException {
		Document requestDoc = getBaseDoc(MSG_ACTION_LOGOUT, SCHEMA_SIMPLE);
		return new SecurityRequest(requestDoc);
	}
	/**
	 * Returns the XML string for a valid AV.GetUserDemographics.Request message
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 */
	static SecurityRequest getAVGetUserDemographicsRequest() throws ParserConfigurationException {
		Document requestDoc = getBaseDoc(MSG_ACTION_USER_DEMOGRAPHICS, SCHEMA_SIMPLE);
		return new SecurityRequest(requestDoc);
	}

	/**
	 * Returns the XML string for a valid AV.Logon.Request message, given a CCOW security token in lieu of access/verify
	 * code
	 * @param token CCOW security token returned by Kernel for non-interactive single signon
	 * @return SecurityRequest String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 */
	static SecurityRequest getAVLogonRequest(String token) throws ParserConfigurationException {
		return getAVLogonRequestInternal(token, false);
	}

	/**
	 * Returns the XML string for a valid AV.Logon.Request message, given an access and verify code
	 * @param ac access code to log on with.
	 * @param vc verify code to log on with.
	 * @param requestCvc true if the user has requested to change their verify code
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 * @throws VistaKernelHashCountLimitExceededException thrown if error encountered encoding access/verify codes
	 */
	static SecurityRequest getAVLogonRequest(String ac, String vc, boolean requestCvc)
		throws ParserConfigurationException, VistaKernelHashCountLimitExceededException {
			
		return getAVLogonRequestInternal(VistaKernelHash.encrypt(ac + ";" + vc, true), requestCvc);
		
	}

	private static SecurityRequest getAVLogonRequestInternal(String encodedLogonString, boolean requestCvc)
		throws ParserConfigurationException {

		Document requestDoc = getBaseDoc(MSG_ACTION_LOGON, SCHEMA_LOGON);

		Element rootElement = requestDoc.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("Request")) {
				Element avCodesElement = requestDoc.createElement("avCodes");
				if (requestCvc) {
					avCodesElement.setAttribute("requestCvc", "true");
				}
				CDATASection cdata = requestDoc.createCDATASection(encodedLogonString);
				avCodesElement.appendChild(cdata);
				node.appendChild(avCodesElement);
				break;
			}
		}

		return new SecurityRequest(requestDoc);
	}

	/**
	 * Returns the XML string for a valid AV.LogonSelectDivision.Request message
	 * @param logonDivisionIen the FileMan IEN (internal entry number) of the division to select, 
	 * from the Vista Kernel Institution file.
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 */
	static SecurityRequest getAVLogonSelectDivisionRequest(String logonDivisionIen)
		throws ParserConfigurationException {

		Document requestDoc = getBaseDoc(MSG_ACTION_SELECT_DIVISION, SCHEMA_SELECT_DIVISION);

		Element rootElement = requestDoc.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("Request")) {
				Element logonDivisionElement = requestDoc.createElement("Division");
				logonDivisionElement.setAttribute("ien", logonDivisionIen);
				node.appendChild(logonDivisionElement);
				break;
			}
		}
		return new SecurityRequest(requestDoc);
	}

	/**
	 * Returns the XML string for a valid AV.UpdateVC.Request message
	 * @param oldVc the old verify code.
	 * @param newVc the new verify code to change to.
	 * @param newVcCheck the check for the new verify code being changed to
	 * @return String String representation of the XML document
	 * @throws ParserConfigurationException thrown if DOM errors encountered creating message
	 * @throws VistaKernelHashCountLimitExceededException thrown if an error is encountered encoding the old, new
	 * and check verify codes
	 */
	static SecurityRequest getAVUpdateVCRequest(String oldVc, String newVc, String newVcCheck)
		throws ParserConfigurationException, VistaKernelHashCountLimitExceededException {

		Document requestDoc = getBaseDoc(MSG_ACTION_UPDATE_VC, SCHEMA_UPDATE_VC);

		Element rootElement = requestDoc.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("Request")) {

				Element oldVcElement = requestDoc.createElement("oldVc");
				CDATASection cdata = requestDoc.createCDATASection(VistaKernelHash.encrypt(oldVc, true));
				oldVcElement.appendChild(cdata);
				node.appendChild(oldVcElement);

				Element newVcElement = requestDoc.createElement("newVc");
				cdata = requestDoc.createCDATASection(VistaKernelHash.encrypt(newVc, true));
				newVcElement.appendChild(cdata);
				node.appendChild(newVcElement);

				Element confirmedVcElement = requestDoc.createElement("confirmedVc");
				cdata = requestDoc.createCDATASection(VistaKernelHash.encrypt(newVcCheck, true));
				confirmedVcElement.appendChild(cdata);
				node.appendChild(confirmedVcElement);

				break;
			}
		}

		return new SecurityRequest(requestDoc);

	}

	/**
	 * Returns a base security document with the XML header tag, and with the VistaLink root element
	 * @param action The type of security document to create
	 * @return Document a DOM document with the root VistaLink tagset
	 * @throws ParserConfigurationException if there's an error creating the base document
	 */
	private static Document getBaseDoc(String action, String schema) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document newDoc = builder.newDocument();
		// VistaLink element
		Element root = (Element) newDoc.createElement("VistaLink");
		root.setAttribute("xmlns:xsi", NAMESPACE_XSI);
		root.setAttribute("xsi:noNamespaceSchemaLocation", schema);
		// enable xmlns attribute in the future?
		// root.setAttribute("xmlns", XMLNS);
		root.setAttribute("messageType", GOV_VA_MED_SECURITY_REQUEST);
		root.setAttribute("version", VISTALINK_VERSION);
		root.setAttribute("mode", "singleton");
		newDoc.appendChild(root);
		// SecurityInfo
		Element securityInfoElement = newDoc.createElement("SecurityInfo");
		securityInfoElement.setAttribute("version", GOV_VA_MED_SECURITY_VERSION);
		root.appendChild(securityInfoElement);
		// Request
		Element requestTypeElement = newDoc.createElement("Request");
		requestTypeElement.setAttribute("type", action);
		root.appendChild(requestTypeElement);
		return newDoc;
	}
}