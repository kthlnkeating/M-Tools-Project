package us.pwc.vista.eclipse.terminal;

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
	
	public VistATelnetConnector(IVistAStreamListener listener) {
		super(null);
		this.listener = listener;
	}
	
	@Override
	public void connect(ITerminalControl control) {
		TerminalControlWrap wrapTC = new TerminalControlWrap(control, this.listener);
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
	
	public void sendCommand(String command) throws IOException {
		this.os.startCommand();
		byte[] bytes = command.getBytes();
		this.getTerminalToRemoteStream().write(bytes);
	}
	
	public void debugCommand(String command) throws IOException {
		this.os.debugCommand(command);
		byte[] bytes = command.getBytes();
		this.getTerminalToRemoteStream().write(bytes);
	}
}
 