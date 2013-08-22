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

package us.pwc.vista.eclipse.server.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.core.helper.SWTHelper;
import us.pwc.vista.eclipse.server.VistAServerPlugin;

public class SelectFolderPage extends FileHandleSupplyPage {
	private IWorkbench workbench;
	
	private Text folderCtrl;
	private Button browseCtrl;
	private Label targetFileCtrl;
	
	private IProject project;
	private String routineName; 

	public SelectFolderPage(String pageName, IWorkbench workbench) {
		super(pageName);
		this.workbench = workbench;
        setTitle("Folder");
        setDescription("Select folder for M routine.");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = SWTHelper.createComposite(parent, 3);

		this.folderCtrl = SWTHelper.createLabelTextPair(container, "Target folder:");
		this.browseCtrl = SWTHelper.createButton(container, "Browse");
		this.targetFileCtrl = SWTHelper.createLabelLabelPair(container, "Target file:");
		
		this.setControl(container);
		this.initialize();
	}
	
	private void initialize() {
		this.setEnabledAll(false);
		this.attachListeners();
	}
	
	private void setEnabledAll(boolean enabled) {
		this.folderCtrl.setEnabled(enabled);
		this.browseCtrl.setEnabled(enabled);		
		this.setPageComplete(enabled);
	}
	
	private void attachListeners() {		
		this.folderCtrl.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				SelectFolderPage.this.validateFolder();	
			}
		});	

		this.browseCtrl.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectFolderPage.this.browseFolder();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectFolderPage.this.browseFolder();
			}
		});		
	}
	
	public void setProject(IProject project, String routineName) {
		this.project = project;
		this.routineName = routineName;
		this.folderCtrl.setText("");
		if ((project == null) || (routineName == null) || routineName.isEmpty()) {
			this.setEnabledAll(false);
			this.updateTargetFile(false);
		} else {
			this.setEnabledAll(true);			
			this.updateTargetFile(true);
		}
	}
	
	private void updateTargetFile(boolean valid) {
		if (valid) {
			IPath path = this.getFilePath();
			this.targetFileCtrl.setText(path.toOSString());
		} else {
			this.targetFileCtrl.setText("<none>");
		}
	}

	private IPath getFilePath() {
		IPath path = this.project.getFullPath();
		IPath relativeFilePath = this.getRelativeFilePath();
		path = path.append(relativeFilePath);
		return path;
	}
	
	private IPath getRelativeFilePath() {
		String folder = this.folderCtrl.getText();
		String fileName = this.routineName + ".m";
		if (folder.isEmpty()) {
			return new Path(fileName);
		} else {
			IPath path = Path.fromOSString(folder);
			return path.append(fileName);
		}
	}

	private String getBackupFolderName(IProject project) {
		try {
			String result = VistACorePrefs.getServerBackupDirectory(project);
			return result;
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VistAServerPlugin.PLUGIN_ID);
			return "";
		}
	}
	
	private void browseFolder() {
		if (this.project != null) {
			Shell parentShell = this.workbench.getActiveWorkbenchWindow().getShell();
			ContainerSelectionDialog dlg = new ContainerSelectionDialog(parentShell, this.project, false, "Select a folder:");
			String backupName = this.getBackupFolderName(this.project);
			final IPath projectPath = this.project.getFullPath();
			final IPath backupPath = projectPath.append(backupName);
			final String projectName = this.project.getName();
			dlg.setValidator(new ISelectionValidator() {				
				@Override
				public String isValid(Object selection) {
					if (selection instanceof IPath) {						
						IPath path = (IPath) selection;
						if (projectPath.isPrefixOf(path)) {
							if (path.equals(backupPath)) {
								return "This folder is reserved as backup directory for routines loaded from server.";
							}
							return null;
						}
					}
					return "This folder does not belong to project " + projectName + ".";
				}
			});
			if (IDialogConstants.OK_ID == dlg.open()) {
				Object[] result = dlg.getResult();
				if ((result != null) && (result.length > 0)) {
					IPath path = (IPath) result[0];
					path = path.makeRelativeTo(project.getFullPath());					
					this.folderCtrl.setText(path.toOSString());
					this.validateFolder();
				}
			}			
		}
	}

	private boolean validateFolder() {
		String folder = this.folderCtrl.getText();
		if (! folder.isEmpty()) {
			String errorMessage = this.getFolderErrorMessage(folder);
			if (errorMessage != null) {
				this.setErrorMessage(errorMessage);
				this.setPageComplete(false);				
				this.updateTargetFile(false);
				return false;									
			}
		}
		this.setErrorMessage(null);
		this.setPageComplete(true);				
		this.updateTargetFile(true);
		return true;
	}
	
	private String getFolderErrorMessage(String folder) {
		IPath projectPath = this.project.getFullPath();
		if (! projectPath.isValidPath(folder)) {
			return "Invalid folder.";
		}		
		IPath path = Path.fromOSString(folder);
		if (path.isAbsolute()) {
			return "Folder needs to be relative.";
		}
		IResource resource = this.project.findMember(path);
		if (resource == null) {
			return "Folder does not exist.";			
		} else if (resource.getType() != IResource.FOLDER) {
			return "Existing resource is not a folder.";
		}		
		return null;
	}
	
	@Override
	public IFile getFileHandle() {
		IPath relativePath = this.getRelativeFilePath();
		IFile result = this.project.getFile(relativePath);
		return result;
	}
}
