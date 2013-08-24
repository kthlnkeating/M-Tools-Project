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

package us.pwc.vista.eclipse.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;

public class VistACorePrefs {
	private static final String P_SERVER_NUM = "Server_Preference_";
	
	private static final String BACKUP_DIR_PREF_NAME = "backupdir"; //$NON-NLS-1$
	private static final String BACKUP_DIR_DEFAULT = ".backups"; //$NON-NLS-1$
	private static final QualifiedName BACKUP_DIR = new QualifiedName(VistACorePlugin.PLUGIN_ID, BACKUP_DIR_PREF_NAME);

	private static final String SERVER_NAME_PREF_NAME = "serverName"; //$NON-NLS-1$
	private static final QualifiedName SERVER_NAME = new QualifiedName(VistACorePlugin.PLUGIN_ID, SERVER_NAME_PREF_NAME);

	private static final String IGNORE_SERVER_SUBFOLDERS_PREF_NAME = "ignoreServerNameSubfolders"; //$NON-NLS-1$
	private static final QualifiedName IGNORE_SERVER_SUBFOLDERS = new QualifiedName(VistACorePlugin.PLUGIN_ID, IGNORE_SERVER_SUBFOLDERS_PREF_NAME);

	public static void setServerName(IProject project, String value) throws CoreException {
		project.setPersistentProperty(SERVER_NAME, value);
 	}
	
	public static String getServerName(IProject project) throws CoreException {
       	String var = project.getPersistentProperty(SERVER_NAME);
        if (var == null) {
            return "";
        } else {
        	return var;
        }
	}
	
	public static void setServerBackupDirectory(IProject project, String value) throws CoreException {
		project.setPersistentProperty(BACKUP_DIR, value);
 	}

	public static String getServerBackupDirectory(IProject project) throws CoreException {
       	String var = project.getPersistentProperty(BACKUP_DIR);
        if (var == null) {
            return BACKUP_DIR_DEFAULT;
        } else {
        	return var;
        }
	}
	
	public static void setDoNotUseServerNameFoldersFlag(IProject project, boolean value) throws CoreException {
		project.setPersistentProperty(IGNORE_SERVER_SUBFOLDERS, String.valueOf(value));
 	}

	public static boolean getDoNotUseServerNameFoldersFlag(IProject project) throws CoreException {
       	String var = project.getPersistentProperty(IGNORE_SERVER_SUBFOLDERS);
        if (var == null) {
            return false;
        } else {
        	return Boolean.valueOf(var);
        }
	}
	
	public static void setServers(List<ServerData> values) {
		IPreferenceStore store = VistACorePlugin.getDefault().getPreferenceStore();		
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
	
	public static List<ServerData> getServers() {
		IPreferenceStore store = VistACorePlugin.getDefault().getPreferenceStore();
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
	
	public static String[] getDoNotUseProjectSubfolders(IProject project, String serverName) {
		List<String> folderNames = new ArrayList<String>();
		try {
			String backupFolderName = getServerBackupDirectory(project);
			if (! backupFolderName.isEmpty()) {
				folderNames.add(backupFolderName);
			}
			if (getDoNotUseServerNameFoldersFlag(project)) {
				String cmpServerName = (serverName == null) ? getServerName(project) : serverName;
				List<ServerData> serverDataList = getServers();
				for (ServerData serverData : serverDataList) {
					String name = serverData.getName();
					if (! name.equals(cmpServerName)) {
						folderNames.add(name);
					}
				}				
			}
			if (folderNames.size() == 0) {
				return null;
			} else {
				String[] result = folderNames.toArray(new String[0]);
				return result;
			}			
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VistACorePlugin.PLUGIN_ID);
			return null;
		}
	}
}
