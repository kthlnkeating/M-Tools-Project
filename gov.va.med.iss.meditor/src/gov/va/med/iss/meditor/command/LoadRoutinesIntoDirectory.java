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

import java.util.ArrayList;
import java.util.List;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.command.resource.ResourceUtilsExtension;
import gov.va.med.iss.meditor.command.utils.MServerRoutine;
import gov.va.med.iss.meditor.dialog.InputDialogHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;
import gov.va.med.iss.meditor.utils.RoutineDirectory;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TreePath;

public class LoadRoutinesIntoDirectory extends LoadMultipleRoutines {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}

		TreePath[] paths = CommandCommon.getTreePaths(event);
		if (paths == null) {
			return null;
		}
		if (paths.length != 1) {
			String message = "This operation is supported only when a single folder is selected.";
			MessageDialogHelper.showError(message);
			return null;
		}		
		IFolder folder = ResourceUtilsExtension.getFolder(paths[0]);
		if (folder == null) {
			String message = "Only folders are supported for this operation.";
			MessageDialogHelper.showError(message);
			return null;			
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		if (! folder.getProject().getName().equals(projectName)) {
			String message = "Connection is only valid for project" + projectName + ".";
			MessageDialogHelper.showError(message);
			return null;						
		}

		String title = Messages.bind2(Messages.LOAD_M_RTNS_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
		String routineNamespace = InputDialogHelper.getRoutineNamespace(title);
		if (routineNamespace == null) {
			return null;
		}
		
		String routines = RoutineDirectory.getRoutineList(routineNamespace);
		if (routines.isEmpty() || (routines.indexOf("<") >= 0)) {
			String message = "NO routine in the namespace" + routineNamespace + " is found.";
			MessageDialogHelper.showError(message);
			return null;						
			
		}
		
		String[] routineArray = routines.split("\n");
			
		int overallSeverity = IStatus.OK;
		List<IStatus> statuses = new ArrayList<>();
		for (String routineName : routineArray) {
			CommandResult<MServerRoutine> r = CommandEngine.loadRoutine(connection, folder.getProject(), routineName);
			IFile file = r.getResultObject().getFileHandle();			
			String prefixForFile = (file == null) ? routineName + " -- ": file.getFullPath().toString() + " -- ";
			IStatus status = r.getStatus();
			overallSeverity = this.updateStatuses(status, prefixForFile, overallSeverity, statuses);
		}
		
		this.showFinalMessage(overallSeverity, statuses);
		return null;
	}
}
