/*
 * Created on Mar 28, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package gov.va.med.foundations.rpc;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;

/**
 * This fault exception class is used for all rpc-related errors returned from the M system.
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcFaultException extends VistaLinkFaultException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for NoRpcContextFaultException. 
	 * @see gov.va.med.foundations.adapter.record.VistaLinkFaultException#VistaLinkFaultException(VistaLinkFaultException)
	 * @param vistaLinkFaultException the exception to copy into a new exception type
	 */
	public RpcFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(vistaLinkFaultException);
	}
}
