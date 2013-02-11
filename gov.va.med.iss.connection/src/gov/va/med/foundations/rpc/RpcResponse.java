package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkResponseVOImpl;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.w3c.dom.Document;

// import x.gov.va.med.iss.log4j.*;

/**
 * Represents a data structure which holds the response value(s).
 * <br><br>
 * <br>It is extremely important that any code which might create a new
 * <br>RpcResponse be encased in a try catch block so that
 * <br>VistaLinkfaultException and FoundationsException can be caught. 
 * 
 *<br>J2SE Example
 *<br>
 *<pre> 
 *
 * // create the callback handler
 * CallbackHandlerSwing cbhSwing = new CallbackHandlerSwing(myFrame);
 * 
 * // create the LoginContext  
 * loginContext = new LoginContext("Production", cbhSwing);
 * 
 * // login to server
 * loginContext.login(); 
 * 
 * //Gets the principal that contains the VistaLinkConnection
 * VistaKernelPrincipalImpl myPrincipal = VistaKernelPrincipalImpl.
 * 			getKernelPrincipal(loginContext.getSubject());
 * 
 * //Get the VistaLinkConnection
 * VistaLinkConnection myConnection = myPrincipal.getAuthenticatedConnection();
 * 
 *  //request  and response objects 
 * RpcRequest vReq = null; 
 * RpcResponse vResp = null;
 * 
 * //The Rpc Context
 * String rpcContext = "XOBV VISTALINK TESTER";
 * 
 * //The Rpc to call
 * String rpcName = "XOBV TEST PING";
 * 
 *  //Construct the request object
 * vReq = RpcRequestFactory.getRpcRequest(rpcContext, rpcName);
 * 
 * 
 * <b>   
 * //Execute   the Rpc and get the response 
 * vResp = myConnection.executeRPC(vReq);
 * 
 *  //Display  the response 
 * System.out.println(vResp.getRawResponse());
 * </b>
 * 
 * </pre>
 *   
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */

public class RpcResponse extends VistaLinkResponseVOImpl {

	/**
	 * The logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(RpcResponse.class);

	/**
	 * The message type used for this response
	 */
	protected static final String GOV_VA_MED_RPC_RESPONSE =
		"gov.va.med.foundations.rpc.response";

	/**
	 * The message type used to indicate RPC fault
	 */
	protected static final String GOV_VA_MED_RPC_FAULT =
		"gov.va.med.foundations.rpc.fault";

	/**
	 * The message type used to indicate a system/foundations fault
	 */
	protected static final String GOV_VA_MED_FOUNDATIONS_FAULT =
		"gov.va.med.foundations.vistalink.system.fault";

	/**
	 * The message type used to indicate a security fault
	 */
	protected static final String GOV_VA_MED_FOUNDATIONS_SECURITY_FAULT =
		"gov.va.med.foundations.security.fault";

	/**
	 * Represents the string that idenitifies if a Foundations fault has
	 * <br>occurred
	 */
	private static final String ERROR_MSG_GEN =
		XmlUtilities.XML_HEADER
			+ "<VistaLink messageType=\""
			+ GOV_VA_MED_FOUNDATIONS_FAULT
			+ "\" >";

	/**
	 * Represents the string that idenitifies if a RPC fault has
	 * <br>occurred
	 */
	private static final String ERROR_MSG_RPC =
		XmlUtilities.XML_HEADER
			+ "<VistaLink messageType=\""
			+ GOV_VA_MED_RPC_FAULT
			+ "\" >";

	/**
	 * Represents the string that idenitifies if a Security fault has
	 * <br>occurred
	 */
	private static final String ERROR_MSG_SEC =
		XmlUtilities.XML_HEADER
			+ "<VistaLink messagetype=\""
			+ GOV_VA_MED_FOUNDATIONS_SECURITY_FAULT
			+ "\" >";

	/**
	 * Represents the suffix portion of the xml response
	 */
	private static final String SUFFIX = "</Response></VistaLink>";

	/**
	 * Represents the beginning of the CDATA section
	 */
	private static final String CDATA_BEG = "<![CDATA[";

	/**
	 * Represents the end of the CDATA section with the SUFFIX
	 */
	private static final String CDATA_END = "]]>" + SUFFIX;

	/**
	 * Represents the results returned in the response
	 */
	private String results;

	/**
	 * Identifies the results type
	 */
	private String resultsType;

	/**
	 * Represents the extracted CDATA
	 */
	private String cdataFromXml = null;

	/**
	 * Represents extracted CDATA as a DOM document
	 */
	private Document cdataDocument = null;

	/**
	 * Constructor RpcResponse.
	 * @param rawXml
	 * @param filteredXml
	 * @param doc
	 * @param messageType
	 * @param cdataFromXml
	 * @param resultsType
	 */
	protected RpcResponse(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		String cdataFromXml,
		String resultsType) {

		// call a superclass constructor
		super(rawXml, filteredXml, doc, messageType);

		// set RPC specific properties				
		this.cdataFromXml = cdataFromXml;
		this.resultsType = resultsType;
	}

