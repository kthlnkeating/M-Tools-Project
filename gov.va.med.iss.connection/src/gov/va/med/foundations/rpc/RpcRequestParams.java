package gov.va.med.foundations.rpc;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.xml.XmlUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// import x.gov.va.med.iss.log4j.*;

/**
 * Represents the the collection of parameters associated with an RPC.
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
 * String rpcName = "XOBV TEST STRING";
 * 
 *  //Construct the request object
 * vReq = RpcRequestFactory.getRpcRequest(rpcContext, rpcName);
 * 
 * <b>
 * //clear the params 	
 * vReq.clearParams();
 * 
 * //Set the params
 * vReq.getParams(). setParam(1, "string", "This is a test string!");
 * </b>
 * 
 * //Execute the Rpc and get the respnse
 * vResp = myConnection.executeRPC(vReq);
 * 
 * //Work with the response ...
 * 
 * 
 * </pre>
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcRequestParams {

	/**
	 * The logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(RpcRequestParams.class);

	/**
	 * The DOM document representing the RpcRequest
	 */
	private Document requestDoc = null;

	/**
	 * The node representing the params for the RpcRequest
	 */
	private Node params = null;

	/**
	 * Method RpcRequestParams.
	 * <br>Constructs the Rpc Params
	 * @param requestDoc
	 */
	protected RpcRequestParams(Document requestDoc) {
		this.requestDoc = requestDoc;
		this.params =
			XmlUtilities.getNode("/VistaLink/Request/Params", requestDoc);
	}

	/**
	 * Method getParams.
	 * <br>Returns the params node
	 * @return Node
	 */
	protected Node getParams() {
		return params;
	}

	/**
	 * Method getParam.
	 * <br>Gets the value for a parameter associated with a specified 
	 * <br>position in the parameters collection.
	 * <br>Normally, this method is not used by the client.
	 * <br>A return value of <b>null</b> indicates the there is no parameter
	 * for the position. .
	 * @param position		parameter position the M RPC expects this parameter
	 * @return Object	String or Map
	 */
	public Object getParam(int position) {
		Node param, index;
		HashMap map;

		param =
			XmlUtilities.getNode(
				"//Param[@position='" + position + "']",
				params);

		// return null if no parameter at the position
		if (param == null) {
			return null;
		}

		String type = XmlUtilities.getAttr(param, "type").getValue();

		if (type.toLowerCase().equals("array")) {
			String xpathStr = "//Indices/Index";

			XPath xpath = null;
			List indices = null;

			try {
				xpath = new DOMXPath(xpathStr);
				indices = xpath.selectNodes(param);
			} catch (JaxenException e) {
				// should we not throw an Exception since this is not really an error, or is it?
				String errStr =
					"Can not get parameter Indices Node from request DOM document.";

				if (logger.isEnabledFor(Priority.ERROR)) {
					logger.error(
						(new StringBuffer())
							.append(errStr)
							.append("\n\t")
							.append(ExceptionUtils.getFullStackTrace(e))
							.toString());
				}

				//				logger.error(errStr, e);
				//				throw new FoundationsException(errStr, e);
			}

			Iterator it = indices.iterator();

			map = new HashMap();
			while (it.hasNext()) {
				index = (Node) it.next();
				map.put(
					XmlUtilities.getAttr(index, "name").getValue(),
					XmlUtilities.getAttr(index, "value").getValue());
			}
			return map;
		} else {
			return param.getChildNodes().item(0).getNodeValue();
		}
	}

	/**
	 * Method setParam.
	 * <br>Sets a parameter needed by for a M RPC call.
	 * <br><br>
	 * The position argument is the parameter list position where the RPC<br>
	 * expects to see this argument.
	 * 
	 * The type argument indicates to VistALink how the argument should be processed<br>
	 * on the M VistA server.<br>
	 * <br>
	 * Possible values are the following:<br>
	 * 	<ul>
	 * 		<li> string	(corresponds to 'Literal' in VA RPC Broker)
	 * 		<li> array	(corresponds to 'List' in VA RPC Broker)
	 * 		<li> ref	(corresponds to 'Reference' in VA RPC Broker)
	 *	</ul>
	 * <br>
	 * @param position	parameter position the M RPC expects this parameter
	 * @param type		type of parameter corresponding to valid M RPC types
	 * @param value	value of parameter
	 */
	public void setParam(int position, String type, Object value) {
		Element param, indices, index;
		Attr attr;
		Node node =
			XmlUtilities.getNode(
				"//Param[@position='" + position + "']",
				params);

		// if param exists at position then remove so it can be re-set
		if (node != null) {
			params.removeChild(node);
			node = null;
		}

		if (value instanceof Map) {
			param = requestDoc.createElement("Param");
			setType(param, "array");
			setPosition(param, Integer.toString(position));

			indices = requestDoc.createElement("Indices");
			Map map = (Map) value;
			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				index = requestDoc.createElement("Index");

				attr = requestDoc.createAttribute("name");
				attr.setValue(me.getKey().toString());
				index.setAttributeNode(attr);

				attr = requestDoc.createAttribute("value");
				attr.setValue(me.getValue().toString());
				index.setAttributeNode(attr);

				indices.appendChild(index);
			}
			param.appendChild(indices);
			params.appendChild(param);
		} else if (value instanceof List) {
			List list = (List) value;
			setIndices(position, list.iterator());
		} else if (value instanceof Set) {
			Set set = (Set) value;
			setIndices(position, set.iterator());
		} else {
			param = requestDoc.createElement("Param");
			setType(param, type);
			setPosition(param, Integer.toString(position));

			param.appendChild(requestDoc.createTextNode(value.toString()));
			params.appendChild(param);
		}
	}

	/**
	 * Method setIndices.
	 * <br>Sets the name, value attributes for a given List's Iterator
	 * @param position
	 * @param it
	 */
	private void setIndices(int position, Iterator it) {
		Element param, indices, index;
		Attr attr;

		param = requestDoc.createElement("Param");
		setType(param, "array");
		setPosition(param, Integer.toString(position));

		int item = 0;
		indices = requestDoc.createElement("Indices");
		while (it.hasNext()) {
			index = requestDoc.createElement("Index");
			attr = requestDoc.createAttribute("name");
			attr.setValue(String.valueOf(++item));
			index.setAttributeNode(attr);

			attr = requestDoc.createAttribute("value");
			attr.setValue(it.next().toString());
			index.setAttributeNode(attr);

			indices.appendChild(index);
		}

		param.appendChild(indices);
		params.appendChild(param);
	}

	/**
	 * Method setType.
	 * <br>Sets the type on the specified element
	 * @param param
	 * @param value
	 */
	private void setType(Element param, String value) {
		Attr attr = requestDoc.createAttribute("type");
		attr.setValue(value);
		param.setAttributeNode(attr);
	}

	/**
	 * Method setPosition.
	 * <br>Sets the RPC parameter position of the specified element param at value
	 * @param param
	 * @param value
	 */
	private void setPosition(Element param, String value) {
		Attr attr = requestDoc.createAttribute("position");
		attr.setValue(value);
		param.setAttributeNode(attr);
	}
}
