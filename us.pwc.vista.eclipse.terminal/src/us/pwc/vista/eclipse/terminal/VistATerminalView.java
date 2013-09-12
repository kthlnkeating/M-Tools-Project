package us.pwc.vista.eclipse.terminal;

import java.io.IOException;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;

import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.view.TerminalView;

@SuppressWarnings("restriction")
public class VistATerminalView extends TerminalView implements IMInterpreter, IVistAStreamListener {
	private IMInterpreterConsumer consumer;
	private VistATelnetConnector connector;
	
	@Override
	public void onTerminalConnect() {
		ITerminalConnector connector = new VistATerminalConnector(this); 
		if (connector != null) {
			this.fCtlTerminal.setConnector(connector);
			this.fCtlTerminal.connectTerminal();
		}
	}

	@Override
	public void onTerminalDisconnect() {
		this.fCtlTerminal.disconnectTerminal();	
	}
	
	@Override
	public void connect(IMInterpreterConsumer consumer) {
		ITerminalConnector connector = new VistATerminalConnector(this); 
		if (connector != null) {
			this.fCtlTerminal.setConnector(connector);
			this.fCtlTerminal.connectTerminal();
			this.consumer = consumer;
		}
	}
	
	@Override
	public void disconnect() {
		this.fCtlTerminal.disconnectTerminal();			
	}
	
	@Override
	public void terminate() {
	}
	
	@Override
	public void handleConnected() {
		this.consumer.handleConnected(this);
	}

	@Override
	public void handleCommandExecuteEnded() {
		this.consumer.handleCommandExecuted();
	}

	@Override
	public void handleBreak(String info) {
		this.consumer.handleBreak(info);
	}

	@Override
	public void handleConnectorCreated(VistATelnetConnector connector) {
		this.connector = connector;
	}

	@Override
	public void sendCommand(String command) throws IOException {
		this.connector.sendCommand(command);
	}

	@Override
	public void debugCommand(String command) throws IOException {
		this.connector.debugCommand(command);
	}
}
