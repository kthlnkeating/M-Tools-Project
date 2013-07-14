package gov.va.med.foundations.utilities;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Nested exception handling code is identical to VistaLinkResourceException
 * nested exception handling code. 
 * <br> Implements methods to return nested exception message as part of current 
 * exception message. 
 * <br> 
 * The nested exception, uses throwable so we can encapsulate all types of exceptions
 * even Error exceptions.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class FoundationsException
	extends Exception
	implements FoundationsExceptionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The nested exception, uses throwable so we can encapsulate even 
	 * Error exceptions.
	 */
	private Throwable nestedException;

	/**
	 * Default constructor. 
	 */
	public FoundationsException() {
	}

	/**
	 * Constructor.
	 * @see java.lang.Throwable#Throwable(java.lang.String)
	 */
	public FoundationsException(String msg) {
		super(msg);
	}

	/**
	 * Constructor.
	 * @param nestedException exception to nest in new FoundationsException
	 */
	public FoundationsException(Throwable nestedException) {
		super();
		this.nestedException = nestedException;
	}

	/**
	 * Constructor.
	 * @param msg Exception message
	 * @param nestedException exception to nest in new FoundationsException
	 */
	public FoundationsException(String msg, Throwable nestedException) {
		super(msg);
		this.nestedException = nestedException;
	}

	/**
	 * Gets the nested exception.
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getNestedException()
	 */
	public Throwable getNestedException() {
		return nestedException;
	}


	/**
	* Method getOriginalMessage. 
	* <br>Returns oringal message that was passed into the exception
	* constructor. Does not perform message discovery from the nested
	* exception. Only to be used by the descendants of this class.
	* @return String
    */
	protected String getOriginalMessage() {
		return super.getMessage();
	}

	/**
	 * Returns the detail message, including nested messages from the nested
	 * exceptions.
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		String retMessage = super.getMessage();
		if (retMessage == null) {
			retMessage = "";
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
	 * Prints the composite message and full embedded stack trace to <code>System.err</code>.
	 * @see java.lang.Throwable#printStackTrace()
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * Returns the composite message and full embedded stack trace trace
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getFullStackTrace()
	 */
	public String getFullStackTrace() {
		return ExceptionUtils.getFullStackTrace(this);
	}

}
