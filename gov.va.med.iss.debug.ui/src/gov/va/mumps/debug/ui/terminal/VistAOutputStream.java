package gov.va.mumps.debug.ui.terminal;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;

import java.io.IOException;
import java.io.OutputStream;

public class VistAOutputStream extends OutputStream {
	private static byte[] PATTERN = {0, 0, 0};

	private OutputStream actual;
	
	private VistAOutputStreamState state = VistAOutputStreamState.NOT_CONNECTED;
	
	private byte[] buffer = new byte[PATTERN.length];
	private int count;
	
	private byte[] debugInfo = new byte[10000];
	private int debugCount;
	
	private IMInterpreterConsumer listener;
	
	private byte[] namespace;
	private IMInterpreter interpreter;
	
	private final static boolean OUTDEBUG = false;
	
	public VistAOutputStream(String namespace, OutputStream actual, IMInterpreterConsumer listener) {
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
	
	public void setState(VistAOutputStreamState state) {
		this.state = state;
	}
	
	private void auxWrite(byte b) throws IOException {
		if (this.state == VistAOutputStreamState.BREAK_SEARCH) {
			this.auxWriteSearch(b);
		} else if (this.state == VistAOutputStreamState.RESUMED) {
			int n = this.debugCount;
			this.debugInfo[n] = b;
			++this.debugCount;
			if ((b == 10) && (this.debugInfo[n-1] == 13)) {
				this.writeInternalStreams(this.debugInfo, this.debugCount);
				this.debugCount = 0;
				this.state = VistAOutputStreamState.BREAK_SEARCH;
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
				this.state = VistAOutputStreamState.BREAK_FOUND;
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
				VistAOutputStreamState currentState = this.state;
				this.writeInternalStreams(this.debugInfo, this.debugCount);
				int currentCount = this.debugCount;
				this.debugCount = 0;
				this.state = VistAOutputStreamState.BREAK_SEARCH;
				if (currentState == VistAOutputStreamState.NOT_CONNECTED) {
					this.listener.handleConnected(this.interpreter);
				}  else if (currentState == VistAOutputStreamState.COMMAND_EXECUTE) {
					String str = new String(this.debugInfo, 0, currentCount);					
					this.listener.handleCommandExecuted(str);						
				} else if (currentState == VistAOutputStreamState.BREAK_FOUND) {					
					String str = new String(this.debugInfo, 0, currentCount);					
					if (str.startsWith("!!")) {
						this.listener.handleEnd();
					} else {					
						this.listener.handleBreak(str);
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
}
