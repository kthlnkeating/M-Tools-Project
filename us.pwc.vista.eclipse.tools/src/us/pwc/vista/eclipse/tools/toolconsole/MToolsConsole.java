package us.pwc.vista.eclipse.tools.toolconsole;

import org.eclipse.ui.console.MessageConsole;

public class MToolsConsole extends MessageConsole {
	public MToolsConsole() {
		super("M Tools Console", null);
	}
	
	@Override
	public MToolsConsoleStream newMessageStream() {
		this.clearConsole();
		return new MToolsConsoleStream(this);
	}
}
