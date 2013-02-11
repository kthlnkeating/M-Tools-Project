/*
 * based on snippet from 
 * http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/FormLayoutcreateasimpleOKandCANCELdialogusingformlayout.htm
 * 
 */

package gov.va.med.iss.meditor.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class YesNoDialog {
	
	private boolean result = true;
	private Button btnOK = null;
	private Button btnCancel = null;

  public static void main(String[] args) {
  }
  
  public boolean open(String title, String input) {
	final Shell shell = new Shell(MEditorUtilities.getIWorkbenchWindow().getShell(), SWT.DIALOG_TRIM |
			SWT.APPLICATION_MODAL);
	shell.setText(title);
	shell.setSize(300,210);
//	Display display = shell.getDisplay();
	
//    shell.pack();
//    shell.open();
//    Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
    Label label = new Label(shell, SWT.NONE); // dialog, SWT.NONE);
    label.setText(input);
    btnOK = new Button(shell, SWT.PUSH); // dialog, SWT.PUSH);
    btnOK.setText("&OK");
    btnCancel = new Button(shell, SWT.PUSH); // dialog, SWT.PUSH);
    btnCancel.setText("&Cancel");

    FormLayout form = new FormLayout();
    form.marginWidth = form.marginHeight = 8;
//    dialog.setLayout(form);
    shell.setLayout(form);
    FormData okData = new FormData();
    okData.top = new FormAttachment(label, 8);
    btnOK.setLayoutData(okData);
    FormData cancelData = new FormData();
    cancelData.left = new FormAttachment(btnOK, 8);
    cancelData.top = new FormAttachment(btnOK, 0, SWT.TOP);
    btnCancel.setLayoutData(cancelData);
/*
    dialog.setDefaultButton(btnOK);
    dialog.pack();
    dialog.open();
*/    
    shell.setDefaultButton(btnOK);
    shell.pack();
    shell.open();

	Listener listener = new Listener() {
		public void handleEvent(Event event) {
//			String strInput = "";
			if (event.widget == btnOK) {
				result = true;
			}
			else {
				result = false;
			}
			shell.setVisible(false);
			shell.close();
		}
	};
	
	btnOK.addListener(SWT.Selection, listener);
	btnCancel.addListener(SWT.Selection, listener);

/*
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
*/
	shell.open();
	Display display = MEditorUtilities.getIWorkbenchWindow().getShell().getDisplay();
	while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) display.sleep();
	}

    return result;
  }
}
