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

package gov.va.mumps.debug.core;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import gov.va.mumps.debug.core.model.MDebugPreference;

public class MDebugSettings {
	private static final String DEBUG_PREFERENCE = "debugPreference";
	private static final String VISTA_NAMESPACE = "vistaNamespace";

	public static MDebugPreference getDebugPreference() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String storedValue = preferences.get(DEBUG_PREFERENCE, null);
		if (storedValue == null) {
			return MDebugPreference.GENERIC;
		} else {			
			return MDebugPreference.valueOf(storedValue);
		}
	}
	
	public static void setDebugPreference(MDebugPreference preference) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String valueToBeStored = preference.toString();
		preferences.put(DEBUG_PREFERENCE, valueToBeStored);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}
	
	public static String getNamespace() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		String namespace = preferences.get(VISTA_NAMESPACE, null);
		if (namespace == null) {
			return "VISTA";
		} else {			
			return namespace;
		}
	}
	
	public static void setNamespace(String namespace) {
		IEclipsePreferences preferences =  InstanceScope.INSTANCE.getNode(MDebugCorePlugin.PLUGIN_ID);
		preferences.put(VISTA_NAMESPACE, namespace);
		try {
			preferences.flush();
		}  catch (Throwable t) {			
		}
	}	
}
