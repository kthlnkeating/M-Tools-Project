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

import gov.va.med.iss.connection.ConnectionData;
import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.core.SaveRoutineEngine;
import us.pwc.vista.eclipse.server.core.StatusHelper;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

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
		
		List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event);
		if (selectedFiles == null) {
			return null;
		}
		IProject project = selectedFiles.get(0).getProject();
		ConnectionData connectionData = VLConnectionPlugin.getConnectionManager().getConnectionData(project);
		if (connectionData == null) {
			return null;
		}
		
		if (namespaceFlag) {
			ServerData data = connectionData.getServerData();
			String title =  Messages.bind(Messages.SAVE_M_RTNS_DLG_TITLE, data.getAddress(), data.getPort());
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
				MessageDialogHelper.showError(Messages.SAVE_MSG_TITLE, message);
				return null;
			}			
			selectedFiles = updatedFiles;
		}

		if (selectedFiles.size() == 1) {
			IStatus status = SaveRoutineEngine.save(connectionData, selectedFiles.get(0));
			MessageDialogHelper.logAndShow(Messages.SAVE_MSG_TITLE, status);
		} else {
			int overallSeverity = IStatus.OK;
			List<IStatus> statuses = new ArrayList<IStatus>();
			for (IFile file : selectedFiles) {
				IStatus status = SaveRoutineEngine.save(connectionData, file);
				String prefixForFile = file.getFullPath().toString() + " -- ";
				overallSeverity = StatusHelper.updateStatuses(status, VistAServerPlugin.PLUGIN_ID, prefixForFile, overallSeverity, statuses);
			}
			
			String[] topMessages = new String[]{Messages.MULTI_SAVE_RTN_ERRR, Messages.MULTI_SAVE_RTN_WARN, Messages.MULTI_SAVE_RTN_INFO};
			String topMessage = CommandCommon.selectMessageOnStatus(overallSeverity, topMessages);
			CommandCommon.showMultiStatus(overallSeverity, Messages.SAVE_MSG_TITLE, topMessage, statuses);
		}
		return null;		
	}
}
