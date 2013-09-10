package us.pwc.vista.eclipse.terminal;

import java.io.IOException;
import java.io.OutputStream;

public class VistAOutputStream extends OutputStream {
	private static enum StateEnum {
		BREAK_SEARCH,
		BREAK_FOUND
	}
	
	private static byte[] PATTERN = "Trace: ZBREAK".getBytes();
	private static byte[] END_PATTERN = "VISTA".getBytes();
	
	private OutputStream actual;
	
	private StateEnum state = StateEnum.BREAK_SEARCH;
	
	private byte[] buffer = new byte[PATTERN.length];
	private int count;
	
	private byte[] debugInfo = new byte[200];
	private int debugCount;
	
	public VistAOutputStream(OutputStream actual) {
		this.actual = actual;
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
	
	private void auxWrite(byte b) throws IOException {
		if (this.state == StateEnum.BREAK_SEARCH) {
			this.auxWriteSearch(b);
		} else {
			this.auxWriteBreak(b);
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

	private void auxWriteBreak(byte b) throws IOException {
		this.debugInfo[this.debugCount] = b;
		++this.debugCount;
		if (b == '>') {
			int endIndex = this.debugCount-6;
			if (endIndex < 0) endIndex = 0;
			int n = END_PATTERN.length;
			byte epb = END_PATTERN[END_PATTERN.length-1];
			for (int i=this.debugCount-1; i>=endIndex; --i) {
				if (epb == this.debugInfo[i]) {
					for (int j=1; j<n; ++j) {
						if (i < j) return;
						if (END_PATTERN[n-j-1] != this.debugInfo[i-j]) {
							return;
						}
					}					
					this.state = StateEnum.BREAK_SEARCH;
					this.debugCount = 0;				
				}			
			}
		} 
	}
}
