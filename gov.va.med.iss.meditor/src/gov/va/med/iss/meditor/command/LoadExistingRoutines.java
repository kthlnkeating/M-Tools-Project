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
import gov.va.med.iss.meditor.command.utils.MServerRoutine;
import gov.va.med.iss.meditor.command.utils.StatusHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

public class LoadExistingRoutines extends AbstractHandler {
	private static String getTopMessage(int overallSeverity) {
		if (overallSeverity == IStatus.ERROR) {
			return "Some file could not be loaded due to errors.";
		} else if (overallSeverity == IStatus.WARNING) {
			return "All files are loaded but some with warnings";			
		} else {
			return "All files are loaded successfully.";
		}
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}

		String projectName = VistaConnection.getPrimaryProject();
		List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event, projectName);
		if (selectedFiles == null) {
			return null;
		}
				
		int overallSeverity = IStatus.OK;
		List<IStatus> statuses = new ArrayList<>();
		for (IFile file : selectedFiles) {
			CommandResult<MServerRoutine> r = CommandEngine.loadRoutine(connection, file);
			String filePathPrefix = file.getFullPath().toString() + " -- ";
			IStatus status = r.getStatus();
			if (status.getSeverity() == IStatus.OK) {
				IStatus newStatus = StatusHelper.getStatus(IStatus.INFO, filePathPrefix + "no issues");
				statuses.add(newStatus);
			} else {
				IStatus newStatus = StatusHelper.getStatus(status, filePathPrefix + status.getMessage() + "\n");
				statuses.add(newStatus);
				int severity = status.getSeverity();
				overallSeverity = StatusHelper.updateOverallSeverity(overallSeverity, severity);				
			}
		}
		
		String message = getTopMessage(overallSeverity);
		MultiStatus multiStatus = StatusHelper.getMultiStatus(overallSeverity, message, statuses);
		MessageDialogHelper.showMulti(multiStatus);
		return null;
	}
}
