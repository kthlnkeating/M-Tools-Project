package gov.va.mumps.debug.ui.console;

import gov.va.mumps.launching.InputReadyListener;
import gov.va.mumps.launching.ReadCommandListener;
import gov.va.mumps.launching.WriteCommandListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

public class MDevConsole extends AbstractConsole implements ReadCommandListener, WriteCommandListener {
	
	private boolean readingUserInput;
	private int maxCharInput;
	private MDevConsolePage pageBookView;
	private List<InputReadyListener> inputReadyListeners;
	private String missedText = "";

	public MDevConsole(String name, String consoleType,
			ImageDescriptor imageDescriptor, boolean autoLifecycle) {
		super(name, consoleType, imageDescriptor, autoLifecycle);
		
		inputReadyListeners = new LinkedList<InputReadyListener>();
	}

	@Override
	public IPageBookViewPage createPage(IConsoleView view) {
		pageBookView = new MDevConsolePage(this);
		return pageBookView;
	}

	public boolean isReadingUserInput() {
		return readingUserInput;
	}
	
	public void setReadingInput(boolean readingUserInput) {
		this.readingUserInput = readingUserInput ;
	}
	
	public int getMaxCharInput() {
		return maxCharInput;
	}

	@Override
	public void handleWriteCommand(final String output) {
		Display.getDefault().asyncExec(new Runnable() { //Only the async thread can access SWT controls
			
			@Override
			public void run() {
				if (pageBookView != null) {
					if (missedText == null) {
						pageBookView.appendText(output);
					} else {
						pageBookView.appendText(missedText+output);
						missedText = null;
					}
				} else {
					missedText += output;
				}
				setConsoleViewFocus();
			}
		});
	}

	@Override
	public void handleReadCommand(int maxCharInput) {
		this.maxCharInput = maxCharInput;
		readingUserInput = true;
		pageBookView.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				setConsoleViewFocus();
			}
		});
	}
	
	private void setConsoleViewFocus() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.console.ConsoleView");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		if (pageBookView != null)
			pageBookView.setFocus();
	}
	
	public void addInputReadyListener(InputReadyListener listener) {
		inputReadyListeners.add(listener);
	}
	
	public void removeInputReadyListener(InputReadyListener listener) {
		inputReadyListeners.remove(listener);
	}
	
	public Iterator<InputReadyListener> getInputReadyInputListeners() {
		return inputReadyListeners.listIterator();
	}

}
