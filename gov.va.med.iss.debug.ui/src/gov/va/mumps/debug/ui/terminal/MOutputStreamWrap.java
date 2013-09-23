package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.MCodeLocation;
import gov.va.mumps.debug.core.model.MStackInfo;
import gov.va.mumps.debug.core.model.MVariableInfo;

import java.io.IOException;
import java.io.OutputStream;

import us.pwc.vista.eclipse.core.helper.MessageConsoleHelper;

public abstract class MOutputStreamWrap extends OutputStream {
	private OutputStream actualStream;

	private static byte[] BREAK_START_PATTERN = {0, 0, 0};

	private byte[] buffer = new byte[BREAK_START_PATTERN.length];
	private int count;
		
	protected OutputStreamState state;
	
	protected byte[] debugInfo = new byte[10000];
	protected int debugCount;
	
	private byte[] namespace;

	protected IMInterpreterConsumer listener;
	protected IMInterpreter interpreter;
	protected OutputStream messageStream;
	private String encoding;
	
	protected MOutputStreamWrap(OutputStream actualStream, OutputStreamState initialState, IMInterpreterConsumer listener, String namespace, String encoding) {
		this.actualStream = actualStream;
		this.state = initialState;
		this.listener = listener;
		try {
			this.namespace = namespace.getBytes(encoding);
		} catch (IOException e) {			
		}
		this.encoding = encoding;
	}
	
	public void setState(OutputStreamState state) {
		this.state = state;
	}
	
	public void setMInterpreter(IMInterpreter interpreter) {
		this.interpreter = interpreter;
	}
 	
	public void setMessageStream(OutputStream messageStream) {
		this.messageStream = messageStream;
	}
 	
	@Override
	public void close() throws IOException {
		this.actualStream.close();
	}

