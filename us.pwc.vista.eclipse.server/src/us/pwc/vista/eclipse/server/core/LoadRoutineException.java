package us.pwc.vista.eclipse.server.core;

public class LoadRoutineException extends Exception {
	private static final long serialVersionUID = 1L;

	public LoadRoutineException(String message) {
		super(message);
	}
	
	public LoadRoutineException(String message, Throwable rootCause) {
		super(message, rootCause);
	}
}
