package gov.va.med.foundations.adapter.heartbeat;


/**
 * This exception class is thrown when the heart beat fails to make 
 * <br>its first interaction to retrieve the heartbeat rate from M. 
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class HeartBeatInitializationFailedException
	extends HeartBeatFailedException {

	/**
	 * Constructor for HeartBeatInitializationFailedException.
	 * @param reason
	 */
	public HeartBeatInitializationFailedException(String reason) {
		super(reason);
	}

	/**
	 * Constructor for HeartBeatInitializationFailedException.
	 * @param e
	 */
	public HeartBeatInitializationFailedException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for HeartBeatInitializationFailedException.
	 * @param reason
	 * @param errorCode
	 */
	public HeartBeatInitializationFailedException(
		String reason,
		String errorCode) {
		super(reason, errorCode);
	}

	/**
	 * Constructor for HeartBeatInitializationFailedException.
	 * @param reason
	 * @param errorCode
	 * @param e
	 */
	public HeartBeatInitializationFailedException(
		String reason,
		String errorCode,
		Exception e) {
		super(reason, errorCode, e);
	}

	/**
	 * Constructor for HeartBeatInitializationFailedException.
	 * @param reason
	 * @param e
	 */
	public HeartBeatInitializationFailedException(String reason, Exception e) {
		super(reason, e);
	}

}
