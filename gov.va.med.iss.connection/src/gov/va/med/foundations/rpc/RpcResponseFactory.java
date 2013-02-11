package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkRequestVO;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// import x.gov.va.med.iss.log4j.*;

/**
 *Implements RPC specific response parsin logic
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcResponseFactory extends VistaLinkResponseFactoryImpl {

	/**
	 * The logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(RpcResponseFactory.class);

	/**
	 * The message type used for this response
	 */
	protected static final String GOV_VA_MED_RPC_RESPONSE =
		"gov.va.med.foundations.rpc.response";

	/**
	 * Represents a rpc fault
	 */
	protected static final String GOV_VA_MED_RPC_FAULT =
		"gov.va.med.foundations.rpc.fault";

	/**
	 * Represents the strings that identifies if a RPC fault has occurred
	 */
	protected static final String ERROR_MSG_RPC =
		XmlUtilities.XML_HEADER
			+ "<VistaLink messageType=\""
			+ GOV_VA_MED_RPC_FAULT;

	/**
	 * Represents the beginning of the CDATA section
	 */
	protected static final String CDATA_BEG = "<![CDATA[";

	/**
	 * Represents the end of the CDATA section w/ the SUFFIX from
	 * VistaLinkResponseFactoryImpl
	 */
	protected static final String CDATA_END_W_SUFFIX = "]]>" + SUFFIX;

	/**
	 * Represents the end of the CDATA section wo/ the SUFFIX from
	 * VistaLinkResponseFactoryImpl
	 */
	protected static final String CDATA_END_WO_SUFFIX = "]]>";

	/**
	 * Constructor for VistaRPCResponseFactory.
	 */
	public RpcResponseFactory() {
		super();
	}

	/**
	 * 
	 * RPC specific method to parse response XML string message body and to
	 * create RpcResponse object with appropriate data.
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#parseMessageBody(java.lang.String, java.lang.String, org.w3c.dom.Document, java.lang.String, gov.va.med.foundations.adapter.record.VistaLinkRequestVO)
	 */
	protected VistaLinkResponseVO parseMessageBody(
		String rawXml,
		String filteredXml,
		Document doc,
		String messageType,
		VistaLinkRequestVO requestVO)
		throws FoundationsException {

		String cdataFromXml = getCDATAFromResponseXml(rawXml);
		String resultsType = null;

		if (messageType.equals(GOV_VA_MED_RPC_RESPONSE)) {
			XPath xpath = null;
			Node resultsNode = null;

			try {
				xpath = new DOMXPath("/VistaLink/Response/.");
				resultsNode = (Node) xpath.selectSingleNode(doc);
			} catch (JaxenException e) {

				String errStr =
					"Exception occured getting Response DOM node from response XML document.";

				if (logger.isEnabledFor(Priority.ERROR)) {
					logger.error(
						(new StringBuffer())
							.append(errStr)
							.append("\n\t")
							.append(ExceptionUtils.getFullStackTrace(e))
							.toString());
				}

				throw new FoundationsException(errStr, e);
			}

			// get result type
			NamedNodeMap attrs = resultsNode.getAttributes();
			Attr attr = (Attr) attrs.getNamedItem("type");
			resultsType = attr.getValue().toLowerCase();
		} else {
			throw new FoundationsException("Illegal Response Format Returned");
		}

		RpcResponse rpcResponse =
			new RpcResponse(
				rawXml,
				filteredXml,
				doc,
				messageType,
				cdataFromXml,
				resultsType);

		RpcRequest rpcRequest = (RpcRequest) requestVO;
		if (rpcRequest.isXmlResponse()) {
			rpcResponse.setResultsType("xml");
		}

		return rpcResponse;
	}

	/**
	 * 
	 * Performs additional response XML string parsing to check if fault occured
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#doesResponseIndicateFault(java.lang.String)
	 */
	protected boolean doesResponseIndicateFault(String rawXml) {
		return rawXml.startsWith(ERROR_MSG_RPC);
	}

	/**
	 * 
	 * Filters response XML string specific to RPC implementation - removes CDATA
	 * from within the response XML.
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#filterResponseXmlString(java.lang.String, boolean)
	 */
	protected String filterResponseXmlString(String rawXml, boolean isFault) {
		return removeCDATAFromResponseXml(rawXml, isFault);
	}

	/**
	 * Method removeCDATAFromResponseXml.
	 * <br>Removes the CDATA section from the raw XML
	 * 
	 * @param rawXml
	 * @param isFault
	 * @return String
	 */
	private String removeCDATAFromResponseXml(String rawXml, boolean isFault) {
		int cdataIndex = getCDATAStartIndex(rawXml);

		if ((cdataIndex > -1) && (!isFault)) {
			//			<dontknow>
			//			int cdataEndIndex = rawXml.
			//				lastIndexOf(CDATA_END_WO_SUFFIX) 
			//			   + CDATA_END_WO_SUFFIX.length();
			//
			//			xmlNoCData = ((new StringBuffer())
			//				.append(rawXml.substring(0, cdataIndex))
			//				.append(rawXml.substring(cdataEndIndex))).toString();
			//			<dontknow>
			String xmlNoCData = rawXml.substring(0, cdataIndex) + SUFFIX;

			return xmlNoCData;
		} else {
			return rawXml;
		}
	}

	/**
	 * Method getCDATAFromResponseXml.
	 * <br>Gets the CDATA section from the raw XML
	 * 
	 * @param rawXml
	 * @return String
	 */
	private String getCDATAFromResponseXml(String rawXml) {
		int cdataStartIndex = getCDATAStartIndex(rawXml);

		String retStr = null;
		if (cdataStartIndex > -1) {
			int cdataEndIndex = getCDATAEndIndex(rawXml);
			retStr =
				rawXml.substring(
					cdataStartIndex + CDATA_BEG.length(),
					cdataEndIndex);
		}
		return retStr;
	}

	/**
	 * Method getCDATAStartIndex.
	 * <br>Gets the start of the CDATA section
	 * @param rawXml
	 * @return int
	 */
	private int getCDATAStartIndex(String rawXml) {
		return rawXml.indexOf(CDATA_BEG);
	}

	/**
	 * Method getCDATAEndIndex.
	 * <br>Gets the end of the CDATA section
	 * @param rawXml
	 * @return int
	 */
	private int getCDATAEndIndex(String rawXml) {
		return rawXml.indexOf(CDATA_END_W_SUFFIX);
	}

	/**
	 * 
	 * Perform RPC specific fault handling and creation of RPC specific
	 * FaultExceptions.
	 * 
	 * 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkResponseFactoryImpl#handleSpecificFault(org.w3c.dom.Document, java.lang.String, gov.va.med.foundations.adapter.record.VistaLinkFaultException)
	 */
	protected VistaLinkFaultException handleSpecificFault(
		Document xdoc,
		String messageType,
		VistaLinkFaultException faultException)
		throws VistaLinkFaultException, FoundationsException {

		if ("182005".equals(faultException.getErrorCode())) {
			return new RpcNotInContextFaultException(faultException);
		} else if ("182006".endsWith(faultException.getErrorCode())) {
			return new NoRpcContextFaultException(faultException);
		} else if ("182007".endsWith(faultException.getErrorCode())) {
			return new RpcTimeOutFaultException(faultException);
		} else if (
			"182".equals(faultException.getErrorCode().substring(0, 3))) {
			return new RpcFaultException(faultException);
		}

		return null;
	}

}
