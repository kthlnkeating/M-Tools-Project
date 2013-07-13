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

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

/**
 * Implements M language debugger.
 */
public class MDebugCorePlugin extends Plugin {
	
	private static MDebugCorePlugin instance = null;
	
	public MDebugCorePlugin() {
		super();
		MDebugCorePlugin.instance = this;
	}
	
	/**
	 * Returns the current instance of the plug-in.
	 */
	public static MDebugCorePlugin getInstance() {
		return MDebugCorePlugin.instance;
	}
	
	public String getPluginId() {
		Bundle bundle = this.getBundle();
		if (bundle != null) {
			String result = bundle.getSymbolicName();
			if (result != null) return result;
		}
		return "gov.va.med.iss.debug.core";
	}
	
}
