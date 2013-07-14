package gov.va.med.foundations.security.vistalink;

import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsExceptionInterface;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.security.auth.login.LoginException;

/**
 * Represents a LoginException thrown by the LoginModule. The main difference from <code>LoginException</code> is
 * support for including a nested exception. When attempting a logon, you can trap for the more specific
 * <code>VistaLoginModuleException</code>, in addition to <code>LoginException</code>.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLoginModuleException extends LoginException implements FoundationsExceptionInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The nested exception, uses throwable so we can encapsulate even 
	 * <br>Error exceptions
	 */
	private Throwable nestedException;

	/**
	 * Constructor
	 * @param msg Exception message
	 * @see java.lang.Throwable#Throwable(String)
	 */
	VistaLoginModuleException(String msg) {
		super(msg);
	}

	/**
	 * Constructor
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleException(Throwable nestedException) {
		super();
		this.nestedException = nestedException;
	}

	/**
	 * Constructor
	 * @param msg String exception message
	 * @param nestedException exception to nest in new VistaLoginModuleException
	 */
	VistaLoginModuleException(String msg, Throwable nestedException) {
		super(msg);
		this.nestedException = nestedException;
	}

	/**
	 * Gets the nested exception
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getNestedException()
	 */
	public Throwable getNestedException() {
		return this.nestedException;
	}

	/**
	* Method getOriginalMessage. 
	* <br>Returns oringal message that was passed into the exception
	* constructor. Does not perform message discovery from the nested
	* exception. Only to be used by the descendants of this class.
	* @return String
	*/
	String getOriginalMessage() {
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
	* writer <code>pw</code>
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
	 * Returns the composite message and full embedded stack trace 
	 * @see gov.va.med.foundations.utilities.FoundationsExceptionInterface#getFullStackTrace()
	 */
	public String getFullStackTrace() {
		return ExceptionUtils.getFullStackTrace(this);
	}

}
