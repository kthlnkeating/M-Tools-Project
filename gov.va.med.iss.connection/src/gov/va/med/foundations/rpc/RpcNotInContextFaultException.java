/*
 * Created on Mar 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This exception represents the case where the requested RPC is not contained in the current
 * RPC context.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcNotInContextFaultException extends RpcFaultException {

	/**
	 * Constructor for RpcNotInContextFaultException.
	 * @see gov.va.med.foundations.adapter.record.VistaLinkFaultException#VistaLinkFaultException(VistaLinkFaultException)
	 * @param vistaLinkFaultException the exception to copy into a new exception type
	 */
	public RpcNotInContextFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
