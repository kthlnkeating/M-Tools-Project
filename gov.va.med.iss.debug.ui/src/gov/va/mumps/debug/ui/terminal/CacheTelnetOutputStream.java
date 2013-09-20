package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.model.MCodeLocation;
import gov.va.mumps.debug.core.model.MStackInfo;
import gov.va.mumps.debug.core.model.MVariableInfo;

import java.io.IOException;
import java.io.OutputStream;

public class CacheTelnetOutputStream extends OutputStream {
	private static String BREAK_IDENTIFIER = "<BREAK>";
	private static byte[] PATTERN = {0, 0, 0};

	private OutputStream actual;
	
	private OutputStreamState state = OutputStreamState.NOT_CONNECTED;
	
	private byte[] buffer = new byte[PATTERN.length];
	private int count;
	
	private byte[] debugInfo = new byte[10000];
	private int debugCount;
	
	private IMInterpreterConsumer listener;
	
	private byte[] namespace;
	private IMInterpreter interpreter;
	
	private final static boolean OUTDEBUG = false;
	
	public CacheTelnetOutputStream(String namespace, OutputStream actual, IMInterpreterConsumer listener) {
		this.namespace = namespace.getBytes();
		this.actual = actual;
		this.listener = listener;
	}
	
	public void setMInterpreter(IMInterpreter interpreter) {
		this.interpreter = interpreter;
	}
 	
	@Override
	public void close() throws IOException {
		if (count > 0) {
			this.actual.write(buffer, 0, count);
		}		
		this.actual.close();
	}

	@Override
	public void flush() throws IOException {		
		this.actual.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (byte bi : b) {
			this.auxWrite(bi);			
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int n = off + len;
		for (int i=off; i<n; ++i) {
			this.auxWrite(b[i]);
		}
	}

	@Override
	public void write(int b) throws IOException {
		this.auxWrite((byte) b);
	}
	
	public void setState(OutputStreamState state) {
		this.state = state;
	}
	
	private void auxWrite(byte b) throws IOException {
		if (this.state == OutputStreamState.BREAK_SEARCH) {
			this.auxWriteSearch(b);
		} else if (this.state == OutputStreamState.RESUMED) {
			int n = this.debugCount;
			this.debugInfo[n] = b;
			++this.debugCount;
			if ((b == 10) && (this.debugInfo[n-1] == 13)) {
				this.writeInternalStreams(this.debugInfo, this.debugCount);
				this.debugCount = 0;
				this.state = OutputStreamState.BREAK_SEARCH;
			}
		} else {
			this.debugInfo[this.debugCount] = b;
			++this.debugCount;			
			if (b == '>') this.auxWriteBreak(b, this.namespace);
		}
	}
	
	private void auxWriteSearch(byte b) throws IOException {
		if (PATTERN[this.count] != b) {
			this.buffer[count] = b;
			this.actual.write(this.buffer, 0, count+1);
			count = 0;				
		} else {
			this.buffer[count] = b;
			++count;
			if (PATTERN.length == count) {								
				this.state = OutputStreamState.BREAK_FOUND;
				count = 0;
			}			
		}
	}

	private void auxWriteBreak(byte b, byte[] pattern) throws IOException {
		int endIndex = this.debugCount-9;
		if (endIndex < 0) endIndex = 0;
		int n = pattern.length;
		byte epb = pattern[n-1];
		for (int i=this.debugCount-1; i>=endIndex; --i) {
			if (epb == this.debugInfo[i]) {
				for (int j=1; j<n; ++j) {
					if (i < j) return;
					if (pattern[n-j-1] != this.debugInfo[i-j]) {
						return;
					}
				}
				OutputStreamState currentState = this.state;
				this.writeInternalStreams(this.debugInfo, this.debugCount);
				int currentCount = this.debugCount;
				this.debugCount = 0;
				this.state = OutputStreamState.BREAK_SEARCH;
				if (currentState == OutputStreamState.NOT_CONNECTED) {
					this.listener.handleConnected(this.interpreter);
				}  else if (currentState == OutputStreamState.COMMAND_EXECUTE) {
					String str = new String(this.debugInfo, 0, currentCount);					
					this.listener.handleCommandExecuted(str);						
				} else if (currentState == OutputStreamState.BREAK_FOUND) {					
					String str = new String(this.debugInfo, 0, currentCount);					
					if (str.startsWith("!!")) {
						this.listener.handleEnd();
					} else {
						MStackInfo[] stackInfos = this.infoToStacksInfo(str); 
						this.listener.handleBreak(stackInfos);
					}
				}
			}			
		}
	}
	
	private void writeInternalStreams(byte[] stream, int count) throws IOException {
		if (OUTDEBUG) {
			this.actual.write(stream, 0, count);			
		}
	}

	private MStackInfo[] infoToStacksInfo(String info) {
		String[] pieces = info.split("\r\n");		
		int breakLineIndex = 0;
		for (String piece : pieces) {
			if (piece.startsWith(BREAK_IDENTIFIER)) break;
			++breakLineIndex;
		}
		if (breakLineIndex >= pieces.length) {
			throw new RuntimeException("Internal Error");
		}
		
		String entryInfo = pieces[breakLineIndex].substring("<BREAK>".length());
		MCodeLocation codeLocation = MCodeLocation.getInstance(entryInfo);

		MVariableInfo[] variableInfos = new MVariableInfo[breakLineIndex-3];
		int index = 0;
		for (int i=1; i<breakLineIndex-2; ++i) {
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
