package us.pwc.vista.eclipse.server.error;

public class VistAServerException extends Exception {
	private static final long serialVersionUID = 1L;

	public VistAServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public VistAServerException(String message) {
		super(message);
	}
	
	public VistAServerException(Throwable cause) {
		super(cause);
	}
}
