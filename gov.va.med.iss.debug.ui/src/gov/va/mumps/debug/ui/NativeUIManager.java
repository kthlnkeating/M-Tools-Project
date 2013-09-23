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

import java.util.HashMap;
import java.util.Map;

import gov.va.mumps.debug.core.model.IMTerminal;
import gov.va.mumps.debug.core.model.IMTerminalManager;
import gov.va.mumps.debug.core.model.MNativeDebugTarget;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;


class NativeUIManager implements IMUIManager {
	private Map<String, IMTerminal> views = new HashMap<String, IMTerminal>();
	private IMTerminalManager terminalManager;
	
	public NativeUIManager(IMTerminalManager terminalManager) {
		this.terminalManager = terminalManager;
	}
	
	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(ILaunch launch) {
		final String launchId = String.valueOf(System.identityHashCode(launch));		
		if (launch.getDebugTarget() != null) {
			MNativeDebugTarget target = (MNativeDebugTarget) launch.getDebugTarget();
			IConsoleManager consoleManager = MessageConsoleHelper.getConsoleManager();
			MessageConsole messageConsole = MessageConsoleHelper.findConsole(consoleManager, "M Debug");
			consoleManager.showConsoleView(messageConsole);
			IMTerminal terminal = this.terminalManager.create(launchId, target);
			if (terminal != null) {
				this.views.put(launchId, terminal);
			}			
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		String launchId = String.valueOf(System.identityHashCode(launch));
		IMTerminal t = this.views.get(launchId);
		this.terminalManager.close(t);
	}
	
	@Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
		Display.getDefault().syncExec(new Runnable() {						
			@Override
			public void run() {
				for (IMTerminal t : NativeUIManager.this.views.values()) {
					NativeUIManager.this.terminalManager.close(t);
				}
			}
		});		
		return true;
    }
 
	@Override
    public void postShutdown(IWorkbench workbench) { 
    }
}