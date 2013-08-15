package us.pwc.vista.eclipse.server.error;


public class InvalidFileException extends VistAServerException {
	private static final long serialVersionUID = 1L;

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFileException(String message) {
		super(message);
	}
}
