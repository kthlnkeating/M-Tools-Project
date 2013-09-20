package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import java.io.IOException;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;
import org.eclipse.tm.internal.terminal.telnet.TelnetConnector;

@SuppressWarnings("restriction")
public class VistATelnetConnector extends TelnetConnector implements IMInterpreter {
	private VistATelnetSettings settings = new VistATelnetSettings();
	private VistAOutputStream os;
	private IMTerminalManager terminalManager;
	private IMInterpreterConsumer consumer;
	
	public VistATelnetConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager) {
		super(null);
		this.consumer = consumer;
		this.terminalManager = terminalManager;
	}
	
	@Override
	public void connect(ITerminalControl control) {
		String namespace = this.consumer.getPrompt();
		TerminalControlWrap wrapTC = new TerminalControlWrap(namespace, control, this.consumer);
		this.os = wrapTC.getVistAStream();
		this.os.setMInterpreter(this);
		super.connect(wrapTC);
	}

	@Override
	public ITelnetSettings getTelnetSettings() {
		return settings;
	}
	
	@Override
	public ISettingsPage makeSettingsPage() {
		return null;
	}
	
	@Override
	public String getSettingsSummary() {
		return settings.getSummary();
	}
	
	@Override
	public void load(ISettingsStore store) {
		settings.load(store);
	}
	
	@Override
	public void save(ISettingsStore store) {
		settings.save(store);
	}
	
	public void sendInfoCommand(String command) {
		this.os.setState(VistAOutputStreamState.COMMAND_EXECUTE);
		this.sendCommandToStream(command);
	}
	
	public void sendRunCommand(String command) {
		command = command + " W $C(0,0,0),\"!!\"\n"; 
		this.os.setState(VistAOutputStreamState.RESUMED);
		this.sendCommandToStream(command);
	}
	
	public void resume() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.os.setState(VistAOutputStreamState.RESUMED);
		this.sendCommandToStream("BREAK \"C\" G\n");
	}
	
	public void stepInto() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("S+");
	}
	
	public void stepOver() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("L");
	}
	
	public void stepReturn() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("L-");
	}
	
	private void sendStepCommand(String type) {
		String cmd = "BREAK \"" + type + "\" ZBREAK $:\"B\":\"1\":\"W $C(0,0,0) ZWRITE\" G\n";
		this.os.setState(VistAOutputStreamState.RESUMED);
		this.sendCommandToStream(cmd);		
	}
		
	private void sendCommandToStream(String command) {
		byte[] bytes = command.getBytes();
		try {
			this.getTerminalToRemoteStream().write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Error", e);
		}		
	}
}
 