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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import gov.va.med.iss.connection.ConnectionData;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.core.resource.FileFillState;
import us.pwc.vista.eclipse.core.resource.FileSetSearchVisitor;
import us.pwc.vista.eclipse.core.resource.IResourceFilter;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;
import us.pwc.vista.eclipse.core.resource.SelectedMFileFilter;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.core.CommandResult;
import us.pwc.vista.eclipse.server.core.LoadRoutineEngine;
import us.pwc.vista.eclipse.server.core.MServerRoutine;
import us.pwc.vista.eclipse.server.core.StatusHelper;

public class CommandCommon {
	public static boolean openEditor(ExecutionEvent event, IFile file) {	
		try {
			IWorkbenchWindow w = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = w.getActivePage();
			IDE.openEditor(page, file);
			return true;
		} catch (Throwable t) {
			String message = Messages.bind(Messages.UNABLE_OPEN_EDITOR, file.getName());
			MessageDialogHelper.logAndShow(message, t);
			return false;
		}
	}
	
	private static List<IFile> getMFiles(TreePath[] selections) throws CoreException {
		IResourceFilter filter = new SelectedMFileFilter();
		FileFillState result = ResourceUtilExtension.getSelectedFiles(selections, filter);
		String invalids = result.getInvalidResourcesAsString(3, "\n");
		if (invalids != null) {
			invalids = "\n" + invalids;
			String message = Messages.bind(Messages.NOT_SUPPORTED_RESOURCES, invalids); 
			MessageDialogHelper.showError(Messages.FILE_SELECT_TITLE, message);
			return null;
		}
		List<IFile> files = result.getFiles();
		if (files.size() == 0) {
			MessageDialogHelper.showError(Messages.FILE_SELECT_TITLE, Messages.NO_FILES);
			return null;			
		}
		return files;
	}
	
	public static TreePath[] getTreePaths(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if ((selection == null) || (! (selection instanceof TreeSelection))) {
			MessageDialogHelper.showError(Messages.FILE_SELECT_TITLE, Messages.NOT_SUPPORTED_SELECTION_LIST);
			return null;
		}
		TreeSelection ts = (TreeSelection) selection;
		TreePath[] selections = ts.getPaths();
		return selections;	
	}

	public static List<IFile> getSelectedMFiles(ExecutionEvent event) {
		try {
			TreePath[] paths = getTreePaths(event);
			if (paths == null) {
				return null;
			} else {
				List<IFile> files = getMFiles(paths);
				return files;				
			}
		} catch (Throwable t) {
			MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, t);
			return null;
		}
	}

	public static void showMultiStatus(int overallSeverity, String title, String message, List<IStatus> statuses) {
		MultiStatus multiStatus = StatusHelper.getMultiStatus(overallSeverity, message, statuses);
		MessageDialogHelper.showMulti(title, multiStatus);		
	}
	
	public static List<IFile> getFileHandles(IFolder defaultFolder, String[] routineNames) {
		List<String> fileNames = new ArrayList<String>();
		for (String routineName : routineNames) {
			String fileName = routineName + ".m";
			fileNames.add(fileName);
		}		
		try {
			IProject project = defaultFolder.getProject();
			FileSetSearchVisitor visitor = new FileSetSearchVisitor(fileNames);
			visitor.run(project, null);
			return visitor.getFiles(defaultFolder);
		} catch (Throwable t) {
			MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, t);
			return null;
		}
	}
	
	public static String selectMessageOnStatus(int severity, String[] messages) {
		if (severity == IStatus.ERROR) {
			return messages[0];
		} else if (severity == IStatus.WARNING) {
			return messages[1];			
		} else {
			return messages[2];
		}
		
	}
	
	private static class MultipleRoutineLoad extends WorkspaceModifyOperation {
		private ConnectionData connectionData;
		private List<IFile> files;
 		
		private List<IStatus> statuses;
		private int overallSeverity;
		
		public MultipleRoutineLoad(ConnectionData connectionData, List<IFile> files) {
			super();
			this.connectionData = connectionData;
			this.files = files;
		}

		@Override
		protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException	{
			this.overallSeverity = IStatus.OK;
			this.statuses = new ArrayList<IStatus>();
			monitor.beginTask("Load Routines", this.files.size());
			int i = 0;
			for (IFile file : this.files) {
				CommandResult<MServerRoutine> r = LoadRoutineEngine.loadRoutine(this.connectionData, file);
				String prefixForFile = file.getFullPath().toString() + " -- ";
				IStatus status = r.getStatus();
				this.overallSeverity = StatusHelper.updateStatuses(status, VistAServerPlugin.PLUGIN_ID, prefixForFile, this.overallSeverity, this.statuses);
				i = i + 1;
				if (monitor.isCanceled()) break;
				monitor.worked(i);
			}
			monitor.done();
		}
	}
	
	public static void loadRoutines(final ConnectionData connectionData, final List<IFile> files) {
		try {
			Shell shell = Display.getDefault().getActiveShell();
			ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
			MultipleRoutineLoad mrl = new MultipleRoutineLoad(connectionData, files);
			pmd.run(true, true, mrl);
	
			String[] topMessages = new String[]{Messages.MULTI_LOAD_RTN_ERRR, Messages.MULTI_LOAD_RTN_WARN, Messages.MULTI_LOAD_RTN_INFO};
			String topMessage = CommandCommon.selectMessageOnStatus(mrl.overallSeverity, topMessages);
			CommandCommon.showMultiStatus(mrl.overallSeverity, Messages.LOAD_MSG_TITLE, topMessage, mrl.statuses);		
		} catch (Throwable t) {
			MessageDialogHelper.logAndShow(VistAServerPlugin.PLUGIN_ID, t);
		}
	}
}
