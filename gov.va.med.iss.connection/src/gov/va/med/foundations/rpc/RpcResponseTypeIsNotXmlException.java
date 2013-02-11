package gov.va.med.foundations.rpc;

import gov.va.med.foundations.utilities.FoundationsException;

/**
 * Represents an exception indicating the RpcResponse type if not XML
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class RpcResponseTypeIsNotXmlException extends FoundationsException {

	/**
	 * Constructor for RpcResponseTypeIsNotXmlException.
	 */
	public RpcResponseTypeIsNotXmlException() {
		super();
	}

	/**
	 * Constructor for RpcResponseTypeIsNotXmlException.
	 * @param s - message value
	 */
	public RpcResponseTypeIsNotXmlException(String s) {
		super(s);
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * @param nestedException
	 */
	public RpcResponseTypeIsNotXmlException(Exception nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * @param msg
	 * @param nestedException
	 */
	public RpcResponseTypeIsNotXmlException(
		String msg,
		Exception nestedException) {
		super(msg, nestedException);
	}

}