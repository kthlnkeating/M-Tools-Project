package gov.va.med.foundations.utilities;

/**
 * Represents the interface that all Foundations exceptions implement.
 * Implementing this interface allows ExceptionUtils to work with
 * the exception. 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public interface FoundationsExceptionInterface {

	/**
	 * Return full stack trace. Full stack trace will include all nested exception
	 * messages and the full stack trace for the root exception.
	 * 
	 * @return full stack trace String
	 */
	public String getFullStackTrace();
	
	/**
	 * Return nested exception that is wrapped within this exception.
	 * @return nested exception
	 */
	public Throwable getNestedException();

}
