package com.raygroupintl.eclipse.vista.toolconsole;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

public class MToolsConsoleHandler {
	private static MessageConsole console;

	private static IConsoleView getConsoleView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IConsoleView consoleView = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			return consoleView;
		} catch (PartInitException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static MessageConsole getMessageConsole() {
		if (console == null) {
			console = new MessageConsole("M Tools Console", null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
		return console;
	}
	
	public static void displayMToolsConsole() {
		IConsoleView view = getConsoleView();
		if (view != null) {
			view.display(console);
		}
	}
}
