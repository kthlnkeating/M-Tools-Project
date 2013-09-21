package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import java.io.IOException;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.ssh.SshConnector;

@SuppressWarnings("restriction")
public class GTMSSHConnector extends SshConnector implements IMInterpreter {
	private VistASSHSettings settings = new VistASSHSettings();
	private GTMSSHOutputStream os;
	private IMTerminalManager terminalManager;
	private IMInterpreterConsumer consumer;
	
	public GTMSSHConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager) {
		super(new VistASSHSettings());
		this.consumer = consumer;
		this.terminalManager = terminalManager;
	}
	
	@Override
	public void connect(ITerminalControl control) {
		String namespace = this.consumer.getPrompt();
		TerminalControlWrap wrapTC = new TerminalControlWrap(namespace, control, this.consumer);
		this.os = (GTMSSHOutputStream) wrapTC.getVistAStream();
		this.os.setMInterpreter(this);
		super.connect(wrapTC);
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
	
	@Override
	public void sendInfoCommand(String command) {
		this.os.setState(OutputStreamState.COMMAND_EXECUTE);
		this.sendCommandToStream(command);
	}
	
	@Override
	public void sendRunCommand(String command) {
		command = command + " W $C(0,0,0),\"!!\"\n"; 
		this.os.setState(OutputStreamState.RESUMED);
		this.sendCommandToStream(command);
	}
	
	@Override
	public void resume() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.os.setState(OutputStreamState.RESUMED);
		this.sendCommandToStream("ZCONTINUE\n");
	}
	
	@Override
	public void stepInto() {
		String cmd = "ZSTEP INTO:\"W $C(0,0,0) ZWRITE  BREAK\"\n";
		this.sendStepCommand(cmd);
	}
	
	@Override
	public void stepOver() {
		String cmd = "ZSTEP OV:\"W $C(0,0,0) ZWRITE  BREAK\"\n";
		this.sendStepCommand(cmd);
	}
	
	@Override
	public void stepReturn() {
		String cmd = "ZSTEP OU:\"W $C(0,0,0) ZWRITE  BREAK\"\n";
		this.sendStepCommand(cmd);
	}
	
	public String getLocationBreakCommand(String codeLocation) {
		return "ZBREAK " + codeLocation + ":\"W $C(0,0,0) ZWRITE  BREAK\"";
	}

	public String getVariableBreakCommand(String variable) {
		return "";
	}

	private void sendStepCommand(String cmd) {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.os.setState(OutputStreamState.RESUMED);
		this.sendCommandToStream(cmd);		
	}
		
	@Override
	public void sendCommandToStream(String command) {
		byte[] bytes = command.getBytes();
		try {
			this.getTerminalToRemoteStream().write(bytes);
			this.getTerminalToRemoteStream().flush();
		} catch (IOException e) {
			throw new RuntimeException("Error", e);
		}		
	}
}
