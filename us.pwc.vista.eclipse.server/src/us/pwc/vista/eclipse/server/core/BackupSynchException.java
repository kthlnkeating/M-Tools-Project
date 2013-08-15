package us.pwc.vista.eclipse.server.core;

public class BackupSynchException extends Exception {
	private static final long serialVersionUID = 1L;

	public BackupSynchException(String message, Throwable cause) {
		super(message, cause);
	}

	public BackupSynchException(String message) {
		super(message);
	}
	
	public BackupSynchException(Throwable cause) {
		super(cause);
	}
}
