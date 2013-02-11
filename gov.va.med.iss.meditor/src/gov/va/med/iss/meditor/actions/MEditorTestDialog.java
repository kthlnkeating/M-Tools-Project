package gov.va.med.iss.meditor.actions;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class MEditorTestDialog extends Dialog {
			Object result;
				
			public MEditorTestDialog (Shell parent, int style) {
				super (parent, style);
			}
			public MEditorTestDialog (Shell parent) {
				this (parent, 0); // your default style bits go here (not the Shell's style bits)
			}
			public Object open () {
				Shell parent = getParent();
				Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				shell.setText(getText());
				// Your code goes here (widget creation, set result, etc).
				shell.open();
				Display display = parent.getDisplay();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) display.sleep();
				}
				return result;
			}
		 }


