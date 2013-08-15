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

import java.util.ArrayList;
import java.util.List;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.core.SaveRoutineEngine;
import us.pwc.vista.eclipse.server.core.StatusHelper;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;
import us.pwc.vista.eclipse.server.dialog.MessageDialogHelper;

/**
 * This implementation of <code>AbstractHandler</code> saves the selected M 
 * file to the M server.  Selected files can be filtered by namespace.
 *
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SaveRoutine extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Object namespaceParam = event.getObjectParameterForExecution("us.pwc.vista.eclipse.server.command.saveRoutine.namespace");
		boolean namespaceFlag = ((Boolean) namespaceParam).booleanValue();
		
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}

		String projectName = VistaConnection.getPrimaryProject();
		List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event, projectName);
		if (selectedFiles == null) {
			return null;
		}
		
		if (namespaceFlag) {
			String title = Messages.bind2(Messages.SAVE_M_RTNS_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
			String namespace = InputDialogHelper.getRoutineNamespace(title);
			if (namespace == null) {
				return null;
			}

			List<IFile> updatedFiles = new ArrayList<IFile>();
			for (IFile file : selectedFiles) {
				String name = file.getName();
				if (name.startsWith(namespace)) {
					updatedFiles.add(file);
				}
			}			
			if (updatedFiles.size() == 0) {
				String message = Messages.bind(Messages.NO_FILES_IN_NAMESPACE, namespace);
				MessageDialogHelper.showError(message);
				return null;
			}			
			selectedFiles = updatedFiles;
		}

		if (selectedFiles.size() == 1) {
			IStatus status = SaveRoutineEngine.save(connection, selectedFiles.get(0));
			MessageDialogHelper.logAndShow(status);
		} else {
			int overallSeverity = IStatus.OK;
			List<IStatus> statuses = new ArrayList<IStatus>();
			for (IFile file : selectedFiles) {
				IStatus status = SaveRoutineEngine.save(connection, file);
				String prefixForFile = file.getFullPath().toString() + " -- ";
				overallSeverity = StatusHelper.updateStatuses(status, prefixForFile, overallSeverity, statuses);
			}
			
			String[] topMessages = new String[]{Messages.MULTI_SAVE_RTN_ERRR, Messages.MULTI_SAVE_RTN_WARN, Messages.MULTI_SAVE_RTN_INFO};
			String topMessage = CommandCommon.selectMessageOnStatus(overallSeverity, topMessages);
			CommandCommon.showMultiStatus(overallSeverity, topMessage, statuses);
		}
		return null;		
	}
}
