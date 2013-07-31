//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package gov.va.med.iss.meditor.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.dialog.GlobalListingData;
import gov.va.med.iss.meditor.dialog.GlobalListingDialog;
import gov.va.med.iss.meditor.utils.GlobalListing;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReportGlobalListing extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		Shell shell = HandlerUtil.getActiveShell(event);
		String title = Messages.bind(Messages.DLG_GLOBAL_LISTING_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort());
		GlobalListingDialog dialog = new GlobalListingDialog(shell, title);
		int result = dialog.open();
		if (result == GlobalListingDialog.OK) {
			GlobalListingData data = dialog.getData();
			GlobalListing.getGlobalListing(data.globalName, data.setupCopySelected, data.dataOnlySelected, data.searchText, data.isSearchDataOnly, data.isCaseSensitive);
		}
		return null;
	}
}
