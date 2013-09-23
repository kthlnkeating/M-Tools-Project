package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.IMTerminalManager;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.telnet.ITelnetSettings;
import org.eclipse.tm.internal.terminal.telnet.TelnetConnector;

@SuppressWarnings("restriction")
public class CacheTelnetConnector extends TelnetConnector implements IMInterpreter {
	private VistATelnetSettings settings = new VistATelnetSettings();
	private CacheTelnetOutputStream os;
	private IMTerminalManager terminalManager;
	private IMInterpreterConsumer consumer;
	private OutputStream messageStream;
	private String encoding;	
	
	public CacheTelnetConnector(IMInterpreterConsumer consumer, IMTerminalManager terminalManager, OutputStream messageStream, String encoding) {
		super(null);
		this.consumer = consumer;
		this.terminalManager = terminalManager;
		this.messageStream = messageStream;
		this.encoding = encoding;
	}
	
	@Override
	public void connect(ITerminalControl control) {
		String namespace = this.consumer.getPrompt();
		TerminalControlWrap wrapTC = new TerminalControlWrap(namespace, control, this.consumer);
		this.os = (CacheTelnetOutputStream) wrapTC.getVistAStream();
		this.os.setMInterpreter(this);
		this.os.setMessageStream(this.messageStream);
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
	public void focus() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
	}
		
	@Override
	public void resume() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.os.setState(OutputStreamState.RESUMED);
		this.sendCommandToStream("BREAK \"C\" G\n");
	}
	
	@Override
	public void terminate() {
		this.terminalManager.disconnect(this.consumer.getLaunchId());
	}
	
	@Override
	public void stepInto() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("S+");
	}
	
	@Override
	public void stepOver() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("L");
	}
	
	@Override
	public void stepReturn() {
		this.terminalManager.giveFocus(this.consumer.getLaunchId());
		this.sendStepCommand("L-");
	}
	
	@Override
	public String getClearBreakCommand() {
		return "ZBREAK /CLEAR";		
	}
	
	@Override
	public String getLocationBreakCommand(String codeLocation) {
		return "ZBREAK " + codeLocation + ":\"B\":\"1\":\"W $C(0,0,0) ZWRITE\"";
	}

	@Override
	public String getVariableBreakCommand(String variable) {
		return "ZBREAK *" + variable + ":\"B\":\"1\":\"W $C(0,0,0) ZWRITE\"";
	}

	private void sendStepCommand(String type) {
		String cmd = "BREAK \"" + type + "\" ZBREAK $:\"B\":\"1\":\"W $C(0,0,0) ZWRITE\" G\n";
		this.os.setState(OutputStreamState.RESUMED);
		this.sendCommandToStream(cmd);		
	}
		
	@Override
	public void sendCommandToStream(String command) {
		try {
			byte[] bytes = command.getBytes(this.encoding);
			this.getTerminalToRemoteStream().write(bytes);
		} catch (IOException e) {
			throw new RuntimeException("Error", e);
		} 
	}
}
 