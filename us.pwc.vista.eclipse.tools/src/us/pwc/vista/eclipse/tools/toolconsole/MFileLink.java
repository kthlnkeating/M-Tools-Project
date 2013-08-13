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

package us.pwc.vista.eclipse.tools.toolconsole;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IDE;

public class MFileLink implements IHyperlink {
	private IWorkbenchWindow window;
	private IFile file;
	private int lineNumber;
 	
	public MFileLink(IWorkbenchWindow window, IFile file, int lineNumber) {
		this.window = window;
		this.file = file;
		this.lineNumber = lineNumber;
 	}
	
	@Override
 	public void linkActivated() {
		if (this.window != null) {
			IWorkbenchPage page = this.window.getActivePage();
 			if (page != null) {
 				try {
 					IMarker marker = this.file.createMarker(IMarker.TEXT);
 					marker.setAttribute(IMarker.LINE_NUMBER, this.lineNumber);
 					IDE.openEditor(page, marker);
 					marker.delete();
 				} catch (CoreException ce) { 					
 				}
			}
		}
	}

 	public void linkEntered() {
	}

	public void linkExited() {
	}
}