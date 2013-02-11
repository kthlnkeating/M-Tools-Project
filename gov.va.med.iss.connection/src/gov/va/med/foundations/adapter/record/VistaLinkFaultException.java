package gov.va.med.foundations.adapter.record;

import gov.va.med.foundations.utilities.FoundationsException;

/**
 * Exception encapsulates Fault information coming from M side.
 * <br> M side can pass bak to Java error condition in the system Fault message. 
 * If this condition happens, VLJ creates VistaLinkFaultException
 * and populates it's properties with data from the Fault message.
 * <br> All other Fault exceptions are subclassed from this exception.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */

public class VistaLinkFaultException extends FoundationsException {

	// !!! CAUTION !!!
	// If adding new members, make sure to update
	// COPY constructor	
	/**
	 * FaultCode from the M side Fault message.  
	 */
	private String faultCode;
	/**
	 * FaultString from the M side Fault message.  
	 */
	private String faultString;
	/**
	 * FaultActor from the M side Fault message.  
	 */
	private String faultActor;
	/**
	 * errorCode from the M side Fault message.  
	 */
	private String errorCode;
	/**
	 * errorType from the M side Fault message.  
	 */
	private String errorType;
	/**
	 * errorMessage from the M side Fault message.  
	 */
	private String errorMessage;

	/**
	 * Constructor for VistaLinkFaultException.
	 * 
	 * @see java.lang.Object#Object()
	 */
	public VistaLinkFaultException() {
		super();
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * 
	 * @see java.lang.Throwable#Throwable(java.lang.String)
	 */
	public VistaLinkFaultException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * <br> Passed-in parameter values are stored as members of the exception.
	 * <br> Exception message string is constructed from the parameters.
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param errorType
	 * @param faultActor
	 * @param faultCode
	 * @param faultString
	 */
	public VistaLinkFaultException(
		String errorCode,
		String errorMessage,
		String errorType,
		String faultActor,
		String faultCode,
		String faultString) {

		super(
			(new StringBuffer(2000))
				.append("\n\t\tFault Code: '")
				.append(faultCode)
				.append("'; ")
				.append("Fault String: '")
				.append(faultString)
				.append("'; ")
				.append("Fault Actor: '")
				.append(faultActor)
				.append("'; ")
				.append("\n\t\tCode: '")
				.append(errorCode)
				.append("'; ")
				.append("Type: '")
				.append(errorType)
				.append("'; ")
				.append("Message: '")
				.append(errorMessage)
				.append("'")
				.toString());
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorType = errorType;
		this.faultActor = faultActor;
		this.faultCode = faultCode;
		this.faultString = faultString;
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * 
	 * @see gov.va.med.foundations.utilities.FoundationsException#FoundationsException(java.lang.Exception)
	 */
	public VistaLinkFaultException(Exception nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaLinkFaultException.
	 * 
	 * @see gov.va.med.foundations.utilities.FoundationsException#FoundationsException(java.lang.String, java.lang.Exception)
	 */
	public VistaLinkFaultException(String msg, Exception nestedException) {
		super(msg, nestedException);
	}

	/**
	 * Copy constructor for VistaLinkFaultException.
	 * <br>This is THE ONLY constructor that subclasses need to implement.
	 * 
	 * @see gov.va.med.foundations.utilities.FoundationsException#FoundationsException(java.lang.String, java.lang.Exception)
	 */
	protected VistaLinkFaultException(VistaLinkFaultException vistaLinkFaultException) {
		super(
			vistaLinkFaultException.getOriginalMessage(),
			vistaLinkFaultException.getNestedException());
		this.faultCode = vistaLinkFaultException.getFaultCode();
		this.faultString = vistaLinkFaultException.getFaultString();
		this.faultActor = vistaLinkFaultException.getFaultActor();
		this.errorCode = vistaLinkFaultException.getErrorCode();
		this.errorType = vistaLinkFaultException.getErrorType();
		this.errorMessage = vistaLinkFaultException.getErrorMessage();
	}

	/**
	 * Returns the errorCode.
	 * @return String
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Returns the errorMessage.
	 * @return String
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Returns the errorType.
	 * @return String
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * Returns the faultActor.
	 * @return String
	 */
	public String getFaultActor() {
		return faultActor;
	}

	/**
	 * Returns the faultCode.
	 * @return String
	 */
	public String getFaultCode() {
		return faultCode;
	}

	/**
	 * Returns the faultString.
	 * @return String
	 */
	public String getFaultString() {
		return faultString;
	}


}
