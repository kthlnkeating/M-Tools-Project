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

package gov.va.mumps.debug.ui;

import gov.va.mumps.debug.core.model.MDebugTarget;
import gov.va.mumps.debug.ui.console.MDevConsole;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

class GenericUIManager implements ILaunchListener {
	Map<MDebugTarget, MDevConsole> consoles = new HashMap<MDebugTarget, MDevConsole>(5);
	
	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(ILaunch launch) {	
		if (launch.getDebugTarget() != null) {
			MDebugTarget mDebugTarget = (MDebugTarget) launch.getDebugTarget();
		
			synchronized (mDebugTarget) {
				if (! mDebugTarget.isLinkedToConsole()) {
					MDevConsole mDevConsole = new MDevConsole("MUMPS Console: " +launch.getLaunchConfiguration().getName(), null, null, true);
					ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { mDevConsole });
			
					mDebugTarget.addReadCommandListener(mDevConsole);
					mDebugTarget.addWriteCommandListener(mDevConsole);
					mDevConsole.addInputReadyListener(mDebugTarget);
			
					mDebugTarget.setLinkedToConsole(true);
					this.consoles.put(mDebugTarget, mDevConsole);
				}
			}
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {	
		if (launch.getDebugTarget() != null) {
			MDebugTarget mDebugTarget = (MDebugTarget) launch.getDebugTarget();
			MDevConsole mDevConsole = consoles.get(mDebugTarget);
		
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] { mDevConsole});
			this.consoles.remove(mDevConsole);
		}
	}
}