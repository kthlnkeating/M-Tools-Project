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

package us.pwc.vista.eclipse.server.dialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class CustomDialogHelper {
	public static IContainer selectWritableFolder(IProject project) {
		Shell shell = Display.getDefault().getActiveShell();
		WorkbenchLabelProvider wlp = new WorkbenchLabelProvider();
		WorkbenchContentProvider wcp = new WorkbenchContentProvider();

		CustElementTreeSelectionDialog dialog = new CustElementTreeSelectionDialog(shell, wlp, wcp);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		dialog.setInput(root);
		dialog.setAllowMultiple(false);
		dialog.setMessage("Select a directory to load the routine into.");
		dialog.setTitle("Routine Import");
		ViewerFilter filter = new ProjectDirViewerFilter(project.getName());
		dialog.addFilter(filter);
		int result = dialog.open();
		if (result == Window.OK) {			
			IContainer container = (IContainer) dialog.getFirstResult();
			return container;
		} else {
			return null;
		}		
	}
}
