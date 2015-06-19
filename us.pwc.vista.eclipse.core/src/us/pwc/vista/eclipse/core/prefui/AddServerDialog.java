package us.pwc.vista.eclipse.core.prefui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.core.VistACorePlugin;
import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class AddServerDialog extends Dialog {
	private static final String BOUNDS = "bounds"; //$NON-NLS-1$

	private String title;
	
	private Text nameCtrl;
	private Text addressCtrl;
	private Text portCtrl;
	private Text acCtrl;
	private Text vcCtrl;
	
	private ServerData data;

	public AddServerDialog(Shell parentShell, String title) {
		super(parentShell);
		this.title = title;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(this.title);
	}
		
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite panel = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		SWTHelper.setGridData(panel, SWT.FILL, true, SWT.FILL, false);

	    Label label = new Label(parent, SWT.NONE);
	    label.setText("Mac users must fill in AC/VC");
		this.nameCtrl = this.createTextLabelPair(panel, "Server name:");
		this.addressCtrl = this.createTextLabelPair(panel, "Server address:");
		this.portCtrl = this.createTextLabelPair(panel, "Server port:");
		this.acCtrl = this.createTextLabelPair(panel, "Access Code:");
		this.vcCtrl = this.createTextLabelPair(panel, "Verify Code:");
		
		return panel;
	}

	private Text createTextLabelPair(Composite parent, String labelText) {
		Label labelCtrl = new Label(parent, SWT.NONE);
		labelCtrl.setText(labelText);

		Text textCtrl = new Text(parent, SWT.BORDER | SWT.SINGLE);
		textCtrl.setText("");
		textCtrl.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				AddServerDialog.this.handleTextChanged();
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textCtrl.setLayoutData(gd);	
		return textCtrl;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button b = this.getButton(IDialogConstants.OK_ID);
		b.setEnabled(false);
	}
	
	private void handleTextChanged() {
		Button button = this.getButton(IDialogConstants.OK_ID);		
		ServerData currentData = this.ctrlToData();
		boolean enabled = currentData.isComplete();
		button.setEnabled(enabled);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public boolean close() {
		if (this.getReturnCode() == OK) {
			this.data = ctrlToData();
		}
		return super.close();
	}
	
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		return VistACorePlugin.getDefault().getDialogSettings(this, BOUNDS);
	}
	
	private ServerData ctrlToData() {
		String name    = this.nameCtrl.getText();
		String address = this.addressCtrl.getText();
		String port    = this.portCtrl.getText();
		String ac      = this.acCtrl.getText();
		String vc      = this.vcCtrl.getText();
		return new ServerData(name, address, port, ac, vc);
	}

	public ServerData getData() {
		return this.data;
	}
}
