package us.pwc.vista.eclipse.server.command;

import gov.va.med.iss.connection.VistAConnection;
import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

public class ReportRoutineDirectory extends AbstractHandler {
	private static final String ROUTINE_DIRECTORY = "Routine Directory Console";
		
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistAConnection vistaConnection = VLConnectionPlugin.getConnectionManager().selectConnection(false);
		if (vistaConnection == null) {
			return null;
		}
		
		ServerData data = vistaConnection.getServerData();
		String title =  Messages.bind(Messages.LOAD_RTNDIR_DLG_TITLE, data.getAddress(), data.getPort());
		String namespace = InputDialogHelper.getRoutineNamespace(title);
		if (namespace == null) {
			return null;
		}
		
		String result = CommandCommon.getRoutineNames(vistaConnection, namespace);
		if (result == null) {
			return null;
		}
		if (result.isEmpty()) {
			result = "<no matches found>\n";
		}
		result = "Routines beginning with "+ namespace +"\n\n"+ result;		
		MessageConsoleHelper.writeToConsole(ROUTINE_DIRECTORY, result, true);
		return null;
	}
}
