package gov.va.mumps.debug.ui.terminal;


import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class VistATerminalConnector extends TerminalConnector {
	private static class VistATerminalConnectorFactory implements TerminalConnector.Factory {		
		private IVistAStreamListener listener;
		private IMInterpreterConsumer consumer;
		private IMTerminalManager terminalManager;
		
		public VistATerminalConnectorFactory(IMInterpreterConsumer consumer, IMTerminalManager terminalManager, IVistAStreamListener listener) {
			this.listener = listener;
			this.consumer = consumer;
			this.terminalManager = terminalManager;
		}
		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			VistATelnetConnector connector = new VistATelnetConnector(this.consumer, this.terminalManager, this.listener);
			this.listener.handleConnectorCreated(connector);
			return connector;
		}
	};
	
	public VistATerminalConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager, IVistAStreamListener listener) {
		super(new VistATerminalConnectorFactory(consumer, terminalManager, listener), "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
