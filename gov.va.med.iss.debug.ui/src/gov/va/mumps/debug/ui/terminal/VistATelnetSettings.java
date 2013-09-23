package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.MDebugSettings;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;

@SuppressWarnings("restriction")
public class VistATelnetSettings implements ITelnetSettings {
	@Override
	public String getHost() {
		return MDebugSettings.getTelnetHost();
	}
	
	@Override
	public int getNetworkPort() {
		return Integer.parseInt(MDebugSettings.getTelnetPort());
	}
	
	@Override
	public int getTimeout() {
		return Integer.parseInt(MDebugSettings.getTelnetTimeout());
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
