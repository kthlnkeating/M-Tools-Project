package us.pwc.eclipse.vista.views;

import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import us.pwc.eclipse.vista.util.SWTTextOutputStream;


public class MRAToolConsoleView extends ViewPart {
	private static StyledText text;
	public static OutputStream out = new SWTTextOutputStream(text);
	
	public void createPartControl(Composite parent) {
		text = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
		FontData fd = new FontData("Courier New", 10, 0);
		text.setFont(new Font(Display.getCurrent(), fd));
		out = new SWTTextOutputStream(text);

		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				//System.setOut(oldOut);
			}
		});
	}
	
	public static void clearText() {
		if (text != null)
			text.setText("");
	}

	public void setFocus() {
		if (text != null)
			text.setFocus();
	}
}