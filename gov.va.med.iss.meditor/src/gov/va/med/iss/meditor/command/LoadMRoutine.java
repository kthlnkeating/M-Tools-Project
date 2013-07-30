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
import gov.va.med.iss.meditor.core.LoadRoutineEngine;
import gov.va.med.iss.meditor.core.CommandResult;
import gov.va.med.iss.meditor.core.MServerRoutine;
import gov.va.med.iss.meditor.dialog.InputDialogHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

public class LoadMRoutine extends AbstractHandler {
	private static IProject getProject(String projectName) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IProject project = root.getProject(projectName);
			if (! project.exists()) {
				project.create(new NullProgressMonitor());
			}
			if (! project.isOpen()) {
				project.open(new NullProgressMonitor());
			}
			return project;
		} catch (CoreException ce) {
			String message = Messages.bind(Messages.UNABLE_GET_PROJECT, projectName, ce.getMessage());
			MessageDialogHelper.logAndShow(message, ce);
			return null;
		} catch (Throwable t) {
			MessageDialogHelper.logAndShowUnexpected(t);
			return null;
		}
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		String title = Messages.bind2(Messages.LOAD_M_RTN_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
		String routineName = InputDialogHelper.getRoutineName(title);
		if (routineName == null) {
			return null;
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		IProject project = getProject(projectName);
		if (project == null) {
			return null;
		}
		
		CommandResult<MServerRoutine> result = LoadRoutineEngine.loadRoutine(connection, project, routineName);
		IStatus status = result.getStatus();		
		if (status.getSeverity() != IStatus.OK) {
			MessageDialogHelper.logAndShow(status);			
		}
		if (status.getSeverity() != IStatus.ERROR) {
			IFile file = result.getResultObject().getFileHandle();
			CommandCommon.openEditor(event, file);
		}
		return null;
	}
}
