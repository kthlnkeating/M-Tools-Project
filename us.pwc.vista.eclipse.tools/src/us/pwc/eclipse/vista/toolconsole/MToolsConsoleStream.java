package us.pwc.eclipse.vista.toolconsole;

import java.io.IOException;

import org.eclipse.ui.console.MessageConsoleStream;

public class MToolsConsoleStream extends MessageConsoleStream {
	public MToolsConsoleStream(MToolsConsole console) {
		super(console);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
	}
}
