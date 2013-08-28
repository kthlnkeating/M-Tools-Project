package gov.va.mumps.debug.ui.launching;

import java.util.List;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import us.pwc.vista.eclipse.core.helper.SWTHelper;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class MMainTab extends AbstractLaunchConfigurationTab {
    private static class ProjectLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			return null;
		}
		
		@Override
		public String getText(Object element) {
			if(element instanceof IProject) {
				IProject p = (IProject) element;
				return p.getName();
			}
			return null;
		}
	}
	
	private Text projectCtrl;
	private Button browseProjectBtn;	
	
	private Text fileCtrl;
	private Button browseFileBtn;
	
	private Text entryTagCtrl;
	private Button browseEntryTagBtn;
	
	private IProject project;
	private List<String> entryTags;
	
	@Override
	public void createControl(Composite parent) {
		Composite container = SWTHelper.createComposite(parent, 3);

		this.projectCtrl = SWTHelper.createLabelTextPair(container, "Project:");
		this.browseProjectBtn = SWTHelper.createButton(container, "Browse");
		
		this.fileCtrl = SWTHelper.createLabelTextPair(container, "M file:");
		this.browseFileBtn = SWTHelper.createButton(container, "Browse");

		this.entryTagCtrl = SWTHelper.createLabelTextPair(container, "Entry tag:");
		this.browseEntryTagBtn = SWTHelper.createButton(container, "Browse");

		this.attachListeners();

		this.setControl(container);
	}

	private void attachListeners() {
		this.projectCtrl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				MMainTab.this.handleProjectChanged();			
			}
		});		

		this.browseProjectBtn.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				MMainTab.this.handleBrowseProject();			
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		this.fileCtrl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				MMainTab.this.handleFileChanged();			
			}
		});		
		this.browseFileBtn.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				MMainTab.this.handleBrowseFile();			
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		this.entryTagCtrl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				MMainTab.this.handleEntryTagChanged();			
			}
		});		
		this.browseEntryTagBtn.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				MMainTab.this.handleBrowseEntryTag();			
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
	
	private void handleProjectChanged() {
		this.project = null;
		String projectName = this.projectCtrl.getText();		
		if (projectName.isEmpty()) {
			this.updateProject("Project is not specified.");
			return;
		}				
		IWorkspaceRoot r = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = r.getProject(projectName);
		if (! project.exists()) {
			this.updateProject("Project " + projectName + " does not exist.");
			return;
		}
		this.project = project;
		this.updateProject(null);
	}
	
	private void updateProject(String errorMessage) {
		boolean enabled = (errorMessage == null);
		this.fileCtrl.setText("");
		this.entryTagCtrl.setText("");	
		this.setErrorMessage(errorMessage);
		this.setFileCtrlsEnabled(enabled);
		this.setEntryTagCtrlsEnabled(false);
		this.updateLaunchConfigurationDialog();		
	}
	
	private void setFileCtrlsEnabled(boolean enabled) {
		this.fileCtrl.setEnabled(enabled);
		this.browseFileBtn.setEnabled(enabled);
	}
	
	private void setEntryTagCtrlsEnabled(boolean enabled) {
		this.entryTagCtrl.setEnabled(enabled);
		this.browseEntryTagBtn.setEnabled(enabled);
	}
	
	private void handleBrowseProject() {
		IWorkspaceRoot r = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = r.getProjects();
		LabelProvider lp = new ProjectLabelProvider();
		Shell shell = this.getShell();
		ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, lp);
		dlg.setMultipleSelection(false);
		dlg.setTitle("Project Selection");
		dlg.setMessage("Please select a project:");
		dlg.setElements(projects);
		if (ListSelectionDialog.OK == dlg.open()) {
			IProject project = (IProject) dlg.getFirstResult();
			this.projectCtrl.setText(project.getName());
		}	
	}
	
	private void handleFileChanged() {
		this.entryTags = null;
		if (this.project == null) return;
		String filePath = this.fileCtrl.getText();
		if (filePath.isEmpty()) {
			this.updateFile("File path is not specified.");
			return;
		}				
		Path path = new Path(filePath);
		if (path.isAbsolute()) {
			this.updateFile("File path must be relative.");
			return;
		}
		IFile file = this.project.getFile(path);
		if (! file.exists()) {
			this.updateFile("File " + file.getName() + " does not exist.");
			return;		
		}
		if (! file.getName().endsWith(".m")) {
			this.updateFile("File " + file.getName() + " is not an M file.");
			return;					
		}
		this.entryTags = TagUtilities.getTags(file);
		if ((this.entryTags == null) || (this.entryTags.size() == 0) ) {
			this.updateFile("No entry tags was extracted from the file.");			
		} else {		
			this.updateFile(null);
		}
	}
	
	private void updateFile(String errorMessage) {
		boolean enabled = (errorMessage == null);
		this.entryTagCtrl.setText("");	
		this.setErrorMessage(errorMessage);
		this.setEntryTagCtrlsEnabled(enabled);
		this.updateLaunchConfigurationDialog();		
	}

	private void handleBrowseFile() {
		Shell shell = this.getShell();		
		ResourceListSelectionDialog dlg = new ResourceListSelectionDialog(shell, this.project, IResource.FILE);
		if (ListSelectionDialog.OK == dlg.open()) {
			IFile file = (IFile) dlg.getResult()[0];
			String path = ResourceUtilExtension.getRelativePath(this.project, file);		
			this.fileCtrl.setText(path);
		}		
	}
	
	private void handleEntryTagChanged() {
		if (this.entryTags != null) {
			String entryTag = this.entryTagCtrl.getText();
			if (entryTag.isEmpty()) {
				this.updateEntryTag("Entry tag " + entryTag + " is not specified.");
				return;
			}				
			for (String routineEntryTag : this.entryTags) {
				if (routineEntryTag.equals(entryTag)) {
					this.updateEntryTag(null);
					return;
				}
			}			
			this.updateEntryTag("Entry tag " + entryTag + "does not exist.");
		}
	}
	
	private void handleBrowseEntryTag() {
		String tag = TagUtilities.selectTag(this.entryTags);
		if (tag != null) {
			this.entryTagCtrl.setText(tag);			
		}
	}
	
	private void updateEntryTag(String errorMessage) {
		this.setErrorMessage(errorMessage);
		this.updateLaunchConfigurationDialog();		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IWorkbenchWindow w = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (w != null) {
			IWorkbenchPage page = w.getActivePage();
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection iss = (IStructuredSelection) selection;
				if (! iss.isEmpty()) {
					Object obj = iss.getFirstElement();
					if (obj instanceof IResource) {
						this.setDefault(configuration, (IResource) obj);
						return;
					}
				}
			}
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IEditorInput input = part.getEditorInput();
				if (input != null) {
					IResource resource = (IResource) input.getAdapter(IResource.class);
					this.setDefault(configuration, resource);
				}
			}
		}
	}
	
	private void setDefault(ILaunchConfigurationWorkingCopy configuration, IResource resource) {
		if (resource != null) {
			IProject project = resource.getProject();
			if (project != null) {
				String name = project.getName();
				configuration.setAttribute(MDebugConstants.ATTR_M_PROJECT_NAME, name);
			}
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				String name = file.getName();
				if (name.endsWith(".m")) {
					String relativePath = ResourceUtilExtension.getRelativePath(project, file);
					configuration.setAttribute(MDebugConstants.ATTR_M_FILE_PATH, relativePath.toString());
					ILaunchConfigurationDialog dialog = this.getLaunchConfigurationDialog();
					if (dialog != null) {
						String routineName = name.substring(0, name.length()-2);
						String newName = dialog.generateName(routineName);
						configuration.rename(newName);
					}
				}
			}
		}
		
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String projectName = configuration.getAttribute(MDebugConstants.ATTR_M_PROJECT_NAME, (String) null);
			if (projectName == null) {
				this.projectCtrl.setText("");
				return;
			} else {				
				this.projectCtrl.setText(projectName);
			}
			String filePath = configuration.getAttribute(MDebugConstants.ATTR_M_FILE_PATH, (String) null);
			if (filePath == null) {
				this.fileCtrl.setText("");
				return;
			} else {				
				this.fileCtrl.setText(filePath);
			}
			String entryTag = configuration.getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, (String) null);
			if (entryTag == null) {
				this.entryTagCtrl.setText("");
			} else {
				this.entryTagCtrl.setText(entryTag);
			}
		} catch (CoreException e) {
			this.project = null;
			setErrorMessage(e.getMessage());
		}
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		this.updateForApply(configuration, this.projectCtrl, MDebugConstants.ATTR_M_PROJECT_NAME);
		this.updateForApply(configuration, this.fileCtrl, MDebugConstants.ATTR_M_FILE_PATH);
		this.updateForApply(configuration, this.entryTagCtrl, MDebugConstants.ATTR_M_ENTRY_TAG);
	}
	
	private void updateForApply(ILaunchConfigurationWorkingCopy configuration, Text textCtrl, String attrName) {
		String value = textCtrl.getText();
		if (value.isEmpty()) {
			value = null;
		}
		configuration.setAttribute(attrName, value);	
	}
	
	@Override
	public String getName() {
		return "Main";
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (this.getErrorMessage() == null) {
			return super.isValid(launchConfig);
		} else {
			return false;
		}
	}
}
