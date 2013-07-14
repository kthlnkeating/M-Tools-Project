package gov.va.med.foundations.utilities;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * Exposes utility methods for handling exceptions
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class ExceptionUtils {
	
	/**
	 * Private constructor to prevent instantiation of this class objects.
	 */
	private ExceptionUtils() {
	}

	/**
	 * Method getFullStackTrace.
	 * <br>Gets the full stack trace as a string. 
	 * 
	 * @param e
	 * @return String
	 */
	public static String getFullStackTrace(Throwable e) {
		ByteArrayOutputStream byteOutStr = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(byteOutStr);
		e.printStackTrace(writer);
		writer.flush();
		writer.close();
		return new String(byteOutStr.toByteArray());
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Method getNestedExceptionByClass.
	 * <br>Gets the nested exception if exception is an instance of the exceptionClass or
	 * if exception implements FoundationsExceptionInterface and one of the nested 
	 * exceptions is an instance of the type exceptionClass.
	 * <br>If desired instance of exceptionClass is not found in the nested exception stack
	 * then null is returned.
	 * <br>Can be used to unwind nested exception stack that implement FoundationsExceptionInterface.  
	 * @param e
	 * @param exceptionClass
	 * @return Throwable
	 */
	public static Throwable getNestedExceptionByClass(Throwable e, Class exceptionClass) {
		// If this is the one we are looking for, just return it
		if (exceptionClass.isInstance(e)) {
			return e;
		}
		
		// If e is FoundationsExceptionInterface check nested exception
		if (FoundationsExceptionInterface.class.isInstance(e)) {
			Throwable nestedException = ((FoundationsExceptionInterface)e).getNestedException();
			return ExceptionUtils.getNestedExceptionByClass(nestedException, exceptionClass);
		}
		  
		return null;	
	}

}
