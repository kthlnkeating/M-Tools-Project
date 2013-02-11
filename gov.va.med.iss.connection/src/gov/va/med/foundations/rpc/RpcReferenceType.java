package gov.va.med.foundations.rpc;

/**
 * Represents a reference type object for an RPC parameter. 
 * <br>Used mainly for RpcRequest.setParams() call to represent a 
 * <br>'reference' type parameter.
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
 * String rpcName = "XWB GET VARIABLE VALUE";
 * 
 *  //Construct the request object
 * vReq = RpcRequestFactory.getRpcRequest(rpcContext, rpcName);
 * 
 * //clear the params 	
 * vReq.clearParams()
 * 
 * <b>
 * //Create an arraylist for the params
 * ArrayList params = new ArrayList();
 * 
 * //Clear the arraylist
 * params.clear();
 * 
 * //add a new VistaRpcReferenceType to the array list
 *  params.add(new VistaRpcReferenceType("DTIME"));
 * 
 * //Add the araylist with the VistaRpcReferenceType to the request as a param
 * vReq. setParams(params);
 * 
 * //An alternate way of doing the above
 * vReq.getParams().setParam(1, "ref", "DT");
 * 
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
public class RpcReferenceType {
	/**
	 * Represnts the value associated with 'reference' type param
	 */
	private String value;

	/**
	 * Default constructor
	 */
	public RpcReferenceType() {
	}

	/**
	 * Method VistaRpcReferenceType.
	 * <br>Constructs this instance with the specified value
	 * @param value Name of variable to be referenced, like DUZ
	 */
	public RpcReferenceType(String value) {
		this.value = value;
	}

	/**
	 * Returns name of the variable desired in the M server partition.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value to the name of the variable desired in the M server partition.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns name of the variable desired in the M server partition.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value;
	}

}
