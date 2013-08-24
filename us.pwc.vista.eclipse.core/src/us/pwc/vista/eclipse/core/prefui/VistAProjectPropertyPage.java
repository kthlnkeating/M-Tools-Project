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

package us.pwc.vista.eclipse.core.prefui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.Messages;
import us.pwc.vista.eclipse.core.VistACorePlugin;
import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class VistAProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	private Text backupDirText;
	private Text serverName;
	private Button filterServerNames;
	
	public VistAProjectPropertyPage() {
		super();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns= 2;
		contents.setLayout(layout);
		SWTHelper.setGridData(contents, SWT.FILL, true, SWT.FILL, true);
		
		SWTHelper.addLabel(contents, Messages.PPP_BKUP_DIR_MSG_1, 2);
		SWTHelper.addLabel(contents, "Choose a server to use with this project.", 2);		
		this.serverName = SWTHelper.createLabelTextPair(contents, "Server name:");

		SWTHelper.addEmptyLabel(contents, 2);
		
		SWTHelper.addLabel(contents, Messages.PPP_BKUP_DIR_MSG_2, 2);
		this.backupDirText = SWTHelper.createLabelTextPair(contents, Messages.PPP_BKUP_DIR_LABEL);
			
		SWTHelper.addEmptyLabel(contents, 2);

		SWTHelper.addLabel(contents, "Each routine file needs to be unique within projects.", 2);		
		SWTHelper.addLabel(contents, "Use \"server name\" project subfolders to work with multiple servers.", 2);		
		this.filterServerNames = SWTHelper.createCheckButton(contents, "Do not use M files in other server named subfolders", 2);
		
		this.initialize();
		return contents;
    }
	
	private void initialize() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		try {
			String backupDir = VistACorePrefs.getServerBackupDirectory(project);
			this.backupDirText.setText(backupDir);			
			String serverName = VistACorePrefs.getServerName(project);
			this.serverName.setText(serverName);			
			boolean filterServer = VistACorePrefs.getDoNotUseServerNameFoldersFlag(project);
			this.filterServerNames.setSelection(filterServer);			
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
			VistACorePrefs.setServerName(project, this.serverName.getText());
			VistACorePrefs.setServerBackupDirectory(project, this.backupDirText.getText());
			VistACorePrefs.setDoNotUseServerNameFoldersFlag(project, this.filterServerNames.getSelection());
		} catch (CoreException coreException) {
			IStatus status = new Status(IStatus.ERROR, VistACorePlugin.PLUGIN_ID, coreException.getMessage(), coreException);
			StatusManager.getManager().handle(status, StatusManager.SHOW);			
		}
		return super.performOk();
	}
}
