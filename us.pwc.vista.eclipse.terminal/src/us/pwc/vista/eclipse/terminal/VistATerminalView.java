package us.pwc.vista.eclipse.terminal;

import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.view.TerminalView;

@SuppressWarnings("restriction")
public class VistATerminalView extends TerminalView {	
	@Override
	public void onTerminalConnect() {
		ITerminalConnector connector = new VistATerminalConnector(); 
		if (connector != null) {
			this.fCtlTerminal.setConnector(connector);
			this.fCtlTerminal.connectTerminal();
		}
	}

	@Override
	public void onTerminalDisconnect() {
		this.fCtlTerminal.disconnectTerminal();	
	}
}
