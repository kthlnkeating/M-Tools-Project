package gov.va.med.iss.meditor.command;

import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.dialog.InputDialogHelper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class LoadRoutinesIntoDirectory extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		String title = Messages.bind2(Messages.LOAD_M_RTNS_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
		String routineNamespace = InputDialogHelper.getRoutineNamespace(title);
		if (routineNamespace == null) {
			return null;
		}
		
		return null;
	}
}
