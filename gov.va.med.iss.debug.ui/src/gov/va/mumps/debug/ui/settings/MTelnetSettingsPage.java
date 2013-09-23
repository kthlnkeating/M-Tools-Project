package gov.va.mumps.debug.ui.settings;

import gov.va.mumps.debug.core.MDebugSettings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class MTelnetSettingsPage extends PropertyPage implements IWorkbenchPreferencePage {
	private Text hostCtl;
	private Text portCtl;
	private Text timeoutCtl;
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		contents.setLayout(gl);

		this.hostCtl = SWTHelper.createLabelTextPair(contents, "Host:");
		this.portCtl = SWTHelper.createLabelTextPair(contents, "Port:");
		this.timeoutCtl = SWTHelper.createLabelTextPair(contents, "Timeout (sec):");
				
		this.initialize();
		return contents;
	}
	
	private void initialize() {
		this.hostCtl.setText(MDebugSettings.getTelnetHost());
		this.portCtl.setText(MDebugSettings.getTelnetPort());
		this.timeoutCtl.setText(MDebugSettings.getTelnetTimeout());
	}

	@Override
	public boolean performOk() {
		MDebugSettings.setTelnetHost(this.hostCtl.getText());
		MDebugSettings.setTelnetPort(this.portCtl.getText());
		MDebugSettings.setTelnetTimeout(this.timeoutCtl.getText());
		return super.performOk();
	}
}
