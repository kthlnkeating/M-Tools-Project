package us.pwc.vista.eclipse.tools.util;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.custom.StyledText;

public class SWTTextOutputStream extends OutputStream {
	private StyledText text;
	
	public SWTTextOutputStream(StyledText text) {
		this.text = text;
	}
	
	@Override
	public void write(int b) throws IOException {
		if (text == null || text.isDisposed())
			return;		
		text.append(String.valueOf((char) b));
	}
}
