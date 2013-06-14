package org.mumps.pathstructure.vista;

import java.io.File;
import java.nio.file.FileSystems;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mumps.pathstructure.generic.CustElementTreeSelectionDialog;
import org.mumps.pathstructure.generic.FileDialogPathResolver;
import org.mumps.pathstructure.generic.ProjectDirViewerFilter;
import org.mumps.pathstructure.generic.RootPathResolver;
import org.mumps.pathstructure.vista.foia.VFPackageRepo;
import org.mumps.pathstructure.vista.foia.VFPathResolver;

public class RoutinePathResolverFactory {
	
	//Singleton class, easier to put inside the existing code this way
	private static volatile RoutinePathResolverFactory psf = null;
	public static RoutinePathResolverFactory getInstance() {
		if (psf == null) {
			synchronized (VFPackageRepo.class) {
				if (psf == null)
					psf = new RoutinePathResolverFactory();
			}
		}
		return psf;
	}

	private RoutinePathResolverFactory() {
	}
	//Singleton class
	
	public RoutinePathResolver getRoutinePathResolver(File projectPath) {

		RoutinePathResolver result;
		
		File packagesCsvFile = new File(projectPath, "Packages.csv");
		RoutinePathResolver backupResolver;
		if (containsFolder(projectPath))
			backupResolver = new FileDialogPathResolver(projectPath.getAbsolutePath());
		else
			backupResolver = new RootPathResolver();
		
		if (packagesCsvFile.exists())
			result = new VFPathResolver(backupResolver, new VFPackageRepo(packagesCsvFile));
		else
			return backupResolver;

		return result;
	}

	private boolean containsFolder(File projectPath) {

		for (File file : projectPath.listFiles())
			if (file.isDirectory() && !file.getName().equals("backups"))
				return true;
		
		return false;
	}

//	public RoutinePathResolver getDialogResolver(File projectPath) {
//		String absPath = projectPath.getAbsolutePath();
//		String projectName = absPath.substring(absPath.lastIndexOf(SEP)+SEP.length());
//		CustElementTreeSelectionDialog dialog = new CustElementTreeSelectionDialog(Display.getDefault().getActiveShell(),
//				new WorkbenchLabelProvider(),
//			    new WorkbenchContentProvider());
//		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
//		dialog.setAllowMultiple(false);
//		dialog.setMessage("Select a directory to load the routine into.");
//		dialog.setTitle("Routine Import");
//		ViewerFilter filter = new ProjectDirViewerFilter(projectName);
//		dialog.addFilter(filter);
//		return new FileDialogPathResolver(dialog);
//	}

}
