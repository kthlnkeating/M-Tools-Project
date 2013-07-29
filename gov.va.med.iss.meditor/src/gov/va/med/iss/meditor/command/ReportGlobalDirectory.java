package gov.va.med.iss.meditor.command;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.GlobalDirectory;
import gov.va.med.iss.meditor.utils.GlobalNameDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ReportGlobalDirectory extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GlobalNameDialog gnd = new GlobalNameDialog();
		String globalName = gnd.getGlobalName("GD");
		if (! (globalName.compareTo("-1") == 0)) {
			//VistaConnection.getPrimaryServer(); //091027 to make check for change in servers
			if (VistaConnection.getPrimaryServer()) {
				GlobalDirectory.getGlobalDirectory(globalName);
			}
		}
		return null;
	}
}
