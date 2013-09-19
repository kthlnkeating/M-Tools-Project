package gov.va.mumps.debug.ui.terminal;


import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class VistATerminalConnector extends TerminalConnector {
	private static class VistATerminalConnectorFactory implements TerminalConnector.Factory {		
		private IVistAStreamListener listener;
		private String namespace;
		
		public VistATerminalConnectorFactory(String namespace, IVistAStreamListener listener) {
			this.listener = listener;
			this.namespace = namespace;
		}
		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			VistATelnetConnector connector = new VistATelnetConnector(this.namespace, this.listener);
			this.listener.handleConnectorCreated(connector);
			return connector;
		}
	};
	
	public VistATerminalConnector(String namespace, IVistAStreamListener listener) {
		super(new VistATerminalConnectorFactory(namespace, listener), "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
