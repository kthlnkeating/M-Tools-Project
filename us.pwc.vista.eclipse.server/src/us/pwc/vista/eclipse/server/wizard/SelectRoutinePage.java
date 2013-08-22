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

import gov.va.med.iss.connection.preferences.ServerData;
import gov.va.med.iss.connection.preferences.VistAConnectionPrefs;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.VistACorePlugin;
import us.pwc.vista.eclipse.core.helper.SWTHelper;
import us.pwc.vista.eclipse.core.validator.ICommonRegexs;
import us.pwc.vista.eclipse.core.validator.RegexInputValidator;
import us.pwc.vista.eclipse.server.Messages;

public class SelectRoutinePage extends WizardPage {
	private Text routineNameCtrl;
	private Combo serversCtrl;
	private Button overrideCtrl;

	private Combo projectCtrl;
	
	private String routineName;
	private ServerData serverData;
	private IProject project;
	
	public SelectRoutinePage(String pageName, IWorkbench workbench) {
		super(pageName);
        setTitle("Server Routine Description");
        setDescription("Specify routine name and server.");
	}
	
	@Override
    public boolean canFlipToNextPage() {
        return isPageComplete();
    }

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		this.addRoutineNameCtrl(container);
		this.serversCtrl = this.addCombo(container, "Server name:");
		this.overrideCtrl = SWTHelper.createCheckButton(container, "Override project server name property", 2);		

		SWTHelper.addEmptyLabel(container, 2);
		this.projectCtrl = this.addCombo(container, "Target project:");
				
