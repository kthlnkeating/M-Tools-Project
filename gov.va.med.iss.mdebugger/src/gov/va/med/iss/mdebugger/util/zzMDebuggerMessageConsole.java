package gov.va.med.iss.mdebugger.util;
//  Created 070301

import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.graphics.Color;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class zzMDebuggerMessageConsole {
	
	public static void writeToConsole(String text) {
		MessageConsole msgConsole = findConsole("MEditorConsole");
		MessageConsoleStream out = msgConsole.newMessageStream();
		out.print(text);
		
		IConsole msgIConsole = (IConsole) msgConsole;
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		try {
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(msgIConsole);
		} catch (Exception e) {
			
		}
	}
	
	public static String readFromConsole() {
		MessageConsole msgConsole = findConsole("MEditorConsole");
		IOConsoleInputStream instream = msgConsole.getInputStream();
		String str = "";
		int num = 0;
		byte[] b;
		b = new byte[255];
		try {
			num = instream.read(b,0,255);
		} catch (Exception e) {
			
		}
		for (int i=0; i<num; i++) {
			str = str + b[i];
		}
		return str;
	}
	
	public static void clearConsole() {
		MessageConsole msgConsole = findConsole("MEditorConsole");
		msgConsole.clearConsole();
	}
	
	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i=0; i<existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] {myConsole});
		return myConsole;
	}

}
