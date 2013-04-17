package gov.va.mumps.debug.ui.breakpoint;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MWatchpointDialog extends Dialog {
	private Text wpText;
	private String watchpoint;

	protected MWatchpointDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("M Watchpoint");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginRight = 5;
		gl_container.marginLeft = 10;
		container.setLayout(gl_container);

		wpText = new Text(container, SWT.BORDER);
		wpText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		wpText.setToolTipText("Enter a variable to break on");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */

	@Override
	protected Point getInitialSize() {
		return new Point(350, 105);
	}

	@Override
	protected void okPressed() {
		watchpoint = wpText.getText();

		super.okPressed();
	}

	public String getWatchpoint() {
		return watchpoint;
	}

}
