package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkRequestVOImpl;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.utilities.VistaKernelHash;
import gov.va.med.foundations.xml.XmlUtilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// import x.gov.va.med.iss.log4j.*;

/**
 * Represents a RPC request to an M VistA server.
 * <br><br>
 * This is the principal class of VLJ used by developers to create and setup
 * requests to the host M server.
 * 
 * <br>J2SE Example
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
 * <b>
 *  //Construct    the request object 
 * vReq = RpcRequestFactory.getRpcRequest(rpcContext, rpcName);
 * </b>
 * 
 * //Execute the Rpc and get the respnse
 * vResp = myConnection.executeRPC(vReq);
 * 
 * //Work with the response ...
 * 
 * 
 * </pre>
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcRequest extends VistaLinkRequestVOImpl {
	/**
	 * The logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(RpcRequest.class);

	/**
	 * The message type used for this Request
	 */
	protected static final String GOV_VA_MED_RPC_REQUEST =
		"gov.va.med.foundations.rpc.request";

	@SuppressWarnings("unused")
	/**
	 * String representing a line feed
	 */
	private static final String CRLF = "\n";

	/**
	 * The version of the VistaLink message
	 */
	private static final String VISTALINK_VERSION = "1.0";

	/**
	 * The version of the RPC Handler on M
	 */
	private static final String RPC_HANDLER_VERSION = "1.0";

	/**
	 * The name of the RPC that acts as a Sink for message sent with
	 * <br>the proprietary format
	 */
	private static final String SINK_NAME = "XOB RPC";

	/**
	 * The padding used to delimit values in the proprietary message format
	 */
	private static final String SINK_PAD = "00000";

	/**
	 * The length of the SINK_PAD
	 */
	private static final int SINK_PAD_LEN = SINK_PAD.length();

	/**
	 * Identifies whether the request will be made using the proprietary
	 * <br>message format or XML
	 */
	private boolean useProprietaryMessageFormat = true;

	/**
	 * Identifies whether debugging is on or off
	 */
	private boolean debuggingOn = false;

	/**
	 * The parameters associated with the RPC that this request represents
	 */
	private RpcRequestParams params;

	/**
	 * Identifies the name of the RPC to executed on the M server
	 */
	private Attr rpcName;

	/**
	 * Identifies the client time out for the RPC
	 */
	private Attr rpcClientTimeOut;

	/**
	 * Identifies the RpcContext
	 */
	private String rpcContext;

	/**
	 * Identifies whether the response will be XML format
	 */
	private boolean xmlResponse = false;

	/**
	 * Method RpcRequest.
	 * @param requestDoc
	 * @param rpcContext
	 * @param rpcName
	 * @param rpcClientTimeOut
	 * @param params
	 * @throws FoundationsException
	 */
	protected RpcRequest(
		Document requestDoc,
		String rpcContext,
		Attr rpcName,
		Attr rpcClientTimeOut,
		RpcRequestParams params)
		throws FoundationsException {
		super(requestDoc);

		if (rpcContext != null) {
			setRpcContext(rpcContext);
		} else {
			setRpcContext("");
		}

		this.rpcName = rpcName;
		this.rpcClientTimeOut = rpcClientTimeOut;
		this.params = params;

	}

	/**
	 * Method getParams.
	 * <br>Gets the reference to the {@link RpcRequestParams} object 
	 * <br>associated with this request.
	 * <br>This object contains the parameters sent with the call to the RPC
	 * <br>during the getResponse() call. Use this object to set these
	 * <br>parameters before calling getResponse().
	 * @return RpcRequestParams
	 */
	public RpcRequestParams getParams() {
		return params;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Method setParams.
	 * <br>Sets all the paramters for a RPC call at once using a List.
	 * @param list
	 */
	public void setParams(List list) {
		Object obj;
		for (int item = 0; item < list.size(); item++) {
			obj = list.get(item);
			if (obj instanceof Map || obj instanceof Collection) {
				params.setParam(item + 1, "array", obj);
			} else if (obj instanceof RpcReferenceType) {
				params.setParam(item + 1, "ref", obj);
			} else {
				params.setParam(item + 1, "string", obj);
			}
		}
	}

	/**
	 * Method clearParams.
	 * <br>Clears the params associated with this instance of RpcRequest
	 */
	public void clearParams() {
		Node reqNode = XmlUtilities.getNode("/VistaLink/Request", requestDoc);
		reqNode.removeChild(
			XmlUtilities.getNode("/VistaLink/Request/Params", requestDoc));
		reqNode.appendChild(requestDoc.createElement("Params"));
		params = new RpcRequestParams(requestDoc);
	}

	/**
	 * Method getRpcName.
	 * <br>Gets the name of the RPC.
	 * @return String
	 */
	public String getRpcName() {
		return rpcName.getValue();
	}

	/**
	 * Method setRpcName.
	 * <br>Sets the name of the RPC to be called on the M server. The name must be a 
	 * <br>valid RPC name as it appears in the REMOTE PROCEDURE (#8994) 
	 * <br>file in M VistA.
	 * @param value
	 */
	public void setRpcName(String value) {
		rpcName.setValue(value);
	}

	/**
	 * Method getRpcContext.
	 * <br>Gets the name of the RPC Context.
	 * @return String
	 */
	public String getRpcContext() {
		return rpcContext;
	}

	/**
	 * Method setRpcContext.
	 * <br>Sets the name of the RPC Context to be used. The name must be 
	 * <br>a valid B- type OPTION name as it appears in the OPTION (#19) 
	 * <br>file in M VistA.
	 * @param value
	 * @throws FoundationsException
	 */
	public void setRpcContext(String value) throws FoundationsException {
		try {
			rpcContext = value;
			Node node =
				XmlUtilities.getNode(
					"/VistaLink/Request/RpcContext",
					requestDoc);

			String encryptedRpcContext =
				VistaKernelHash.encrypt(rpcContext, true);
			CDATASection cdata =
				requestDoc.createCDATASection(encryptedRpcContext);

			Node currentRpcContextNode = node.getFirstChild();
			if (currentRpcContextNode != null) {
				node.removeChild(currentRpcContextNode);
			}
			node.appendChild(cdata);
		} catch (FoundationsException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append("Can not set RpcContext.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw e;
		}
	}

	/**
	 * Method getRpcClientTimeOut.
	 * <br>Gets the current client time out value. (Value is returned in 
	 * <br>the number of seconds).
	 * @return int
	 */
	public int getRpcClientTimeOut() {
		return Integer.parseInt(rpcClientTimeOut.getValue());
	}

	/**
	 * Method setRpcClientTimeOut.
	 * <br>Sets the client time out value. (Value is expected in seconds.)
	 * @param value
	 */
	public void setRpcClientTimeOut(int value) {
		rpcClientTimeOut.setValue(String.valueOf(value));
	}

	/**
	 * <br>Gets the request string that will be used to make the request
	 * <br>from M. This method will return the propietary message format
	 * <br>if useProprietaryMessageFormat is true, otherwise it will
	 * <br>return a XML string.
	 * @see gov.va.med.foundations.adapter.record.VistaLinkRequestVO#getRequestString()
	 */
	public String getRequestString() throws FoundationsException {
		try {
			String reqStr;
			if (useProprietaryMessageFormat) {
				reqStr = getFormatterSinkStr();
			} else {
				reqStr = super.getRequestString();
			}
			return reqStr;
		} catch (FoundationsException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append("Can not get Request String from RpcRequestObject.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Method getFormatterSinkStr.
	 * <br>Gets the proprietary request string
	 * @return String
	 * @throws FoundationsException
	 */
	private String getFormatterSinkStr() throws FoundationsException {
		StringBuffer sb = new StringBuffer();
		Node param, item = null;
		NamedNodeMap attrs = null;
		String xpathStr;
		XPath xpath = null;
		List items = null;
		Attr attr = null;
		Node paramsNode = params.getParams();
		Iterator iter = null;
		String type, position;

		// set sink name ; must be in sync with message type  on M server
		String lenSinkName = String.valueOf(SINK_NAME.length());
		sb.append(
			"00".substring(0, 2 - lenSinkName.length())
				+ lenSinkName
				+ SINK_NAME);

		// send debugging byte
		if (this.debuggingOn) {
			sb.append("1");
		} else {
			sb.append("0");
		}

		// send length string size
		sb.append(SINK_PAD_LEN);

		// send VistaLink version
		append("VLV", convert(VISTALINK_VERSION), sb);

		// send RpcHandler version
		append("RHV", convert(RPC_HANDLER_VERSION), sb);

		// send RPC name
		append("RPC", convert(rpcName.getValue()), sb);

		// send RPC Context name
		try {
			append(
				"RCX",
				convert(VistaKernelHash.encrypt(rpcContext, true)),
				sb);
		} catch (FoundationsException e) {
			String errStr = "Can not encrypt rpcContext to be set to RCX.";

			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append(errStr)
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}

			throw new FoundationsException(errStr, e);
		}

		// send RPC Client Time Out
		append("RTO", convert(rpcClientTimeOut.getValue()), sb);

		// indicate how many parameters are being sent
		NodeList paramNodes = paramsNode.getChildNodes();
		append("PMS", convert(paramNodes.getLength()), sb);

		// send parameters
		for (int i = 0; i < paramNodes.getLength(); i++) {
			param = paramNodes.item(i);
			attrs = param.getAttributes();

			attr = (Attr) attrs.getNamedItem("type");
			type = attr.getNodeValue();
			append("TYP", convert(type), sb);

			attr = (Attr) attrs.getNamedItem("position");
			position = attr.getNodeValue();
			append("POS", convert(position), sb);

			// send items for array parameter or value
			if (type.toLowerCase().equals("array")) {
				xpathStr = "//Indices/Index";
				try {
					xpath = new DOMXPath(xpathStr);
					items = xpath.selectNodes(param);
				} catch (JaxenException e) {
					String errStr =
						"Can not send items for array parameter or value.";
					if (logger.isEnabledFor(Level.ERROR)) {
						logger.error(
							(new StringBuffer())
								.append(errStr)
								.append("\n\t")
								.append(ExceptionUtils.getFullStackTrace(e))
								.toString());
					}
					throw new FoundationsException(errStr, e);
				}
				iter = items.iterator();

				// indicate how many items in the array
				append("ITS", convert(items.size()), sb);

				// send subscripts and values
				while (iter.hasNext()) {
					item = (Node) iter.next();
					append(
						"SUB",
						convert(XmlUtilities.getAttr(item, "name").getValue()),
						sb);
					append(
						"VAL",
						convert(XmlUtilities.getAttr(item, "value").getValue()),
						sb);
				}
			} else {
				// send standalone value
				append(
					"VAL",
					convert(param.getChildNodes().item(0).getNodeValue()),
					sb);
			}
		}
		return sb.toString();
	}

	/**
	 * Method append.
	 * <br>Helper method to concatenate strings 
	 * @param label
	 * @param value
	 * @param sb
	 */
	private void append(String label, String value, StringBuffer sb) {
		if (this.debuggingOn) {
			sb.append(label).append("=").append(value);
		} else {
			sb.append(value);
		}
	}

	/**
	 * Method convert.
	 * <br>Converts a string to a proprietary formatted string
	 * @param str
	 * @return String
	 */
	private String convert(String str) {
		String lenStr = String.valueOf(str.length());
		return SINK_PAD.substring(0, SINK_PAD_LEN - lenStr.length())
			+ lenStr
			+ str;
	}

	/**
	 * Method convert.
	 * <br>Converts an int to a proprietary formatted string
	 * @param cnt
	 * @return String
	 */
	private String convert(int cnt) {
		String lenStr = String.valueOf(String.valueOf(cnt).length());
		return SINK_PAD.substring(0, SINK_PAD_LEN - lenStr.length())
			+ lenStr
			+ cnt;
	}

	/**
	 * Method isXmlResponse.
	 * <br>Indicates whether the returned value from the RPC call is expected in XML format or not.
	 * @return boolean
	 * @deprecated This method will be removed after the REMOTE PROCEDURE (#8894) file adds an XML return type.
	 */
	public boolean isXmlResponse() {
		return xmlResponse;
	}

	/**
	 * Method setXmlResponse.
	 * <br>Sets the indicator that the returned value from the RPC call is expected in XML format or not.
	 * @deprecated This method will be removed after the REMOTE PROCEDURE
	 * (#8894) file adds an XML return type.
	 * @param value Whether XML is the expected type of result to be returned.
	 * @deprecated This method will be removed after the REMOTE PROCEDURE (#8894) file adds an XML return type.
	 */
	public void setXmlResponse(boolean value) {
		this.xmlResponse = value;
	}
	/**
	 * Method isUseProprietaryMessageFormat.
	 * <br>Indicates whether the RPC request should be sent to the M server in a proprietary format (true)
	 *  or in XML format (false).
	 * @return boolean
	 * @deprecated	For internal testing only!
	 */
	public boolean isUseProprietaryMessageFormat() {
		return useProprietaryMessageFormat;
	}

	/**
	 * Method setUseProprietaryMessageFormat.
	 * <br>Set the Indicator that the RPC request should be sent to the M server in a proprietary format (true)
	 *  or in XML format (false).
	 * <br>Sets the useProprietaryMessageFormat.
	 * @param useSink
	 */
	public void setUseProprietaryMessageFormat(boolean useSink) {
		this.useProprietaryMessageFormat = useSink;
	}

}