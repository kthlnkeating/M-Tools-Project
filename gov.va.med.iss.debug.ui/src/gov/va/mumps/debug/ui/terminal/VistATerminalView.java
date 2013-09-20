package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminal;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.view.TerminalView;

@SuppressWarnings("restriction")
public class VistATerminalView extends TerminalView implements IMInterpreter, IVistAStreamListener, IMTerminal {
	private IMInterpreterConsumer consumer;
	private VistATelnetConnector connector;
	
	@Override
	public void connect(IMTerminalManager terminalManager, IMInterpreterConsumer consumer) {
		ITerminalConnector connector = new VistATerminalConnector(consumer, terminalManager, this); 
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
	public String getId() {
		return this.getViewSite().getSecondaryId();
	}
	
	@Override
	public void terminate() {
	}
	
	@Override
	public void handleConnected() {
		this.consumer.handleConnected(this);
	}

	@Override
	public void handleCommandExecuteEnded(String info) {
		this.consumer.handleCommandExecuted(info);
	}

	@Override
	public void handleBreak(String info) {
		this.consumer.handleBreak(info);
	}

	@Override
	public void handleEnd() {
		this.consumer.handleEnd();
	}

	@Override
	public void handleConnectorCreated(VistATelnetConnector connector) {
		this.connector = connector;
	}

	@Override
	public void sendInfoCommand(String command) {
		this.connector.sendInfoCommand(command);
	}

	@Override
	public void sendRunCommand(String command) {
		this.connector.sendRunCommand(command);
	}
	
	@Override
	public void resume() {
		this.connector.resume();		
	}
	
	@Override
	public void stepInto() {
		this.connector.stepInto();		
	}
	
	@Override
	public void stepOver() {
		this.connector.stepOver();		
	}
	
	@Override
	public void stepReturn() {
		this.connector.stepReturn();		
	}

	// Remove interactive components of Eclipse terminal
	
	@Override
	protected void setupActions() {
	}

	@Override
	protected void setupLocalToolBars() {
	}

	@Override
	protected void setupContextMenus(Control ctlText) {
	}

	@Override
	protected void loadContextMenus(IMenuManager menuMgr) {
	}
	
	@Override
	public void updateStatus() {
	}
		
	@Override
	public void updateTerminalConnect() {
	}

	@Override
	public void updateTerminalDisconnect() {		
	}
}
