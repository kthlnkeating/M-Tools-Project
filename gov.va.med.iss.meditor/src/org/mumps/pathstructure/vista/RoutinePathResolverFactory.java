package org.mumps.pathstructure.vista;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

import java.io.File;

import org.mumps.pathstructure.generic.FileDialogPathResolver;
import org.mumps.pathstructure.generic.RootPathResolver;
import org.mumps.pathstructure.vista.foia.PreferencesPathResolver;
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

		if (Boolean.valueOf(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_SERVER)) || Integer.parseInt(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_NAMESPACE)) > 0) {
			String serverName = Boolean.valueOf(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_SERVER)) ?
					VistaConnection.getPrimaryServerName() : null;
			return new PreferencesPathResolver(serverName, Integer.parseInt(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_NAMESPACE)));
		}
		
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
