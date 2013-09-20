package gov.va.mumps.debug.ui.terminal;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminal;
import gov.va.mumps.debug.core.model.IMTerminalManager;
import gov.va.mumps.debug.ui.MDebugUIPlugin;

public class MTerminalManager implements IMTerminalManager {
	private IMTerminal showResult;
	
	public synchronized IMTerminal show(final String id, final IMInterpreterConsumer consumer) {
		Display.getDefault().syncExec(new Runnable() {						
			@Override
			public void run() {
				try {
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
					IWorkbenchPage wbp = window.getActivePage();
					IViewPart vp = wbp.showView(MDebugUIPlugin.TERMINAL_VIEW_ID, id, IWorkbenchPage.VIEW_ACTIVATE);
					MTerminalManager.this.showResult = (IMTerminal) vp;
					if (consumer != null) {
						((VistATerminalView) vp).connect(MTerminalManager.this, consumer); 
					}
				} catch (Throwable t) {
					MTerminalManager.this.showResult = (IMTerminal) null;
				}
			}
		});
		return this.showResult;
	}
	
	public IMTerminal create(String id, IMInterpreterConsumer consumer) {
		return this.show(id, consumer);
	}
		
	public void giveFocus(String id) {
		this.show(id, null);
	}

	public void close(IMTerminal terminal) {
		if (terminal instanceof IViewPart) {
			final IViewPart vp = (IViewPart) terminal;
			Display.getDefault().syncExec(new Runnable() {						
				@Override
				public void run() {
					vp.getSite().getWorkbenchWindow().getActivePage().hideView(vp);				
				}
			});				
		}
	}
}
