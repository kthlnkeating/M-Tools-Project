package gov.va.med.iss.connection.preferences;

import gov.va.med.iss.connection.VLConnectionPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class ProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage  {
	private Text serverName;

	public ProjectPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		contents.setLayout(layout);
		SWTHelper.setGridData(contents, SWT.FILL, true, SWT.FILL, true);
		
				
		Composite backupDir = createServerName(contents);
		SWTHelper.setGridData(backupDir, SWT.FILL, true, SWT.TOP, false);
		
		this.initialize();
		return contents;
    }

	private Composite createServerName(Composite parent) {
		Composite panel= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		panel.setLayout(layout);
	
		Label label = new Label(panel, SWT.LEFT);
		label.setText("Server name:");
		SWTHelper.setGridData(label, SWT.LEFT, false, SWT.CENTER, false);
		
		Text text = new Text(panel, SWT.BORDER);
		SWTHelper.setGridData(text, SWT.FILL, true, SWT.CENTER, false);
		this.serverName = text;
		
		return panel;
	}
	
	private void initialize() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		try {
			String backupDir = VistAConnectionPrefs.getServerName(project);
			this.serverName.setText(backupDir);			
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VLConnectionPlugin.PLUGIN_ID);
			this.serverName.setEnabled(false);
		}		
	}

	@Override
	public boolean performOk() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class);
		try {
			VistAConnectionPrefs.setServerName(project, this.serverName.getText());
		} catch (CoreException coreException) {
			IStatus status = new Status(IStatus.ERROR, VLConnectionPlugin.PLUGIN_ID, coreException.getMessage(), coreException);
			StatusManager.getManager().handle(status, StatusManager.SHOW);			
		}
		return super.performOk();
	}
}
