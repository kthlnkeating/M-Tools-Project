package gov.va.med.iss.meditor.preferences;

import gov.va.med.iss.meditor.MEditorPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;

public class MEditorPrefs {
	private static final QualifiedName NEW_FILE_FOLDER_SCHEME = 
			new QualifiedName(MEditorPlugin.PLUGIN_ID, "newFileFolderScheme"); //$NON-NLS-1$
	private static final QualifiedName ADD_SERVER_NAME_SUBFOLDER = 
			new QualifiedName(MEditorPlugin.PLUGIN_ID, "addServerNameSubfolder"); //$NON-NLS-1$
	private static final QualifiedName ADD_NAMESPACE_CHARS_SUBFOLDER = 
			new QualifiedName(MEditorPlugin.PLUGIN_ID, "addNamespaceCharsSubfolder"); //$NON-NLS-1$
	
	private static MEditorPreferencesPage mepp = null;
	
	public static String getPrefs(String prefName) {
		if (mepp == null) {
			mepp = new MEditorPreferencesPage();
		}
		String prefValue = Platform.getPreferencesService().getString(MEditorPlugin.PLUGIN_ID, prefName, "", null);
		return prefValue;
	}

	public static boolean getAutoSaveToServer() {
		String p = MEditorPrefs.getPrefs(MEditorPlugin.P_AUTO_SAVE_TO_SERVER);		
		return Boolean.valueOf(p);		
	}
	
	public static NewFileFolderScheme getNewFileFolderScheme(IProject project) throws CoreException {
       	String p = project.getPersistentProperty(NEW_FILE_FOLDER_SCHEME);
        if (p == null) {
            return NewFileFolderScheme.ASK;
        } else {
        	return NewFileFolderScheme.valueOf(p);
        }		
	}
	
	public static void setNewFileFolderScheme(IProject project, NewFileFolderScheme folderScheme) throws CoreException {
		project.setPersistentProperty(NEW_FILE_FOLDER_SCHEME, folderScheme.toString());
	}
	
	public static boolean getAddServerNameSubfolder(IProject project) throws CoreException {
       	String p = project.getPersistentProperty(ADD_SERVER_NAME_SUBFOLDER);
        if (p == null) {
            return true;
        } else {
        	return Boolean.parseBoolean(p);
        }				
	}

	public static void setAddServerNameSubfolder(IProject project, boolean value) throws CoreException {
		project.setPersistentProperty(ADD_SERVER_NAME_SUBFOLDER, String.valueOf(value));
	}
	
	public static int getAddNamespaceCharsSubfolder(IProject project) throws CoreException {
       	String p = project.getPersistentProperty(ADD_NAMESPACE_CHARS_SUBFOLDER);
        if (p == null) {
            return 0;
        } else {
        	return Integer.parseInt(p);
        }		
	}
	
	public static void setAddNamespaceCharsSubfolder(IProject project, int value) throws CoreException {
		project.setPersistentProperty(ADD_NAMESPACE_CHARS_SUBFOLDER, String.valueOf(value));
	}
}
