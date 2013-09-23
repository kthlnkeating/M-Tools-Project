package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

import java.io.IOException;
import java.io.OutputStream;

public class CacheTelnetOutputStream extends MOutputStreamWrap {
	public CacheTelnetOutputStream(String namespace, OutputStream actual, IMInterpreterConsumer listener, String encoding) {
		super(actual, OutputStreamState.WAITING_PROMPT, listener, namespace, encoding);
	}
	
	@Override
	protected void writeDuringConnection(byte b) throws IOException {		
	}

	@Override
	protected String getBreakLocationLineIdentifier() {
		return "<BREAK>";
	}
	
	@Override
	protected int getPatternLineToVariablesOffset() {
		return 1;
	}
	
	@Override
	protected int getVariablesToLocationLineOffset() {
		return 2;
	}
}