	@Override
	public void flush() throws IOException {		
		this.actualStream.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (byte bi : b) {
			this.filteredWrite(bi);			
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int n = off + len;
		for (int i=off; i<n; ++i) {
			this.filteredWrite(b[i]);
		}
	}

	@Override
	public void write(int b) throws IOException {
		this.filteredWrite((byte) b);
	}
	
	protected synchronized void filteredWrite(byte b) throws IOException {
		switch (this.state) {
		case NOT_CONNECTED: 
			this.writeDuringConnection(b);
			break;
		case BREAK_SEARCH:
			this.writeSearchingBreakPattern(b);
			break;
		case RESUMED:
			this.writeAfterResumed(b);
			break;
		default:
			this.writeSearchingPrompt(b);
			break;
		}
	}		
	
	protected abstract void writeDuringConnection(byte b) throws IOException;
	
	protected void writeAfterResumed(byte b) throws IOException {
		int n = this.debugCount;
		this.debugInfo[n] = b;
		++this.debugCount;
		if ((b == 10) && (this.debugInfo[n-1] == 13)) {
			this.writeInternalStreams(this.debugInfo, this.debugCount);
			this.debugCount = 0;
			this.state = OutputStreamState.BREAK_SEARCH;
		}		
	}
	
	protected void writeSearchingBreakPattern(byte b) throws IOException {
		if (BREAK_START_PATTERN[this.count] != b) {
			this.buffer[count] = b;
			this.actualWrite(this.buffer, 0, count+1);
			this.count = 0;				
		} else {
			this.buffer[this.count] = b;
			++count;
			if (BREAK_START_PATTERN.length == this.count) {								
				this.state = OutputStreamState.BREAK_FOUND;
				this.count = 0;
			}			
		}
	}
	
	protected void writeSearchingPrompt(byte b) throws IOException {
		this.debugInfo[this.debugCount] = b;
		++this.debugCount;			
		if (b == '>') {
			if (this.verifyPompt()) {
				this.transitionAfterPromptFound();
			}
		}
	}
 	
	public void actualWrite(byte[] b, int off, int len) throws IOException {
		this.actualStream.write(b, off, len);
	}
	
	private boolean verifyPompt() throws IOException {
		int endIndex = this.debugCount-9;
		if (endIndex < 0) endIndex = 0;
		int n = this.namespace.length;
		byte epb = this.namespace[n-1];
		for (int i=this.debugCount-1; i>=endIndex; --i) {
			if (epb == this.debugInfo[i]) {
				for (int j=1; j<n; ++j) {
					if (i < j) {
						return false;
					}
					if (this.namespace[n-j-1] != this.debugInfo[i-j]) {
						return false;
					}
				}
				return true;
			}			
		}
		return false;
	}
	
	protected void transitionAfterPromptFound() throws IOException {
		switch (this.state) {
		case WAITING_PROMPT:
			this.handleFirstPromptFound();			
			break;
		case COMMAND_EXECUTE:
			this.handleDebugCommandExecuted();
			break;
		case BREAK_FOUND:
			this.handleBreakFound();
			break;
		default:
			break;
		}
	}
	
	private void resetBreakInfoStream() throws IOException {
		this.writeInternalStreams(this.debugInfo, this.debugCount);
		this.debugCount = 0;
		this.state = OutputStreamState.BREAK_SEARCH;		
	}
	
	private void handleFirstPromptFound() throws IOException {
		this.resetBreakInfoStream();
		this.listener.handleConnected(this.interpreter);		
	}
	
	private void handleDebugCommandExecuted() throws IOException {
		String str = new String(this.debugInfo, 0, this.debugCount, this.encoding);					
		this.resetBreakInfoStream();
		this.listener.handleCommandExecuted(str);								
	}
	
	private void handleBreakFound() throws IOException {
		String str = new String(this.debugInfo, 0, this.debugCount, this.encoding);					
		this.resetBreakInfoStream();
		if (str.startsWith("!!")) {
			this.listener.handleEnd();
		} else {
			MStackInfo[] stackInfos = this.infoToStacksInfo(str); 
			this.listener.handleBreak(stackInfos);
		}		
	}
		
	protected abstract String getBreakLocationLineIdentifier();
	
	protected abstract int getPatternLineToVariablesOffset();

	protected abstract int getVariablesToLocationLineOffset();
	
	protected MStackInfo[] infoToStacksInfo(String info) {
		String identifier = this.getBreakLocationLineIdentifier();

		String[] pieces = info.split("\r\n");		
		int breakLineIndex = 0;
		for (String piece : pieces) {
			if (piece.indexOf(identifier) >= 0) break;
			++breakLineIndex;
		}
		if (breakLineIndex >= pieces.length) {
			throw new RuntimeException("Internal Error");
		}
		
		String entryInfo = pieces[breakLineIndex].split(identifier)[1].trim();
		MCodeLocation codeLocation = MCodeLocation.getInstance(entryInfo);

		int start = this.getPatternLineToVariablesOffset();
		int endOffset = this.getVariablesToLocationLineOffset();
		
		return this.getStackInfo(codeLocation, pieces, start, breakLineIndex-endOffset);
	}
	
	protected void writeInternalStreams(byte[] stream, int count) throws IOException {
		String text = new String(stream, 0, count, this.encoding);
		Emulator emulator = new Emulator();
		String controlFreeText = emulator.processText(text);		
		MessageConsoleHelper.writeToConsole("M Debug", controlFreeText, false, false);
	}
	
	protected MStackInfo[] getStackInfo(MCodeLocation codeLocation, String[] pieces, int start, int end) {
		int length = end - start;
		MVariableInfo[] variableInfos = new MVariableInfo[length];
		int index = 0;
		for (int i=start; i<end; ++i) {
			String nv = pieces[i];
			int equalLocation = nv.indexOf('=');
			if (equalLocation >= 0) {
				String name = nv.substring(0, equalLocation);
				String value = nv.substring(equalLocation+1);
				variableInfos[index] = new MVariableInfo(name, value);
				++index;
			}				
		}
		MStackInfo[] stacks = new MStackInfo[1];
		stacks[0] = new MStackInfo(codeLocation, variableInfos);
		return stacks;		
	}
}
