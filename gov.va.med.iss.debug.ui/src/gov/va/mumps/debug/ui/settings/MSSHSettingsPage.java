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

public class MSSHSettingsPage extends PropertyPage implements IWorkbenchPreferencePage {
	private Text hostCtl;
	private Text portCtl;
	private Text userCtl;
	private Text passwordCtl;	
	private Text timeoutCtl;
	private Text keepAliveCtl;
	
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
		this.userCtl = SWTHelper.createLabelTextPair(contents, "User:");
		this.passwordCtl = SWTHelper.createLabelTextPair(contents, "Pasword:");		
		this.timeoutCtl = SWTHelper.createLabelTextPair(contents, "Timeout (sec):");
		this.keepAliveCtl = SWTHelper.createLabelTextPair(contents, "Keep alive (sec):");
				
		this.initialize();
		return contents;
	}

	private void initialize() {
		this.hostCtl.setText(MDebugSettings.getSSHHost());
		this.portCtl.setText(MDebugSettings.getSSHPort());
		this.timeoutCtl.setText(MDebugSettings.getSSHTimeout());		
		this.userCtl.setText(MDebugSettings.getSSHUser());
		this.passwordCtl.setText(MDebugSettings.getSSHPassword());
		this.keepAliveCtl.setText(MDebugSettings.getSSHKeepAlive());		
	}

	@Override
	public boolean performOk() {
		MDebugSettings.setSSHHost(this.hostCtl.getText());
		MDebugSettings.setSSHPort(this.portCtl.getText());
		MDebugSettings.setSSHTimeout(this.timeoutCtl.getText());
		MDebugSettings.setSSHUser(this.userCtl.getText());
		MDebugSettings.setSSHPassword(this.passwordCtl.getText());
		MDebugSettings.setSSHKeepAlive(this.keepAliveCtl.getText());
		return super.performOk();
	}
}
