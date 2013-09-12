package us.pwc.vista.eclipse.terminal;

import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class VistATerminalConnector extends TerminalConnector {
	private static class VistATerminalConnectorFactory implements TerminalConnector.Factory {		
		private IVistAStreamListener listener;
		
		public VistATerminalConnectorFactory(IVistAStreamListener listener) {
			this.listener = listener;
		}
		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			VistATelnetConnector connector = new VistATelnetConnector(this.listener);
			this.listener.handleConnectorCreated(connector);
			return connector;
		}
	};
	
	public VistATerminalConnector(IVistAStreamListener listener) {
		super(new VistATerminalConnectorFactory(listener), "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
