package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreterConsumer;

import java.io.IOException;
import java.io.OutputStream;

public class GTMSSHOutputStream extends MOutputStreamWrap {
	public GTMSSHOutputStream(String namespace, OutputStream actual, IMInterpreterConsumer listener, String encoding) {
		super(actual, OutputStreamState.NOT_CONNECTED, listener, "GTM", encoding);
	}

	@Override
	protected void writeDuringConnection(byte b) throws IOException {
		int n = this.debugCount;
		this.debugInfo[n] = b; 
		++this.debugCount;
		if (b == 36) {
			this.writeInternalStreams(this.debugInfo, this.debugCount);
			this.debugCount = 0;
			this.state = OutputStreamState.WAITING_PROMPT;
			this.interpreter.sendCommandToStream("gtm\n");
		}			
	}			

	@Override
	protected String getBreakLocationLineIdentifier() {
		return "At M source location";
	}
	
	@Override
	protected int getPatternLineToVariablesOffset() {
		return 0;
	}
	
	@Override
	protected int getVariablesToLocationLineOffset() {
		return 1;
	}
}