	/**
	 * Gets the &lt;results&gt; node. The node(s) returned by the RPC will be sub-nodes of &lt;results&gt.
	 * <br><br>
	 * Note: It must be known beforehand that the data being returned is
	 *       in the appropriate format to be converted to a XML Node(s).
	 * @return Node
	 * @throws RpcResponseTypeIsNotXmlException results type must be 'xml'
	 */
	//	public Node getResultsNode() throws Exception, RpcResponseTypeIsNotXmlException {
	//		if (this.resultsType.equals("xml")) {
	//			try {
	//				return XmlUtilities
	//					.getDocumentForXmlString(getBaseXml())
	//					.getDocumentElement();
	//			} catch (Exception e) {
	//				throw e;
	//			}
	//		} else {
	//			throw new RpcResponseTypeIsNotXmlException("Illegal method call. Results type is not 'xml'.");
	//		}
	//	}

	//	private String getBaseXml() throws RpcResponseTypeIsNotXmlException {
	//		return XmlUtilities.XML_HEADER
	//			+ "<results> "
	//			+ getResults()
	//			+ "</results> ";
	//	}

	/**
	 * Gets an XML Document format based on the contains of the results returned by the RPC.
	 * <br><br>
	 * Note: This XML document is created during the call to this method and not as part of the
	 * creation of the RpcResponse object. 
	 * <br>
	 * 
	 * If calling application wants to use this method, it should use generic
	 * xml DOM interfaces from org.w3c.dom.* package. <br>
	 * 
	 * Alternatively if application wants to use this document in a specific
	 * XML parser implementation, parser should be able to create a specific
	 * Document implementation from org.w3c.dom.Document interface. In this case
	 * it might be better from performance standpiont to use getResults() and
	 * parse xml string directly.
	 * 
	 * @return org.w3c.dom.Document
	 * @throws RpcResponseTypeIsNotXmlException results type must be 'xml'
	 * @throws FoundationsException 
	 */
	public Document getResultsDocument()
		throws RpcResponseTypeIsNotXmlException, FoundationsException {
		try {
			if (this.resultsType.equals("xml")) {
				if (cdataDocument == null) {
					cdataDocument =
						XmlUtilities.getDocumentForXmlString(getResults());
				}
				return cdataDocument;
			} else {
				throw new RpcResponseTypeIsNotXmlException("Illegal method call. Results type is not 'xml'.");
			}
		} catch (FoundationsException e) {

			if (logger.isEnabledFor(Priority.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append("Can not get results Document.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			} 
			throw e;
		}
	}

	/**
	 * Gets the results string for the returned data in this response.
	 * @return String
	 */
	public String getResults() {
		return cdataFromXml;
	}

	//	<dontknow>
	/**
	 * Method getCDATAResultsXml.
	 * <br>Returns the CDATA section of the response
	 * <br> as an Xml String constructed by custom LineByLineXMLHandler
	 * @param hdlr
	 * @return String
	 * @throws RpcResponseTypeIsNotXmlException
	 * @throws Exception
	 */
	//	public String getCDATAResultsXml(LineByLineXMLHandler hdlr)
	//		throws RpcResponseTypeIsNotXmlException, Exception {
	//		if (!(this.resultsType.equals("xml"))) {
	//			try {
	//
	//				return XMLTransformer.LineByLineTransform(hdlr, getResults());
	//
	//			} catch (Exception e) {
	//				throw e;
	//			}
	//		} else {
	//			throw new RpcResponseTypeIsNotXmlException("Illegal method call. Results type is 'xml'.");
	//		}
	//	}
	//	<dontknow>

	//	<dontknow>
	/**
	 * Method getCDATAResultsXml.
	 * <br> Returns the CDATA section of the response as an XML
	 * <br> string constructed by default LineByLineXMLHandler
	 * @return String
	 * @throws RpcResponseTypeIsNotXmlException
	 * @throws Exception
	 */
	//	public String getCDATAResultsXml()
	//		throws RpcResponseTypeIsNotXmlException, Exception {
	//		if (!(this.resultsType.equals("xml"))) {
	//			try {
	//				return getCDATAResultsXml(new LineByLineXMLHandler());
	//			} catch (Exception e) {
	//				throw e;
	//			}
	//		} else {
	//			throw new RpcResponseTypeIsNotXmlException("Illegal method call. Results type is 'xml'.");
	//		}
	//	}
	//	<dontknow>

	/**
	 * Gets the return type of the results sent back from the M VistAServer.
	 * At the present time (04/2002) the possible types are 'string' or 'array'.
	 * @return String
	 */
	public String getResultsType() {
		return resultsType;
	}

	/**
	 * Sets the resultsType.
	 * @param resultsType The resultsType to set
	 * @deprecated This method will be removed after the REMOTE PROCEDURE (#8894) file adds an XML return type.
	 */
	public void setResultsType(String resultsType) {
		this.resultsType = resultsType;
	}

}