package gov.va.med.iss.connection.preferences;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;

public class VistAConnectionPrefs {
	public static final String P_SERVER_NUM = "Server_Preference_";
	
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
	
	public static List<ServerData> getServers() {
		IPreferenceStore store = VLConnectionPlugin.getDefault().getPreferenceStore();
		ArrayList<ServerData> result = new ArrayList<ServerData>();
		int i = 1;
		while (true) {
			String dataString = store.getString(P_SERVER_NUM + String.valueOf(i));
			if (dataString.isEmpty()) {
				break;
			}
			ServerData sd = ServerData.valueOf(dataString);
			result.add(sd);
			++i;
		}
		return result;		
	}
	
	public static void setServers(List<ServerData> values) {
		IPreferenceStore store = VLConnectionPlugin.getDefault().getPreferenceStore();		
		int count = 1;
		for (ServerData value : values) {
			String name = P_SERVER_NUM + String.valueOf(count);
			String previous = store.getString(name);
			String current = value.toString();
			if (! current.equals(previous)) {
				store.setValue(name, current);
			}
			++count;
		}
		while (true) {
			String name = P_SERVER_NUM + String.valueOf(count);
			String dataString = store.getString(name);
			if (dataString.isEmpty()) {
				break;
			} else {
				store.setValue(name, "");
				
			}
			++count;
		}
	}
}
