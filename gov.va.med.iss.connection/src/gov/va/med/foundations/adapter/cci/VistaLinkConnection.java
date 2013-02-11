package gov.va.med.foundations.adapter.cci;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkRequestVO;
import gov.va.med.foundations.adapter.record.VistaLinkResponseFactory;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.adapter.spi.VistaLinkManagedConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;

//import x.gov.va.med.iss.log4j.*;


import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * This implementation class represents an application level connection
 * handle that is used by a component to access an EIS instance.
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
 * <b>  //Execute the Rpc and get the response 
 * vResp = myConnection.executeRPC(vReq);
 * </b>
 * 
 *  //Work with the response ...
 * 
 * 
 * </pre>
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnection implements Connection {

	/**
	 * The logger used by this class
	 */
/*

	private static final Logger logger =
		Logger.getLogger(VistaLinkConnection.class);
*/
	/**
	 * The managed connection this VistaLinkConnection uses to interact with M
	 */
	private VistaLinkManagedConnection managedConnection;

	/**
	 * VistaLinkConnection Constructor.
	 * <br>This constructor should not be called directly. 
	 * <br>Used by spi classes.
	 * 
	 * @param mc
	 */
	public VistaLinkConnection(VistaLinkManagedConnection mc) {
		managedConnection = mc;
	}

	/**
	 * Method close 
	 * <br>Closes this connection handle
	 * <br>Informs the managed connection that this handle is closed
	 * @throws ResourceException
	 */
	public void close() throws ResourceException {
		try {
			if (managedConnection != null) {
				managedConnection.closeHandle(this);
				managedConnection = null;
			}
		} catch (ResourceException e) {
/*			
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(
					"Can not close connection handle.")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
*/			
			throw e;
		}
	}

	/**
	 * Method executeInteraction.
	 * <br>Executes an interaction with M
	 * @param requestVO - the request being made
	 * @param responseFactory - the factory which will construct the response
	 * @return VistaLinkResponseVO - the response from M
	 * @throws VistaLinkFaultException - thrown if an error occurred on M while
	 * processing the request
	 * @throws FoundationsException - thrown if an internal adapter exception
	 * has occurred
	 */
	public VistaLinkResponseVO executeInteraction(
		VistaLinkRequestVO requestVO,
		VistaLinkResponseFactory responseFactory)
		throws VistaLinkFaultException, FoundationsException {
		try {
			return getManagedConnection().executeInteraction(
				requestVO,
				responseFactory);
		} catch (VistaLinkFaultException e) {
			// Do not want to log application level exceptions
			throw e;
		} catch (FoundationsException e) {
/*
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(
					"Can not execute interaction.")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
*/
			throw e;
		}
	}

	/**
	 * Method executeRPC. 
	 * <br>Executes an interaction with M using the RpcResponseFactory
	 * <br>to construct a response. 
	 * @param request - The request being made
	 * @return RpcResponse - the response that is returned
	 * @throws VistaLinkFaultException - thrown if an error occurred on M while
	 * processing the request
	 * @throws FoundationsException - thrown if an internal adapter exception
	 * has occurred
	 */
	public RpcResponse executeRPC(RpcRequest request)
		throws VistaLinkFaultException, FoundationsException {
		try {
			return getManagedConnection().executeRPC(request);
		} catch (VistaLinkFaultException e) {
			// Do not want to log application level exceptions
			throw e;
		} catch (FoundationsException e) {
/*
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = (new StringBuffer())
					.append(
					"Can not execute RPC.")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);
			}
*/
			throw e;
		}
	}

	/**
	 * Method getMetaData 
	 * <br>Gets the meta-information on the underlying EIS instance via this
	 * connection
	 * @return ConnectionMetaData - the value object that contains 
	 * <br>the metadata for this adapter
	 * @throws ResourceException
	 */
	public ConnectionMetaData getMetaData() throws ResourceException {
		try {
			return managedConnection.getConnectionMetaData();
		} catch (VistaLinkResourceException e) {
/*
			if(logger.isEnabledFor(Priority.ERROR)){

				String errMsg = ExceptionUtils
							.getFullStackTrace(e);
						
				logger.error(errMsg);
			}
*/			throw e;
		}
	}

	/**
	 * Method getConnectionInfo. Returns connection information
	 * <br>about the host
	 * @return VistaLinkServerInfo - the value object that contains connection
	 * <br>information
	 */
	public VistaLinkServerInfo getConnectionInfo() {
		return new VistaLinkServerInfo(
			managedConnection.getHostAddr(),
			managedConnection.getHostPort());
	}

	/**
	 * Method getManagedConnection. <br>
	 * <b>This method should not be called directly. Used by spi classes.</b>
	 * 
	 * @return VistaLinkManagedConnection - the managed connection
	 */
	public VistaLinkManagedConnection getManagedConnection() throws FoundationsException {
		if(managedConnection == null){
			
			throw new FoundationsException("The managed connection is null.");
			
		}

		return managedConnection;
	}

	/**
	 * Method setManagedConnection.
	 * <b>This method should not be called directly. Used by spi classes.</b>
	 * 
	 * @param mc
	 */
	public void setManagedConnection(VistaLinkManagedConnection mc) {
		managedConnection = mc;
	}

	/**
	 * Empty method. throws NotSupportedException  
	 * 
	 * @see javax.resource.cci.Connection#createInteraction()
	 */
	public Interaction createInteraction() throws ResourceException {
		throw new NotSupportedException("VistALink does not implement Iteraction and Record part of CCI interface.  VistaRequest interface is used instead.");
	}

	/**
	 * Empty method. throws NotSupportedException  
	 * 
	 * @see javax.resource.cci.Connection#getLocalTransaction()
	 */
	public LocalTransaction getLocalTransaction() throws ResourceException {
		throw new NotSupportedException("VistALink does not support local transactions.");
	}

	/**
	 * Empty method. throws NotSupportedException  
	 * 
	 * @see javax.resource.cci.Connection#getResultSetInfo()
	 */
	public ResultSetInfo getResultSetInfo() throws ResourceException {
		throw new NotSupportedException("VistALink does not support ResultSet functionality.");
	}

}
