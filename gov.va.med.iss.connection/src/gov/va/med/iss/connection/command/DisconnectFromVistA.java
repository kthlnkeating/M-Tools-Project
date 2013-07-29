package gov.va.med.iss.connection.command;

import gov.va.med.iss.connection.actions.VistaConnection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DisconnectFromVistA extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaConnection.disconnect();
		return null;
	}
}
