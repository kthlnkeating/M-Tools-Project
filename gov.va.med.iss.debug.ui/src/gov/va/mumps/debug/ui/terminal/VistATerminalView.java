package gov.va.mumps.debug.ui.terminal;

import java.io.OutputStream;

import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminal;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.view.TerminalView;

@SuppressWarnings("restriction")
public class VistATerminalView extends TerminalView implements IMTerminal {
	@Override
	public void connect(IMTerminalManager terminalManager, IMInterpreterConsumer consumer, OutputStream messageStream) {
		String encoding = this.fCtlTerminal.getEncoding();
		ITerminalConnector connector = new VistATerminalConnector(consumer, terminalManager, messageStream, encoding); 
		if (connector != null) {
			this.fCtlTerminal.setConnector(connector);
			this.fCtlTerminal.connectTerminal();
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
