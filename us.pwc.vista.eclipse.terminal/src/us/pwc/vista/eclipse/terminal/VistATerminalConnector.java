package us.pwc.vista.eclipse.terminal;

import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class VistATerminalConnector extends TerminalConnector {
	private static TerminalConnector.Factory FACTORY = new TerminalConnector.Factory() {		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			return new VistATelnetConnector();
		}
	};
	
	public VistATerminalConnector() {
		super(FACTORY, "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
