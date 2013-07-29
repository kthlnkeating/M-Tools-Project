package gov.va.med.iss.meditor.command;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineDirectory;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ReportRoutineDirectory extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
		RoutineNameDialogData data = dialogForm.open();
		if (data.getButtonResponse()) {
			String routineName = data.getTextResponse();
			if (data.getUpperCase()) {
				routineName = routineName.toUpperCase();
			}
			if (! (routineName.compareTo("") == 0)) {
				//VistaConnection.getPrimaryServer(); //091029 to make check for change in servers
				if (VistaConnection.getPrimaryServer()) {
					RoutineDirectory.getRoutineDirectory(routineName);
				}
			}
		}
		return null;
	}
}
