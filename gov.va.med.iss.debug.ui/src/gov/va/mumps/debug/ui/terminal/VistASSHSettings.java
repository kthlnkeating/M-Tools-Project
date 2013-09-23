package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.MDebugSettings;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.ssh.SshSettings;

@SuppressWarnings("restriction")
public class VistASSHSettings extends SshSettings {
	public String getHost() {
		return MDebugSettings.getSSHHost();
	}

	public String getUser() {
		return MDebugSettings.getSSHUser();
	}

	public String getPassword() {
		return MDebugSettings.getSSHPassword();
	}
	
	public int getTimeout() {
		return Integer.parseInt(MDebugSettings.getSSHTimeout());
	}

	public int getKeepalive() {
		return Integer.parseInt(MDebugSettings.getSSHKeepAlive());
	}

	public int getPort() {
		return Integer.parseInt(MDebugSettings.getSSHPort());
	}

	public String getSummary() {
		return "Summary";
	}

	public void load(ISettingsStore store) {		
	}

	public void save(ISettingsStore store) {		
	}
}
