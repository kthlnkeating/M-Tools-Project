package us.pwc.eclipse.vista.toolconsole;

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