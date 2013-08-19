package gov.va.med.iss.connection.preferences;

import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class VistAConnectionPrefs {
	private static final String SERVER_NAME_PREF_NAME = "serverName"; //$NON-NLS-1$
	private static final QualifiedName SERVER_NAME = new QualifiedName(VLConnectionPlugin.PLUGIN_ID, SERVER_NAME_PREF_NAME);

	public static String getServerName(IProject project) throws CoreException {
       	String var = project.getPersistentProperty(SERVER_NAME);
        if (var == null) {
            return "";
        } else {
        	return var;
        }
	}
	
	public static void setServerName(IProject project, String value) throws CoreException {
		project.setPersistentProperty(SERVER_NAME, value);
 	}	
}
