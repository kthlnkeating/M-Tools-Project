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


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.ServerData;
import us.pwc.vista.eclipse.server.VistAServerPlugin;
import us.pwc.vista.eclipse.server.core.PreferencesPathResolver;
import us.pwc.vista.eclipse.server.core.RootPathResolver;
import us.pwc.vista.eclipse.server.core.VFPackageRepo;
import us.pwc.vista.eclipse.server.core.VFPathResolver;
import us.pwc.vista.eclipse.server.dialog.CustomDialogHelper;
import us.pwc.vista.eclipse.server.preferences.NewFileFolderScheme;
import us.pwc.vista.eclipse.server.preferences.VistAServerPrefs;

public class SelectRoutineWizard extends Wizard implements IWorkbenchWizard {
	private final static String ROUTINE_PAGE = "Select Routine";
	private final static String FOLDER_PAGE = "Select Folder";
	private final static String FILE_PAGE = "File Notice";
	
	private IWorkbench workbench;
	
	private FileHandleSupplyPage nextPage;
	
	private IFile fileHandle;

	public SelectRoutineWizard() {
		super();
        super.setWindowTitle("Load M Routine");
		super.setForcePreviousAndNextButtons(true);
	}
	
	@Override
	public void addPages() {
		SelectRoutinePage routineServerPage = new SelectRoutinePage(ROUTINE_PAGE, this.workbench);
		this.addPage(routineServerPage);
		SelectFolderPage folderPage = new SelectFolderPage(FOLDER_PAGE, this.workbench);
		this.addPage(folderPage);
		FileNoticePage filePage = new FileNoticePage(FILE_PAGE);
		this.addPage(filePage);
	}
	
	public void setNextError(String message) {
		this.nextPage = null;
		SelectRoutinePage selectPage = (SelectRoutinePage) this.getPage(ROUTINE_PAGE);
		selectPage.setErrorMessage(message);
	}
		
	public void setNextPage(IFile file) {
		if (file != null) {
			FileNoticePage page = (FileNoticePage) this.getPage(FILE_PAGE);
			page.setTitle("Existing File");
			page.setDescription("Update existing file with M routine.");
			page.setFileHandle(file);
			this.nextPage = page;
		} else {
			try {
				IProject project = this.getProject();
				String routineName = this.getRoutineName();
				NewFileFolderScheme scheme = VistAServerPrefs.getNewFileFolderScheme(project);
				if (scheme == NewFileFolderScheme.ASK) {
					SelectFolderPage page = (SelectFolderPage) this.getPage("Select Folder");	
					page.setProject(project, routineName);
					this.nextPage = page;
				} else {
					FileNoticePage page = (FileNoticePage) this.getPage(FILE_PAGE);
					page.setTitle("New File");
					page.setDescription("Create new file with M routine.");
					String serverName = this.getServerData().getName();
					IFile newFile = getNewFileHandle(project, serverName, routineName);
					page.setFileHandle(newFile);
					this.nextPage = page;
				}	
			} catch (CoreException coreException) {
				this.nextPage = null;
				StatusManager.getManager().handle(coreException, VistAServerPlugin.PLUGIN_ID);					
			}
		}
	}
	
	public void resetNextPage() {
		if (this.nextPage != null) {
			this.nextPage.reset();
		}
	}

	private static IFile getNewFileHandle(IProject project, String serverNameIn, String routineName) throws CoreException {		
		NewFileFolderScheme locationScheme = VistAServerPrefs.getNewFileFolderScheme(project);
		switch (locationScheme) {
		case NAMESPACE_SPECIFIED:
			IResource resource = project.findMember("Packages.csv");
			if (resource != null) {
				IFile file = (IFile) resource;
				VFPathResolver vpr = new VFPathResolver(new VFPackageRepo(file));
				return vpr.getFileHandle(project, routineName);
			} else {
				RootPathResolver rpr = new RootPathResolver();
				rpr.getFileHandle(project, routineName);
			}			
		case PROJECT_ROOT:
			boolean serverNameToFolder = VistAServerPrefs.getAddServerNameSubfolder(project);
			int namespaceDigits = VistAServerPrefs.getAddNamespaceCharsSubfolder(project);
			String serverName = serverNameToFolder ? serverNameIn : null;
			PreferencesPathResolver ppr = new PreferencesPathResolver(serverName, namespaceDigits);
			return ppr.getFileHandle(project, routineName);
		default:
			IContainer container = CustomDialogHelper.selectWritableFolder(project);
			if (container != null) {
				IPath path = new Path(routineName + ".m");
				return container.getFile(path);
			} else {
				return null;
			}
		}
	}

	@Override
    public IWizardPage getNextPage(IWizardPage page) {
		return this.nextPage;
	}
	
	@Override
    public boolean canFinish() {
    	if (this.nextPage != null) {
    		SelectRoutinePage selectPage = (SelectRoutinePage) this.getPage(ROUTINE_PAGE);
    		return selectPage.isPageComplete() && this.nextPage.isPageComplete();
		}
       return false;
    }

    @Override
	public boolean performFinish() {
		if (this.nextPage == null) {
			this.fileHandle = null;
			return false;
		} else {
			this.fileHandle = this.nextPage.getFileHandle();
			return true;
		}
	}

	@Override
	public boolean performCancel() {
		this.fileHandle = null;
		return true;
	}

	@Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
    }
	
	public IFile getFileHandle() {
		return this.fileHandle;
	}
	
	public ServerData getServerData() {
		SelectRoutinePage selectPage = (SelectRoutinePage) this.getPage(ROUTINE_PAGE);
		return selectPage.getServerData();
	}
	
	public IProject getProject() {
		SelectRoutinePage selectPage = (SelectRoutinePage) this.getPage(ROUTINE_PAGE);
		return selectPage.getProject();
	}

	public String getRoutineName() {
		SelectRoutinePage selectPage = (SelectRoutinePage) this.getPage(ROUTINE_PAGE);
		return selectPage.getRoutineName();
	}
}
