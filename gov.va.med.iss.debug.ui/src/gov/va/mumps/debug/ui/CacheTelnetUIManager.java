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

import gov.va.mumps.debug.core.MDebugSettings;
import gov.va.mumps.debug.core.model.MCacheTelnetDebugTarget;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import us.pwc.vista.eclipse.terminal.VistATerminalView;

class CacheTelnetUIManager implements ILaunchListener {
	private Map<String, IViewPart> views = new HashMap<String, IViewPart>();
	
	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(ILaunch launch) {
		final String launchId = String.valueOf(System.identityHashCode(launch));		
		if (launch.getDebugTarget() != null) {
			final MCacheTelnetDebugTarget target = (MCacheTelnetDebugTarget) launch.getDebugTarget();
			final String namespace = MDebugSettings.getNamespace();
		
			synchronized (target) {
				final CacheTelnetUIManager thiz = this;
				Display.getDefault().syncExec(new Runnable() {						
					@Override
					public void run() {
						try {
							IWorkbench wb = PlatformUI.getWorkbench();
							IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
							IWorkbenchPage wbp = window.getActivePage();
							IViewPart vp = wbp.showView(MDebugUIPlugin.TERMINAL_VIEW_ID, launchId, IWorkbenchPage.VIEW_ACTIVATE);
							thiz.views.put(launchId, vp);
							
							((VistATerminalView) vp).connect(target, namespace); 
						} catch (Throwable t) {
						}
					}
				});
			}
		}
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		String launchId = String.valueOf(System.identityHashCode(launch));		
		final IViewPart vp = this.views.get(launchId);
		if (vp != null) {		
			Display.getDefault().syncExec(new Runnable() {						
				@Override
				public void run() {
					vp.getSite().getWorkbenchWindow().getActivePage().hideView(vp);				
				}
			});
		}
	}
}