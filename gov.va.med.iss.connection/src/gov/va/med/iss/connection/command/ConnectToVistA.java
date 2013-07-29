package gov.va.med.iss.connection.command;

import gov.va.med.iss.connection.actions.VistaConnection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ConnectToVistA extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		VistaConnection.run(window);
		return null;
	}
	
	@Override
	public void dispose() {
		VistaConnection.doDispose();
	}
}
