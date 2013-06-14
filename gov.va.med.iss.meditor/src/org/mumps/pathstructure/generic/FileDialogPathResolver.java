package org.mumps.pathstructure.generic;

import java.nio.file.FileSystems;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mumps.pathstructure.vista.RoutinePathResolver;

/**
 * Uses the eclipse SWT ElementTreeSelectionDialog class to get a file location
 * from the user.
 */
public class FileDialogPathResolver implements RoutinePathResolver {
	
	private static final String SEP = FileSystems.getDefault().getSeparator();
	
	private ElementTreeSelectionDialog dialog;
	
	public FileDialogPathResolver(String absPath) {
		String projectName = absPath.substring(absPath.lastIndexOf(SEP)+SEP.length());
		CustElementTreeSelectionDialog dialog = new CustElementTreeSelectionDialog(Display.getDefault().getActiveShell(),
				new WorkbenchLabelProvider(),
			    new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);
		dialog.setMessage("Select a directory to load the routine into.");
		dialog.setTitle("Routine Import");
		ViewerFilter filter = new ProjectDirViewerFilter(projectName);
		dialog.addFilter(filter);
		this.dialog = dialog;
	}

	@Override
	public String getRelativePath(String routineName) {
		
		if (dialog.open() == Window.OK) {
			IResource resource = (IResource) dialog.getFirstResult();
			String path = resource.getFullPath().removeFirstSegments(1).toOSString(); //remove the projectName
			return path;
		} else {
			throw new RuntimeException("User cancelled file selection");
		}
	}
}
