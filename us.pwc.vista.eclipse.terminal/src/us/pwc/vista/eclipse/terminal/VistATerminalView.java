package us.pwc.vista.eclipse.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.view.TerminalView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class VistATerminalView extends TerminalView implements IMInterpreter, IVistAStreamListener {
	private IMInterpreterConsumer consumer;
	private VistATelnetConnector connector;
	
	@Override
	public void connect(IMInterpreterConsumer consumer, String namespace) {
		ITerminalConnector connector = new VistATerminalConnector(namespace, this); 
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
		this.giveFocusToTerminal();		
		this.connector.resume();		
	}
	
	@Override
	public void stepInto() {
		this.giveFocusToTerminal();		
		this.connector.stepInto();		
	}
	
	@Override
	public void stepOver() {
		this.giveFocusToTerminal();		
		this.connector.stepOver();		
	}
	
	@Override
	public void stepReturn() {
		this.giveFocusToTerminal();		
		this.connector.stepReturn();		
	}
	
	private void giveFocusToTerminal() {
		Display.getDefault().syncExec(new Runnable() {						
			@Override
			public void run() {
				try {
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
					IWorkbenchPage wbp = window.getActivePage();
					try {
						wbp.showView("us.pwc.vista.eclipse.terminal.VistATerminalView");
					} catch (Throwable t) {
					}
				} catch (Throwable t) {
				}
			}
		});		
	}
}
