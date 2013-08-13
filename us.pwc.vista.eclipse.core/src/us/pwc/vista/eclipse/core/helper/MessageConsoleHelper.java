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

package us.pwc.vista.eclipse.core.helper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.Messages;
import us.pwc.vista.eclipse.core.VistACorePlugin;

public class MessageConsoleHelper {
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

	public static void writeToConsole(String name, String text, boolean clear) {
		try {
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager consoleManager = plugin.getConsoleManager();
			MessageConsole console = findConsole(consoleManager, name);
			if (clear) { 
				// workaround to console.clearConsole() which is buggy.  Clears after the output below is done.
				IDocument document = console.getDocument();
				document.set("");
			}
			MessageConsoleStream out = console.newMessageStream();
			out.print(text);
			out.flush();
			out.close();
			consoleManager.showConsoleView(console);
		} catch (Throwable t) {
			String message = Messages.bind(Messages.MCH_WRITE_ERROR, t.getMessage());
			IStatus status = new Status(IStatus.ERROR, VistACorePlugin.PLUGIN_ID, message, t);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
		}		
	}
	
	public static MessageConsole getMessageConsole(String consoleName) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		MessageConsole console = findConsole(consoleManager, consoleName);
		return console;
	}
}
