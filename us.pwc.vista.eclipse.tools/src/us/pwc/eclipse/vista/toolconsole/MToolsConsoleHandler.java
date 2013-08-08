package us.pwc.eclipse.vista.toolconsole;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

public class MToolsConsoleHandler {
	private static MessageConsole findConsole(IConsoleManager consoleManager, String name) {
		IConsole[] consoles = consoleManager.getConsoles();
		for (IConsole console : consoles) {
			if (name.equals(console.getName())) {
				MessageConsole messageConsole = (MessageConsole) console;
				return messageConsole;
			}
		}
		MessageConsole newConsole = new MessageConsole(name, null);
		consoleManager.addConsoles(new IConsole[] {newConsole});
		return newConsole;
	}

	public static MessageConsole getMessageConsole(String projectName) {
		String name = "M Tools Console (" +  projectName + ")";
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		MessageConsole console = findConsole(consoleManager, name);
		return console;
	}
	
	public static void displayMToolsConsole(MessageConsole console, IWorkbenchWindow window) {
		IWorkbenchPage page = window.getActivePage();
		try {
			IConsoleView view = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
			if (view != null) {
				view.display(console);
			}			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
