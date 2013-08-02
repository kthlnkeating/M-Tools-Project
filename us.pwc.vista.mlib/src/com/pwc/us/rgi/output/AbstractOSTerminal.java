package com.pwc.us.rgi.output;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractOSTerminal extends Terminal {
	private OutputStream os;
	private String eol = Utility.getEOL();

	protected abstract OutputStream getOutputStream() throws IOException;
	
	@Override
	public void stop() throws IOException {
		if (this.os != null) {
			this.os.close();
		} 
	}

	@Override
	public void write(String data) throws IOException {
		if (this.os == null) {
			this.os = this.getOutputStream();
		}
		this.os.write(data.getBytes());
	}
	
	@Override
	public void writeEOL() throws IOException {
		this.write(this.eol);
	}
}
