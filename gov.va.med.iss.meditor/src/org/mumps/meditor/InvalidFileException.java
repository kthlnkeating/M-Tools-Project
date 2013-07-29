package org.mumps.meditor;

public class InvalidFileException extends MEditorException {
	private static final long serialVersionUID = 1L;

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidFileException(String message) {
		super(message);
	}
}
