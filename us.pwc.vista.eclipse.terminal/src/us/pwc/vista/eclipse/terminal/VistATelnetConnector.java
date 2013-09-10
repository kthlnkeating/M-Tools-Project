package us.pwc.vista.eclipse.terminal;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;
import org.eclipse.tm.internal.terminal.telnet.TelnetConnector;

@SuppressWarnings("restriction")
public class VistATelnetConnector extends TelnetConnector {
	private VistATelnetSettings settings = new VistATelnetSettings();
	
	public VistATelnetConnector() {
		super(null);
	}
	
	@Override
	public void connect(ITerminalControl control) {
		ITerminalControl wrapTC = new TerminalControlWrap(control);
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
}
 