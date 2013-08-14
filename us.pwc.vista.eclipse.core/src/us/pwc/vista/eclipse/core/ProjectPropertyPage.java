//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package us.pwc.vista.eclipse.core;

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

public class ProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	private Text backupDirText;
	
	public ProjectPropertyPage() {
		super();
	}
	
	private Composite createBackupDirPanel(Composite parent) {
		Composite panel= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		panel.setLayout(layout);
	
		Label label = new Label(panel, SWT.LEFT);
		label.setText(Messages.PPP_BKUP_DIR_LABEL);
		SWTHelper.setGridData(label, SWT.LEFT, false, SWT.CENTER, false);
		
		Text text = new Text(panel, SWT.BORDER);
		SWTHelper.setGridData(text, SWT.FILL, true, SWT.CENTER, false);
		this.backupDirText = text;
		
		return panel;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		contents.setLayout(layout);
		SWTHelper.setGridData(contents, SWT.FILL, true, SWT.FILL, true);
		
		Label label1st = new Label(contents, SWT.LEFT | SWT.WRAP);
		label1st.setText(Messages.PPP_BKUP_DIR_MSG_1);
		SWTHelper.setGridData(label1st, SWT.FILL, true, SWT.CENTER, false);
		Label label2nd = new Label(contents, SWT.LEFT | SWT.WRAP);
		label2nd.setText(Messages.PPP_BKUP_DIR_MSG_2);
		SWTHelper.setGridData(label2nd, SWT.FILL, true, SWT.CENTER, false);
				
		Composite backupDir = createBackupDirPanel(contents);
		SWTHelper.setGridData(backupDir, SWT.FILL, true, SWT.TOP, false);
		
		this.initialize();
		return contents;
    }
	
	private void initialize() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		try {
			String backupDir = VistACorePrefs.getServerBackupDirectory(project);
			this.backupDirText.setText(backupDir);			
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VistACorePlugin.PLUGIN_ID);
			this.backupDirText.setEnabled(false);
		}
	}
	
	@Override
	public boolean performOk() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class);
		try {
			VistACorePrefs.setServerBackupDirectory(project, this.backupDirText.getText());
		} catch (CoreException coreException) {
			IStatus status = new Status(IStatus.ERROR, VistACorePlugin.PLUGIN_ID, coreException.getMessage(), coreException);
			StatusManager.getManager().handle(status, StatusManager.SHOW);			
		}
		return super.performOk();
	}
}
