package gov.va.mumps.debug.ui.terminal;


import java.io.IOException;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;
import org.eclipse.tm.internal.terminal.telnet.TelnetConnector;


@SuppressWarnings("restriction")
public class VistATelnetConnector extends TelnetConnector {
	private VistATelnetSettings settings = new VistATelnetSettings();
	private IVistAStreamListener listener;
	private VistAOutputStream os;
	private String namespace;
	
	public VistATelnetConnector(String namespace, IVistAStreamListener listener) {
		super(null);
		this.namespace = namespace;
		this.listener = listener;
	}
	
	@Override
	public void connect(ITerminalControl control) {
		TerminalControlWrap wrapTC = new TerminalControlWrap(this.namespace, control, this.listener);
		this.os = wrapTC.getVistAStream();
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
		this.os.setState(VistAOutputStreamState.RESUMED);
		this.sendCommandToStream("BREAK \"C\" G\n");
	}
	
	public void stepInto() {
		this.sendStepCommand("S+");
	}
	
	public void stepOver() {
		this.sendStepCommand("L");
	}
	
	public void stepReturn() {
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
 