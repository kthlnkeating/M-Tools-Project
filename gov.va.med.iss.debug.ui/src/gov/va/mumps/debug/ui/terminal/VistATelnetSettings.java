package gov.va.mumps.debug.ui.terminal;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;

@SuppressWarnings("restriction")
public class VistATelnetSettings implements ITelnetSettings {
	@Override
	public String getHost() {
		return "localhost";
	}
	
	@Override
	public int getNetworkPort() {
		return 23;
	}
	
	@Override
	public int getTimeout() {
		return 10;
	}

	@Override
	public String getSummary() {
		return "summary";
	}
	
	@Override
	public void load(ISettingsStore store) {		
	}

	@Override
	public void save(ISettingsStore store) {		
	}
}
