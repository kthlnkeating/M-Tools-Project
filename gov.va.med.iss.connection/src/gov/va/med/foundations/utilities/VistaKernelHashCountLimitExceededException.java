package gov.va.med.foundations.utilities;

/**
 * Represents an exception identifying that the Hash Count Limit (for a call to the VistaKernelHash
 * <code>encrypt</code> method) has been exceeded. In this case, the hash algorithm could not return an encrypted hash
 * within a certain number of tries, that was free of CDATA boundary character strings ("&lt;![CDATA[" and "]]&gt;").
 * @see VistaKernelHash
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaKernelHashCountLimitExceededException
	extends FoundationsException {

	/**
	 * Constructor for VistaKernelHashCountLimitExceededException.
	 */
	public VistaKernelHashCountLimitExceededException() {
		super();
	}

	/**
	 * Constructor for VistaKernelHashCountLimitExceededException.
	 * @param s
	 */
	public VistaKernelHashCountLimitExceededException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for VistaKernelHashCountLimitExceededException.
	 * @param nestedException
	 */
	public VistaKernelHashCountLimitExceededException(Exception nestedException) {
		super(nestedException);
	}

	/**
	 * Constructor for VistaKernelHashCountLimitExceededException.
	 * @param msg
	 * @param nestedException
	 */
	public VistaKernelHashCountLimitExceededException(
		String msg,
		Exception nestedException) {
		super(msg, nestedException);
	}

}
