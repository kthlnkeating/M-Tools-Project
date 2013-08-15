package us.pwc.vista.eclipse.server.error;


public class LoadRoutineException extends VistAServerException {
	private static final long serialVersionUID = 1L;

	public LoadRoutineException(String message) {
		super(message);
	}
	
	public LoadRoutineException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}
