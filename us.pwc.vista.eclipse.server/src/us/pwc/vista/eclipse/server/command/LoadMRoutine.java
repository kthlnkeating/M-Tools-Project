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

package us.pwc.vista.eclipse.server.command;

import gov.va.med.iss.connection.ConnectionData;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.connection.preferences.ServerData;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.handlers.HandlerUtil;

import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.core.LoadRoutineEngine;
import us.pwc.vista.eclipse.server.core.MServerRoutine;
import us.pwc.vista.eclipse.server.wizard.SelectRoutineDialog;

public class LoadMRoutine extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbench wb = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();		
		Shell shell = HandlerUtil.getActiveShell(event);
		
		SelectRoutineDialog dialog = SelectRoutineDialog.getInstance(wb, shell);
		if (SelectRoutineDialog.OK != dialog.open()) {
			return null;
		}
		ServerData serverData = dialog.getServerData();
		IFile fileHandle = dialog.getFileHandle();

		String serverName = serverData.getName();
		ConnectionData connectionData = VLConnectionPlugin.getConnectionManager().getConnectionData(serverName);
		if (connectionData == null) {
			return null;
		}

		CommandResult<MServerRoutine> result = LoadRoutineEngine.loadRoutine(connectionData, fileHandle);
		IStatus status = result.getStatus();		
		if (status.getSeverity() != IStatus.OK) {
			MessageDialogHelper.logAndShow(Messages.LOAD_MSG_TITLE, status);			
		}
		if (status.getSeverity() != IStatus.ERROR) {
			IFile file = result.getResultObject().getFileHandle();
			CommandCommon.openEditor(event, file);
		}
		return null;
	}
}
