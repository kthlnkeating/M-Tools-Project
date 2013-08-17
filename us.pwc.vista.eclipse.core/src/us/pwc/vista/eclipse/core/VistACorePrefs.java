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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class VistACorePrefs {
	private static final String BACKUP_DIR_PREF_NAME = "backupdir"; //$NON-NLS-1$
	private static final String BACKUP_DIR_DEFAULT = ".backups"; //$NON-NLS-1$
	private static final QualifiedName BACKUP_DIR = new QualifiedName(VistACorePlugin.PLUGIN_ID, BACKUP_DIR_PREF_NAME);

	public static String getServerBackupDirectory(IProject project) throws CoreException {
       	String var = project.getPersistentProperty(BACKUP_DIR);
        if (var == null) {
            return BACKUP_DIR_DEFAULT;
        } else {
        	return var;
        }
	}
	
	public static void setServerBackupDirectory(IProject project, String value) throws CoreException {
		project.setPersistentProperty(BACKUP_DIR, value);
 	}
}
