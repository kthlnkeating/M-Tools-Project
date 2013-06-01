package us.pwc.eclipse.vista.toolconsole;

import org.eclipse.swt.widgets.Display;
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
		final IConsoleView container[] = new IConsoleView[1];
		
		Display.getDefault().syncExec(new Runnable() { //must get the getActiveWorkbenchWindow from the main UI thread
					public void run() {
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						try {
							container[0] = (IConsoleView) page
									.showView(IConsoleConstants.ID_CONSOLE_VIEW);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					}
				});

		return container[0];
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
