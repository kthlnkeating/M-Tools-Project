package gov.va.med.iss.connection.command;

import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DisconnectFromVistA extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VLConnectionPlugin.getConnectionManager().removeConnection();
		return null;
	}
}
