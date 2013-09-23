package gov.va.mumps.debug.ui.terminal;

import java.io.OutputStream;

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
		private OutputStream messageStream;
		private String encoding;
		
		public VistATerminalConnectorFactory(IMInterpreterConsumer consumer, IMTerminalManager terminalManager, OutputStream messageStream, String encoding) {
			this.consumer = consumer;
			this.terminalManager = terminalManager;
			this.messageStream = messageStream;
			this.encoding = encoding;
		}
		
		@Override
		public TerminalConnectorImpl makeConnector() throws Exception {
			MDebugPreference preference = MDebugSettings.getDebugPreference();			
			switch (preference) {
			case CACHE_TELNET:
				return new CacheTelnetConnector(this.consumer, this.terminalManager, this.messageStream, this.encoding);			
			case GTM_SSH:
				return new GTMSSHConnector(this.consumer, this.terminalManager, this.messageStream, this.encoding);			
			default:
				return null;
			}		
		}
	};
	
	public VistATerminalConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager, OutputStream messageStream, String encoding) {
		super(new VistATerminalConnectorFactory(consumer, terminalManager, messageStream, encoding), "us.pwc.vista.eclipse.terminal.VistATerminalConnector", "VistA Telnet", false);
	}	
}
