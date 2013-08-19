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

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.preferences.ServerData;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.core.resource.FileSearchVisitor;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.core.LoadRoutineEngine;
import us.pwc.vista.eclipse.server.core.MServerRoutine;
import us.pwc.vista.eclipse.server.core.PreferencesPathResolver;
import us.pwc.vista.eclipse.server.core.RootPathResolver;
import us.pwc.vista.eclipse.server.core.VFPackageRepo;
import us.pwc.vista.eclipse.server.core.VFPathResolver;
import us.pwc.vista.eclipse.server.dialog.CustomDialogHelper;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;
import us.pwc.vista.eclipse.server.preferences.VistAServerPrefs;
import us.pwc.vista.eclipse.server.preferences.NewFileFolderScheme;

public class LoadMRoutine extends AbstractHandler {
	private static IFile getExistingFileHandle(IProject project, String routineName) throws CoreException {
		String backupDirectory = VistACorePrefs.getServerBackupDirectory(project);
		FileSearchVisitor visitor = new FileSearchVisitor(routineName + ".m", backupDirectory);
		project.accept(visitor, 0);
		return visitor.getFile();
	}
	
	public static IFile getNewFileHandle(IProject project, String serverNameIn, String routineName) throws CoreException {		
		NewFileFolderScheme locationScheme = VistAServerPrefs.getNewFileFolderScheme(project);
		switch (locationScheme) {
		case NAMESPACE_SPECIFIED:
			IResource resource = project.findMember("Packages.csv");
			if (resource != null) {
				IFile file = (IFile) resource;
				VFPathResolver vpr = new VFPathResolver(new VFPackageRepo(file));
				return vpr.getFileHandle(project, routineName);
			} else {
				RootPathResolver rpr = new RootPathResolver();
				rpr.getFileHandle(project, routineName);
			}			
		case PROJECT_ROOT:
			boolean serverNameToFolder = VistAServerPrefs.getAddServerNameSubfolder(project);
			int namespaceDigits = VistAServerPrefs.getAddNamespaceCharsSubfolder(project);
			String serverName = serverNameToFolder ? serverNameIn : null;
			PreferencesPathResolver ppr = new PreferencesPathResolver(serverName, namespaceDigits);
			return ppr.getFileHandle(project, routineName);
		default:
			IContainer container = CustomDialogHelper.selectWritableFolder(project);
			if (container != null) {
				IPath path = new Path(routineName + ".m");
				return container.getFile(path);
			} else {
				return null;
			}
		}
	}

	private static IFile getFileHandle(IProject project, String serverName, String routineName) throws ExecutionException {
		try {
			IFile fileHandle = getExistingFileHandle(project, routineName);
			if (fileHandle == null) {
				return getNewFileHandle(project, serverName, routineName);
			}
			return fileHandle;
		} catch (CoreException coreException) {
			String message = Messages.bind(Messages.UNABLE_GET_HANDLE, routineName);
			throw new ExecutionException(message, coreException);
		}
	}
		
	private IProject extractProject(ISelection selection) {
		if (! (selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if (element instanceof IResource) return ((IResource) element).getProject();
		if (!(element instanceof IAdaptable)) return null;
		IAdaptable adaptable = (IAdaptable)element;
		Object adapter = adaptable.getAdapter(IResource.class);
		if (adapter == null) return null;
		return ((IResource) adapter).getProject();
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		IProject project = extractProject(selection);
		if (project == null) {			
			IStatus status = new Status(IStatus.ERROR, VistAServerPlugin.PLUGIN_ID, "Please a select a project.");
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		}
		
		
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		ServerData data = VistaConnection.getServerData();
		String title = Messages.bind(Messages.LOAD_M_RTN_DLG_TITLE, data.serverAddress, data.port);
		String routineName = InputDialogHelper.getRoutineName(title);
		if (routineName == null) {
			return null;
		}
		
		IFile fileHandle = getFileHandle(project, VistaConnection.getServerData().serverName, routineName);
		if (fileHandle == null) {
			return null;
		}
		
		CommandResult<MServerRoutine> result = LoadRoutineEngine.loadRoutine(connection, fileHandle);
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
