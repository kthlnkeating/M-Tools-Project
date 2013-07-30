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

import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.command.resource.FileFillState;
import gov.va.med.iss.meditor.command.resource.FileSetSearchVisitor;
import gov.va.med.iss.meditor.command.resource.IResourceFilter;
import gov.va.med.iss.meditor.command.resource.ResourceUtilsExtension;
import gov.va.med.iss.meditor.command.utils.StatusHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class CommandCommon {
	public static boolean openEditor(ExecutionEvent event, IFile file) {	
		try {
			IWorkbenchWindow w = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = w.getActivePage();
			FileEditorInput editorInput = new FileEditorInput(file);
			page.openEditor(editorInput, MEditor.M_EDITOR_ID);		
			return true;
		} catch (Throwable t) {
			String message = Messages.bind(Messages.UNABLE_OPEN_EDITOR, file.getName());
			MessageDialogHelper.logAndShow(message, t);
			return false;
		}
	}
	
	private static class SelectedMFileFilter implements IResourceFilter {
		private String projectName;

		public SelectedMFileFilter(String projectName) {
			super();
			this.projectName = projectName;
		}
		
		private boolean checkProject(IProject project) {
			String name = project.getName();
			return this.projectName.equals(name);
		}
		
		@Override
		public boolean isValid(IResource resource) {
			if (resource instanceof IProject) {
				IProject project = (IProject) resource;
				return this.checkProject(project);
			} else if (resource instanceof IFolder) {
				IFolder folder = (IFolder) resource;
				return this.checkProject(folder.getProject());				
			} else if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				String extension = file.getFullPath().getFileExtension();
				return "m".equals(extension) && this.checkProject(file.getProject());
 			} else {
				return false;
			}
		}		
	}
	
	private static List<IFile> getMFiles(TreePath[] selections, String projectName) throws CoreException {
		IResourceFilter filter = new SelectedMFileFilter(projectName);
		FileFillState result = ResourceUtilsExtension.getSelectedFiles(selections, filter);
		String invalids = result.getInvalidResourcesAsString(4, "\n");
		if (invalids != null) {
			invalids = "\n" + invalids;
			String message = Messages.bind(Messages.NOT_SUPPORTED_RESOURCES, invalids); 
			MessageDialogHelper.showError(message);
			return null;
		}
		List<IFile> files = result.getFiles();
		if (files.size() == 0) {
			MessageDialogHelper.showError(Messages.NO_FILES);
			return null;			
		}
		return files;
	}
	
	public static TreePath[] getTreePaths(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if ((selection == null) || (! (selection instanceof TreeSelection))) {
			MessageDialogHelper.showError(Messages.NOT_SUPPORTED_SELECTION_LIST);
			return null;
		}
		TreeSelection ts = (TreeSelection) selection;
		TreePath[] selections = ts.getPaths();
		return selections;	
	}

	public static List<IFile> getSelectedMFiles(ExecutionEvent event, String projectName) {
		try {
			TreePath[] paths = getTreePaths(event);
			if (paths == null) {
				return null;
			} else {
				List<IFile> files = getMFiles(paths, projectName);
				return files;				
			}
		} catch (Throwable t) {
			MessageDialogHelper.logAndShowUnexpected(t);
			return null;
		}
	}

	public static void showMultiStatus(int overallSeverity, String message, List<IStatus> statuses) {
		MultiStatus multiStatus = StatusHelper.getMultiStatus(overallSeverity, message, statuses);
		MessageDialogHelper.showMulti(multiStatus);		
	}
	
	public static List<IFile> getFileHandles(IFolder defaultFolder, String[] routineNames) {
		List<String> fileNames = new ArrayList<String>();
		for (String routineName : routineNames) {
			String fileName = routineName + ".m";
			fileNames.add(fileName);
		}		
		try {
			String backupDirectory = MEditorPrefs.getServerBackupFolderName();
			FileSetSearchVisitor visitor = new FileSetSearchVisitor(fileNames, backupDirectory);
			IProject project = defaultFolder.getProject();
			project.accept(visitor, 0);
			return visitor.getFiles(defaultFolder);
		} catch (Throwable t) {
			MessageDialogHelper.logAndShow(t);
			return null;
		}
	}
}
