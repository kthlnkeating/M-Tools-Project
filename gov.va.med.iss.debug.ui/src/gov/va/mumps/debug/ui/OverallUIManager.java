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

import gov.va.mumps.debug.core.model.IMDebugTarget;
import gov.va.mumps.debug.core.model.IMTerminalManager;
import gov.va.mumps.debug.ui.terminal.MTerminalManager;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.ui.IWorkbench;

class OverallUIManager implements IMUIManager {
	private IMUIManager nativeListener;
	private IMUIManager genericListener;
	private IMTerminalManager terminalManager = new MTerminalManager();
	
	private ILaunchListener getListener(ILaunch launch) {
		IDebugTarget target = launch.getDebugTarget();
		if (target instanceof IMDebugTarget) {
			IMDebugTarget mtarget = (IMDebugTarget) target;
			boolean isNative = mtarget.isNative();
			if (isNative) {
				if (this.nativeListener == null) {
					this.nativeListener = new NativeUIManager(this.terminalManager);					
				}
				return this.nativeListener;
			} else {
				if (this.genericListener == null) {
					this.genericListener = new GenericUIManager();					
				}
				return this.genericListener;
			}
		}
		return null;		
	}
			
	@Override
	public void launchAdded(ILaunch launch) {
		ILaunchListener listener = this.getListener(launch);
		if (listener != null) {
			listener.launchAdded(launch);
		}
	}

	@Override
	public void launchChanged(ILaunch launch) {
		ILaunchListener listener = this.getListener(launch);
		if (listener != null) {
			listener.launchChanged(launch);
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		ILaunchListener listener = this.getListener(launch);
		if (listener != null) {
			listener.launchRemoved(launch);
		}
	}

	@Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
		if (this.nativeListener != null) {
			this.nativeListener.preShutdown(workbench, forced);
		}
		if (this.genericListener != null) {
			this.genericListener.preShutdown(workbench, forced);
		}
		return true;
    }
 
	@Override
    public void postShutdown(IWorkbench workbench) { 
		if (this.nativeListener != null) {
			this.nativeListener.postShutdown(workbench);
		}
		if (this.genericListener != null) {
			this.genericListener.postShutdown(workbench);
		}
     }
}
