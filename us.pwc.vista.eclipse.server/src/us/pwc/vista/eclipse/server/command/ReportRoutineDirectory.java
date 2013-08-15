package us.pwc.vista.eclipse.server.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.core.RoutineDirectory;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

public class ReportRoutineDirectory extends AbstractHandler {
	public static final String ROUTINE_DIRECTORY = "Routine Directory Console";
		
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		String title =  Messages.bind(Messages.LOAD_RTNDIR_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort());
		String namespace = InputDialogHelper.getRoutineNamespace(title);
		if (namespace == null) {
			return null;
		}
		
		String result = RoutineDirectory.getRoutineNames(namespace);
		MessageConsoleHelper.writeToConsole(ROUTINE_DIRECTORY, result, true);
		return null;
	}
}