		this.setControl(container);
		this.initialize();
	}
	
    private void addRoutineNameCtrl(Composite parent) {
		IInputValidator validator = new RegexInputValidator(true, ICommonRegexs.M_ROUTINE_NAME, Messages.ROUTINE_NAME);
		this.routineNameCtrl = SWTHelper.createLabelTextPair(parent, "Routine name:");
		this.routineNameCtrl.setData(validator);
	}
	
	private void initialize() {
		List<ServerData> serverDataList = VistAConnectionPrefs.getServers();
		this.serversCtrl.setData(serverDataList);
		int count = serverDataList.size();
		if (count == 0) {
			this.disableAll("No server is specified in Preferences/VistA/Connection.");
			return;			
		}
		for (ServerData sd : serverDataList) {
			String s = sd.toUIString();
			this.serversCtrl.add(s);
		}
		this.serversCtrl.select(0);
		this.updateProject(0);
		if (count == 1) {
			this.serversCtrl.setEnabled(false);
		} 
		
		this.attachListeners();
		this.setPageComplete(false);
	}
	
	private void updateProject(int serverIndex) {
		this.projectCtrl.removeAll();
		IProject[] projects = this.getProjectsAssignedToServer(serverIndex);
		if (projects != null) {	
			for (IProject project : projects) {
				this.projectCtrl.add(project.getName());				
			}
			this.updateProject(projects);
		}
	}
	
	private IProject[] getProjectsAssignedToServer(int serverIndex) {
		try {
			@SuppressWarnings("unchecked")
			List<ServerData> serverDataList = (List<ServerData>) this.serversCtrl.getData();
			ServerData serverData = serverDataList.get(serverIndex);		
			String serverName = serverData.getName();		
			IProject[] projects = getProjects();
			IProject[] result = new IProject[projects.length];
			int count = 0;
			for (IProject project : projects) {
				String projectServerName = VistAConnectionPrefs.getServerName(project);
				if (projectServerName.equals(serverName)) {
					result[count] = project;
					++count;
				}				
			}
			return Arrays.copyOf(result, count);
		} catch (CoreException coreException) {
			this.disableAll(coreException.getMessage());
			StatusManager.getManager().handle(coreException, VistACorePlugin.PLUGIN_ID);
			return null;
		}
	}
		
	private static IProject[] getProjects() {
		IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = wr.getProjects();
		return projects;
	}
	
	private void updateProject(IProject[] projects) {
		int count = projects.length;			
		if (count == 1) {
			this.projectCtrl.select(0);
		} 
		if (count < 2) {
			this.projectCtrl.setEnabled(false);
		} else {
			this.projectCtrl.setEnabled(true);
		}
	}
	
	private void attachListeners() {
		this.routineNameCtrl.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				SelectRoutinePage.this.validateRoutineNameCtrl(true);	
			}
		});
		
		this.serversCtrl.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleServerChanged();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleServerChanged();				
			}
		});
		
		this.overrideCtrl.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleOverrideSwitch();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleOverrideSwitch();
			}
		});				
		
		this.projectCtrl.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleProjectChanged();				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectRoutinePage.this.handleProjectChanged();				
			}
		});
	}
	
	private boolean validateRoutineNameCtrl(boolean checkOthers) {
		IInputValidator validator = (IInputValidator) this.routineNameCtrl.getData();
		String routineName = this.routineNameCtrl.getText();
		String errorMessage = validator.isValid(routineName);
		this.updatePage(errorMessage);
		if ((errorMessage == null) && checkOthers) {
			this.validate(this.routineNameCtrl);
		}
		return errorMessage == null;		
	}
	
	private void handleServerChanged() {
		if (! this.overrideCtrl.getSelection()) {
			this.updateProject(this.serversCtrl.getSelectionIndex());
			this.validateProjectCtrl(true);
		}
	}
	
	private void handleOverrideSwitch() {
		if (this.overrideCtrl.getSelection()) {
			IProject[] projects = getProjects();
			this.updateProjectFromOverride(projects);
		} else {
			int serverIndex = this.serversCtrl.getSelectionIndex();
			IProject[] projects = getProjectsAssignedToServer(serverIndex);
			this.updateProjectFromOverride(projects);			
		}
	}
	
	private void updateProjectFromOverride(IProject[] projects) {
		String currentProjectName = this.projectCtrl.getText();
		this.projectCtrl.removeAll();
		int index = 0;
		int selectionIndex = -1;
		for (IProject project : projects) {
			String name = project.getName();
			this.projectCtrl.add(name);
			if (name.equals(currentProjectName)) {
				selectionIndex = index;
			}
			++index;
		}
		if (selectionIndex >= 0) {
			this.projectCtrl.select(selectionIndex);				
		} else {
			this.updateProject(projects);
		}
		this.validateProjectCtrl(true);
	}
	
	private void handleProjectChanged() {
		this.validateProjectCtrl(true);
	}
	
	private void disableAll(String errorMessage) {
		this.setErrorMessage(errorMessage);
		Composite parent = this.routineNameCtrl.getParent();
		for (Control c : parent.getChildren()) {
			c.setEnabled(false);
		}
	}
		
	private Combo addCombo(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
				
		Combo c = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		c.setLayoutData(gd);	
		return c;
	}
	
	private void validate(Control source) {
		if (source != this.routineNameCtrl) {
			if (! this.validateRoutineNameCtrl(false)) return;
		}
		
		if (source != this.projectCtrl) {
			if (! this.validateProjectCtrl(false)) return;
		}

		this.updatePage(null);
	}

	private boolean validateProjectCtrl(boolean checkOthers) {
		String projectName = this.projectCtrl.getText();
		if (projectName.isEmpty()) {				
			if (this.projectCtrl.getItemCount() > 0) {
				this.updatePage("No project is selected.");				
			} else {
				if (this.overrideCtrl.getSelection()) {
					this.updatePage("No project found in workspace.");
				} else {
					this.updatePage("No project found for the server.");
				}
			}
			return false;
		}
		if (checkOthers) {
			this.validate(this.projectCtrl);
		}
		return true;		
	}
	
	
	private void updatePage(String errorMessage) {
		if (errorMessage == null) {
			this.routineName = this.routineNameCtrl.getText();
			this.project = this.getProjectFromCtrl();
			this.serverData = this.getServerDatafromCtrl();
			
			this.setErrorMessage(null);
			this.setPageComplete(true);						
		} else {
			this.routineName = null;
			this.project = null;
			this.serverData = null;

			this.setErrorMessage(errorMessage);
			this.setPageComplete(false);
		}
	}
	
	private IProject getProjectFromCtrl() {
		String projectName = this.projectCtrl.getText();
		if (projectName.isEmpty()) {
			return null;
		} else {
			IWorkspaceRoot wr = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = wr.getProject(projectName);
			return project;
		}
	}

	private ServerData getServerDatafromCtrl() {
		int index = this.serversCtrl.getSelectionIndex(); 
		@SuppressWarnings("unchecked")
		List<ServerData> serverDataList = (List<ServerData>) this.serversCtrl.getData();		
		ServerData serverData = serverDataList.get(index);
		return serverData;		
	}
	
	public String getRoutineName() {
		return this.routineName;
	}

	public IProject getProject() {
		return this.project;
	}
	
	public ServerData getServerData() {
		return this.serverData;
	}
}
