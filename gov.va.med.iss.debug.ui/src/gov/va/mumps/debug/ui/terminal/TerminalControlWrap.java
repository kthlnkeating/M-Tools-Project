package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;

@SuppressWarnings("restriction")
public class TerminalControlWrap implements ITerminalControl {
	private ITerminalControl actual;
	private VistAOutputStream os;
	
	public TerminalControlWrap(String namespace, ITerminalControl actual, IMInterpreterConsumer listener) {
		this.actual = actual;
		this.os = new VistAOutputStream(namespace, actual.getRemoteToTerminalOutputStream(), listener);
	}
	
	public VistAOutputStream getVistAStream() {
		return this.os;
	}
	
	@Override
	public TerminalState getState() {
		return this.actual.getState();
	}

	@Override
	public void setState(TerminalState state) {
		this.actual.setState(state);
	}

	@Override
	public Shell getShell() {
		return this.actual.getShell();
	}

	@Override
	public void setEncoding(String encoding) throws UnsupportedEncodingException {
		this.actual.setEncoding(encoding);
	}
	
	@Override
	public String getEncoding() {
		return this.actual.getEncoding();
	}

	@Override
	public void displayTextInTerminal(String text) {
		this.actual.displayTextInTerminal(text);
	}

	@Override
	public OutputStream getRemoteToTerminalOutputStream() {
		return this.os;
	}

	@Override
	public void setTerminalTitle(String title) {
		this.actual.setTerminalTitle(title);
	}

	@Override
	public void setMsg(String msg) {
		this.actual.setMsg(msg);
	}
}
