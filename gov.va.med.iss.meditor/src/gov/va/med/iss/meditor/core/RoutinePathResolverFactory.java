package gov.va.med.iss.meditor.core;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

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
	
	public RoutinePathResolver getRoutinePathResolver(IProject project) {
		boolean saveUsingServerName = Boolean.valueOf(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_SERVER));
		int namespaceDigits = Integer.parseInt(MEditorPrefs.getPrefs(MEditorPlugin.P_SAVE_BY_NAMESPACE));		
		if (saveUsingServerName ||  (namespaceDigits > 0)) {
			String serverName = saveUsingServerName ? VistaConnection.getPrimaryServerName() : null;
			return new PreferencesPathResolver(serverName, namespaceDigits);
		}
		
		IResource resource = project.findMember("Packages.csv");
		if (resource != null) {
			IFile file = (IFile) resource;
			return new VFPathResolver(new VFPackageRepo(file));
		} 
		return null;
	}
}
