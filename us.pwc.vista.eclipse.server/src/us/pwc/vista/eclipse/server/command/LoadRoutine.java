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

import java.util.List;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreePath;

import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.core.LoadRoutineEngine;
import us.pwc.vista.eclipse.server.core.MServerRoutine;
import us.pwc.vista.eclipse.server.core.RoutineDirectory;
import us.pwc.vista.eclipse.server.dialog.InputDialogHelper;

/**
 * This implementation of <code>AbstractHandler</code> loads the selected M 
 * files (routines), or the specified routine or all the routines in the specified 
 * namespace from the M server.
 *
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class LoadRoutine extends AbstractHandler {
	private IFolder getFolder(final ExecutionEvent event) {
		TreePath[] paths = CommandCommon.getTreePaths(event);
		if (paths == null) {
			return null;
		}
		if (paths.length != 1) {
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, Messages.MULTI_LOAD_RTN_FOLDER_SINGLE);
			return null;
		}		
		IFolder folder = ResourceUtilExtension.getFolder(paths[0]);
		if (folder == null) {
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, Messages.MULTI_LOAD_RTN_FOLDER_ONLY);
			return null;			
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		if (! folder.getProject().getName().equals(projectName)) {
			String message = Messages.bind(Messages.CONNECTION_INVALID_PROJECT, projectName);
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, message);
			return null;						
		}
	
		return folder;
	}
	
	private String[] getRoutinesInNamespace() {
		String title = Messages.bind2(Messages.LOAD_M_RTNS_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
		String routineNamespace = InputDialogHelper.getRoutineNamespace(title);
		if (routineNamespace == null) {
			return null;
		}
		
		String routines = RoutineDirectory.getRoutineList(routineNamespace);
		if (routines.isEmpty() || (routines.indexOf("<") >= 0)) {
			String message = Messages.bind(Messages.MULTI_LOAD_RTN_NONE_IN_NAMESPC, routineNamespace);
			MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, message);
			return null;						
			
		}
		
		String[] routineArray = routines.split("\n");
		return routineArray;
	}
	
	private String[] getRoutine() {
		String title = Messages.bind2(Messages.LOAD_M_RTN_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
		String routineName = InputDialogHelper.getRoutineName(title);
		if (routineName == null) {
			return null;
		}
		return new String[]{routineName};
	}
	
	private List<IFile> getFiles(final ExecutionEvent event, String projectName, boolean namespaceFlag, boolean folderFlag) {
		if (folderFlag) {
			IFolder folder = this.getFolder(event);
			if (folder == null) {
				return null;
			}
			IProject project = folder.getProject();
			if (! project.getName().equals(projectName)) {
				String message = Messages.bind2(Messages.PROJECT_INVALID_FILE, projectName, folder.getName(), folder.getProject().getName());
				MessageDialogHelper.showError(Messages.LOAD_MSG_TITLE, message);
				return null;
			}
		
			String[] routines = namespaceFlag ? this.getRoutinesInNamespace() : this.getRoutine();
			if (routines == null) {
				return null;
			}
			
			return CommandCommon.getFileHandles(folder, routines);
		} else {
			List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event, projectName);
			return selectedFiles;
		}		
	}
		
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Object namespaceParam = event.getObjectParameterForExecution("us.pwc.vista.eclipse.server.command.loadRoutine.namespace");
		boolean namespaceFlag = ((Boolean) namespaceParam).booleanValue();
		
		Object folderParam = event.getObjectParameterForExecution("us.pwc.vista.eclipse.server.command.loadRoutine.folder");
		boolean folderFlag = ((Boolean) folderParam).booleanValue();

		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}

		String projectName = VistaConnection.getPrimaryProject();
		List<IFile> files = getFiles(event, projectName, namespaceFlag, folderFlag);
		if (files.size() == 1) {
			CommandResult<MServerRoutine> r = LoadRoutineEngine.loadRoutine(connection, files.get(0));
			MessageDialogHelper.logAndShow(Messages.LOAD_MSG_TITLE, r.getStatus());
		} else {
			CommandCommon.loadRoutines(connection, files);
		}
		return null;
	}
}
