package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkRequestFactory;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.foundations.xml.XmlUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// import x.gov.va.med.iss.log4j.*;

/**
 * Factory class to creates instances of RpcRequest
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
 * //Construct  the request object 
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
public class RpcRequestFactory implements VistaLinkRequestFactory {

	/**
	 * the logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(RpcRequestFactory.class);

	/**
	 * Method getRpcRequest.
	 * <br>Creates a RpcRequest with a null RpcContext and RpcName
	 * @return RpcRequest
	 * @throws FoundationsException
	 */
	public static RpcRequest getRpcRequest() throws FoundationsException {
		return getRpcRequest(null);
	}

	/**
	 * Method getRpcRequest.
	 * <br>Creates a RpcRequest with the specified RpcContext and a 
	 * <br>null RpcName
	 * @param rpcContext
	 * @return RpcRequest
	 * @throws FoundationsException
	 */
	public static RpcRequest getRpcRequest(String rpcContext)
		throws FoundationsException {
		return getRpcRequest(rpcContext, null);
	}

	/**
	 * Method getRpcRequest.
	 * 
	 * <br>Creates appropriate rpc request object to be passed into the
	 * connection.
	 * 
	 * @param rpcContext
	 * @param rpcName
	 * @return RpcRequest
	 * @throws FoundationsException 
	 */
	public static RpcRequest getRpcRequest(String rpcContext, String rpcName)
		throws FoundationsException {

		try {
			// Get base XML
			String baseXml = getBaseXml();
			// Retrieve xmlDoc 
			Document requestDoc = XmlUtilities.getDocumentForXmlString(baseXml);

			// Setup rpcName
			Node reqNode =
				XmlUtilities.getNode("/VistaLink/Request", requestDoc);
			Attr rpcNameAttr = XmlUtilities.getAttr(reqNode, "rpcName");
			if (rpcName != null) {
				rpcNameAttr.setValue(rpcName);
			}

			// Setup rpcClientTimeOut 
			Attr rpcClientTimeOutAttr =
				XmlUtilities.getAttr(reqNode, "rpcClientTimeOut");

			// Setup RPC params
			RpcRequestParams params = new RpcRequestParams(requestDoc);

			// Create RpcRequest VO with default XML 
			RpcRequest rpcRequest =
				new RpcRequest(
					requestDoc,
					rpcContext,
					rpcNameAttr,
					rpcClientTimeOutAttr,
					params);

			return rpcRequest;
		} catch (FoundationsException e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(
					(new StringBuffer())
						.append("Can not create RpcRequest.")
						.append("\n\t")
						.append(ExceptionUtils.getFullStackTrace(e))
						.toString());
			}
			throw e;
		}
	}

	/**
	 * Method getBaseXml
	 * <br>Returns the base xml string used to construct a RpcRequest
	 * @return String
	 */
	private static String getBaseXml() {
		return XmlUtilities.XML_HEADER
			+ "<VistaLink messageType='"
			+ RpcRequest.GOV_VA_MED_RPC_REQUEST
			+ "'"
			+ " mode='singleton'"
			+ " version='1.0'"
			+ " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
			+ " xsi:noNamespaceSchemaLocation='rpcRequest.xsd'"
		//+ " xmlns='http://med.va.gov/Foundations'"
		+">"
			+ "  <RpcHandler version='1.0'/>"
			+ "  <Request rpcName='' rpcClientTimeOut='600' version='1.0' >"
			+ "    <RpcContext></RpcContext>"
			+ "    <Params></Params>"
			+ "  </Request>"
			+ "</VistaLink>";
	}

}
