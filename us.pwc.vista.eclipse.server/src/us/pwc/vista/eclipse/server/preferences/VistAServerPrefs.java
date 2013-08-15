package us.pwc.vista.eclipse.server.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import us.pwc.vista.eclipse.server.VistAServerPlugin;

public class VistAServerPrefs {
	private static final QualifiedName NEW_FILE_FOLDER_SCHEME = 
			new QualifiedName(VistAServerPlugin.PLUGIN_ID, "newFileFolderScheme"); //$NON-NLS-1$
	private static final QualifiedName ADD_SERVER_NAME_SUBFOLDER = 
			new QualifiedName(VistAServerPlugin.PLUGIN_ID, "addServerNameSubfolder"); //$NON-NLS-1$
	private static final QualifiedName ADD_NAMESPACE_CHARS_SUBFOLDER = 
			new QualifiedName(VistAServerPlugin.PLUGIN_ID, "addNamespaceCharsSubfolder"); //$NON-NLS-1$
	
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
