/*
 * Created on Mar 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This exception represents the case where the request RPC context does not exist or the current
 * user does not have access to the B-option representing the context.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class NoRpcContextFaultException extends RpcFaultException {

	/**
	 * Constructor for NoRpcContextFaultException.
	 * @see gov.va.med.foundations.adapter.record.VistaLinkFaultException#VistaLinkFaultException(VistaLinkFaultException)
	 * @param vistaLinkFaultException the exception to copy into a new exception type
	 */
	public NoRpcContextFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
