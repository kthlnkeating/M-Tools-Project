package org.mumps.pathstructure.generic;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.mumps.pathstructure.vista.RoutinePathResolver;

/**
 * Uses the eclipse SWT ElementTreeSelectionDialog class to get a file location
 * from the user.
 */
public class FileDialogPathResolver implements RoutinePathResolver {
	
	private ElementTreeSelectionDialog dialog;
	
	public FileDialogPathResolver(ElementTreeSelectionDialog dialog) {
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
