package us.pwc.vista.eclipse.server.error;


public class BackupSynchException extends VistAServerException {
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
