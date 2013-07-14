package gov.va.med.foundations.adapter.cci;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsExceptionInterface;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.resource.ResourceException;

/**
 *	Represents a ResourceException thrown by the VistaLink adapter.
 * Nested exception handling code is identical to FoundationsException 
 * nested exception handling code.  
 * <br> Nested exception is not stored as a memeber of this class as <code>ResourceException</code>
 * has it's own member variable that is used to store nested exception.
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkResourceException
	extends ResourceException
	implements FoundationsExceptionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param reason application level reason why this exception occured
	 */
	public VistaLinkResourceException(String reason) {
		super(reason);
	}

	/**
	 * Constructor.
	 * @param e exception to nest in new VistaLinkResourceException
	 */
	public VistaLinkResourceException(Exception e) {
		super(e.getMessage());
		this.setLinkedException(e);
	}

	/**
	 * Constructor. Parameters are required by the parent class.
	 * @param reason application level reason why this exception occured
	 * @param errorCode application level code reason why this exception occured
	 */
	public VistaLinkResourceException(String reason, String errorCode) {
		super(reason, errorCode);
	}

	/**
	 * Constructor. Parameters are required by the parent class.
	 * @param reason application level reason why this exception occured
	 * @param errorCode application level code reason why this exception occured
	 * @param e exception to nest in new VistaLinkResourceException
	 */
	public VistaLinkResourceException(
		String reason,
		String errorCode,
		Exception e) {
		super(reason, errorCode);
		this.setLinkedException(e);
	}

	/**
	 * Constructor.
	 * @param reason application level reason why this exception occured
	 * @param e exception to nest in new VistaLinkResourceException
	 */
	public VistaLinkResourceException(String reason, Exception e) {
		super(reason);
		this.setLinkedException(e);
		setLinkedException(e);
	}

	/** 
	 * Return full stack trace. Full stack trace will include all nested exception messages and the full 
	 * stack trace for the root exception. 
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getNestedException()
	 **/
	public Throwable getNestedException() {
		return getLinkedException();
	}

	/** 
	 * Returns the detail message, including nested messages from the nested
	 * exceptions.
	 * @see java.lang.Throwable#getMessage()
	 **/
	public String getMessage() {
		String retMessage = super.getMessage();
		if (retMessage == null) {
			retMessage = "";
		}

		if (getErrorCode() != null) {
			retMessage += "; Code: " + getErrorCode();
		}

		if (getNestedException() == null) {
			return retMessage;
		} else {
			return retMessage
			+ "; \n\tRoot cause exception: \n\t"
				+ getNestedException().toString();
		}
	}

	/**
	 * Prints the composite message and full embedded stack trace to the
	 * specified stream <code>ps</code>.
	 * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
	 * @param ps the print stream
	 */
	public void printStackTrace(PrintStream ps) {
		if (getNestedException() == null) {
			super.printStackTrace(ps);
		} else {
			ps.println(this);
			getNestedException().printStackTrace(ps);
		}
	}

	/**
	* Prints the composite message and full embedded stack trace to the specified print
	* <br>writer <code>pw</code>
	* @see java.lang.Throwable#printStackTrace(java.io.PrintWriter) 
	* @param pw the print writer
	*/
	public void printStackTrace(PrintWriter pw) {
		if (getNestedException() == null) {
			super.printStackTrace(pw);
		} else {
			pw.println(this);
			getNestedException().printStackTrace(pw);
		}
	}

	/**
	 * Prints the composite message and full embedded stack trace  to <code>System.err</code>.
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		printStackTrace(System.err);

	}

	/**
	 * Returns the composite message and full embedded stack trace  stack trace
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getFullStackTrace()
	 */
	public String getFullStackTrace() {
		return ExceptionUtils.getFullStackTrace(this);
	}

}
