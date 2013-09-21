package gov.va.mumps.debug.ui.terminal;


import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.MDebugSettings;
import gov.va.mumps.debug.core.model.IMTerminalManager;
import gov.va.mumps.debug.core.model.MDebugPreference;

import org.eclipse.tm.internal.terminal.connector.TerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class VistATerminalConnector extends TerminalConnector {
	private static class VistATerminalConnectorFactory implements TerminalConnector.Factory {		
		private IMInterpreterConsumer consumer;
		private IMTerminalManager terminalManager;
		
		public VistATerminalConnectorFactory(IMInterpreterConsumer consumer, IMTerminalManager terminalManager) {
			this.consumer = consumer;
			this.terminalManager = terminalManager;
		}
		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			MDebugPreference preference = MDebugSettings.getDebugPreference();			
			switch (preference) {
			case CACHE_TELNET:
				return new CacheTelnetConnector(this.consumer, this.terminalManager);			
			case GTM_SSH:
				return new GTMSSHConnector(this.consumer, this.terminalManager);			
			default:
				return null;
			}		
		}
	};
	
	public VistATerminalConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager) {
		super(new VistATerminalConnectorFactory(consumer, terminalManager), "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
