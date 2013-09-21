package gov.va.mumps.debug.ui.terminal;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.ssh.SshSettings;

@SuppressWarnings("restriction")
public class VistASSHSettings extends SshSettings {
	public String getHost() {
		return "127.0.0.1";
	}

	public String getUser() {
		return "softhat";
	}

	public String getPassword() {
		return "softhat";
	}
	
	public int getTimeout() {
		return 0;
	}

	public int getKeepalive() {
		return 300;
	}

	public int getPort() {
		return 3022;
	}

	public String getSummary() {
		return "Summary";
	}

	public void load(ISettingsStore store) {		
	}

	public void save(ISettingsStore store) {		
	}
}
