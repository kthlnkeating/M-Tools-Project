package gov.va.med.iss.meditor.error;

public class MEditorException extends Exception {
	private static final long serialVersionUID = 1L;

	public MEditorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MEditorException(String message) {
		super(message);
	}
	
	public MEditorException(Throwable cause) {
		super(cause);
	}
}
