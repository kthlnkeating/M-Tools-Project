package us.pwc.vista.eclipse.server.core;

import org.eclipse.core.runtime.IStatus;

public class CommandResult<T> {
	private T resultObject;
	private IStatus status;
	
	public CommandResult(T resultObject, IStatus status) {
		super();
		this.resultObject = resultObject;
		this.status = status;
	}
	
	public T getResultObject() {
		return resultObject;
	}

	public IStatus getStatus() {
		return status;
	}
	
	public boolean isOK() {
		return this.status.getSeverity() != IStatus.ERROR;
	}
}
