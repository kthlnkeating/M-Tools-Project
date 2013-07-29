package gov.va.med.iss.meditor.command;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.GlobalListing;
import gov.va.med.iss.meditor.utils.GlobalNameDialog;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ReportGlobalListing extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GlobalNameDialog gnd = new GlobalNameDialog();
		String globalName = gnd.getGlobalName("GL");
		if (! (globalName.compareTo("-1") == 0)) {
			if (! (globalName.indexOf('(') == 0)) {
				//VistaConnection.getPrimaryServer(); //091027 to make check for change in servers
				if (VistaConnection.getPrimaryServer()) {
					GlobalListing.getGlobalListing(globalName,gnd.isCopy(),gnd.isDataOnly(),gnd.getSearchText(),gnd.isSearchDataOnly(),gnd.isSearchCaseSensitive());
				}
			}
		}
		return null;
	}
}
