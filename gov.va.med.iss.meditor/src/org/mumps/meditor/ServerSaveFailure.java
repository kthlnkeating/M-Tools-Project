package org.mumps.meditor;

public class ServerSaveFailure extends Exception {

	private static final long serialVersionUID = 9060239748906466162L;

	public ServerSaveFailure() {
		super();
	}

	public ServerSaveFailure(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServerSaveFailure(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerSaveFailure(String message) {
		super(message);
	}

	public ServerSaveFailure(Throwable cause) {
		super(cause);
	}

}
