package gov.va.med.iss.mdebugger.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class MDebuggerConsoleDisplay extends ViewPart {
	public static String strval = "";
	public static StyledText text;
	public static MDebuggerConsoleDisplay currentInstance = null;

	public MDebuggerConsoleDisplay() {
            super();
    }
    public void setFocus() {
    	text.setFocus();
    }
    
    public static void setToFocus() {
    	text.setFocus();
    }
    
    public static void clearConsole() {
    	// 080523 - following added after an inactive console window 
    	// showed up on top of the M-Debugger console and resulting in
    	// a null value for text (and the debugger console) - resetting 
    	// the perspective would get rid of it, but this code also
    	// activates the M-Debugger console directly since the user might
    	// not think or resetting the perspective (and doesn't need a null
    	// error)
    	if (currentInstance == null)
    		try {
    			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("gov.va.med.iss.mdebugger.views.MDebuggerConsoleDisplay");
    		} catch (Exception e) {
    			
    		}
    	// end of 080523 addition
    	text.setText("");
    	strval = "";
    }

    public void createPartControl(Composite parent) {
        text = new StyledText(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    	text.addKeyListener(new MDebuggerKeyListener());
    	text.setText("");
    	currentInstance = this;
    }
 
    public static void updateView(String input, boolean addToBase) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(MDebuggerConsoleDisplay.getCurrentInstance());
    	try {
    		text.setText("");
    	} catch (Exception e) {
    		strval = e.getMessage();
    	}
    	text.setText(strval + input);
    	if (addToBase)
    		strval = strval + input;
    	text.setCaretOffset(text.getText().length());
    	int lineCount = text.getLineCount();
   		int linesDisplayed = text.getClientArea().height / text.getLineHeight();
   		if (lineCount+1 > linesDisplayed) {
    		text.setTopIndex(lineCount - linesDisplayed + 1);
     	}
    }
    
    public static MDebuggerConsoleDisplay getCurrentInstance() {
    	return currentInstance;
    }
}