/*
 * Created on Mar 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This exception represents the case where the RPC execution took too long on the server and
 * the appliaction gracefully stopped the RPC's processing.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcTimeOutFaultException extends RpcFaultException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for RpcTimeOutFaultException.
	 * @see gov.va.med.foundations.adapter.record.VistaLinkFaultException#VistaLinkFaultException(VistaLinkFaultException)
	 * @param vistaLinkFaultException the exception to copy into a new exception type
	 */
	public RpcTimeOutFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}

}
