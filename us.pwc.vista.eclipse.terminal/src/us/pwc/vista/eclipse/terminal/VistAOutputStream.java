package us.pwc.vista.eclipse.terminal;

import java.io.IOException;
import java.io.OutputStream;

public class VistAOutputStream extends OutputStream {
	private static enum StateEnum {
		NOT_CONNECTED,
		COMMAND_EXECUTE,
		COMMAND_STREAM_EXPECTED,
		BREAK_SEARCH,
		BREAK_FOUND
	}
	
	private static byte[] PATTERN = "Trace: ZBREAK".getBytes();
	private static byte[] END_PATTERN = "VISTA".getBytes();
	
	private OutputStream actual;
	
	private StateEnum state = StateEnum.NOT_CONNECTED;
	
	private byte[] buffer = new byte[PATTERN.length];
	private int count;
	
	private byte[] debugInfo = new byte[200];
	private int debugCount;
	
	private IVistAStreamListener listener;
	
	public VistAOutputStream(OutputStream actual, IVistAStreamListener listener) {
		this.actual = actual;
		this.listener = listener;
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
	
	public void startCommand() {
		this.state = StateEnum.COMMAND_EXECUTE;
	}
	
	public void debugCommand(String command) {
		this.state = StateEnum.COMMAND_STREAM_EXPECTED;
	}
	
	private void auxWrite(byte b) throws IOException {
		if (this.state == StateEnum.BREAK_SEARCH) {
			this.auxWriteSearch(b);
		} else if (this.state == StateEnum.COMMAND_STREAM_EXPECTED) {
			int n = this.debugCount;
			this.debugInfo[n] = b;
			++this.debugCount;
			if ((b == 10) && (this.debugInfo[n-1] == 13)) {
				this.debugCount = 0;
				this.state = StateEnum.BREAK_SEARCH;
			}
		} else {
			this.debugInfo[this.debugCount] = b;
			++this.debugCount;			
			if (b == '>') this.auxWriteBreak(b, END_PATTERN);
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
				this.state = StateEnum.BREAK_FOUND;
				count = 0;
			}			
		}
	}

	private void auxWriteBreak(byte b, byte[] pattern) throws IOException {
		int endIndex = this.debugCount-6;
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
				StateEnum currentState = this.state;
				int currentCount = this.debugCount;
				this.debugCount = 0;
				this.state = StateEnum.BREAK_SEARCH;
				if (currentState == StateEnum.NOT_CONNECTED) {
					this.listener.handleConnected();
				}  else if (currentState == StateEnum.COMMAND_EXECUTE) {
					this.listener.handleCommandExecuteEnded();						
				} else if (currentState == StateEnum.BREAK_FOUND) {						
					String str = new String(this.debugInfo, 0, currentCount);
					this.listener.handleBreak(str);
				}
			}			
		}
	}
}
