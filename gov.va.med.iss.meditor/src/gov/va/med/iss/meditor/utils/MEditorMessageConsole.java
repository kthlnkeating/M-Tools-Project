/*
 * Created on Aug 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorMessageConsole {
	
	public static void writeToConsole(String text) throws Exception {
		MessageConsole msgConsole;
		try {
			msgConsole = findConsole("MEditorConsole");
		} catch (Exception e) {
			throw new Exception(e.getMessage() + ": couldn't findConsole");
		}
		try {
			msgConsole.getDocument().replace(0, msgConsole.getDocument().getLength(),"");
		} catch (Exception e) {
			throw new Exception(e.getMessage()+": Error getting console");
		}
		MessageConsoleStream out = msgConsole.newMessageStream();
		out.print(text);
		
		IConsole msgIConsole = (IConsole) msgConsole;
		IWorkbenchPage page = MEditorUtilities.getIWorkbenchPage();
		String id = IConsoleConstants.ID_CONSOLE_VIEW;
		try {
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(msgIConsole);
		} catch (Exception e) {
			throw new Exception (e.getMessage() + ": can't show view for IConsoleView");
		}
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
