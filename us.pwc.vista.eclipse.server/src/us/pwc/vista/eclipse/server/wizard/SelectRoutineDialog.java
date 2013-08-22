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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

import us.pwc.vista.eclipse.core.resource.FileSearchVisitor;

public class SelectRoutineDialog extends WizardDialog {
	private IFile fileHandle;
	private ServerData serverData;
	
	private SelectRoutineDialog(Shell shell, SelectRoutineWizard wizard) {
		super(shell, wizard);
	}
	
	@Override
	protected void nextPressed() {
		SelectRoutineWizard wizard = (SelectRoutineWizard) this.getWizard();
		IProject project = wizard.getProject();
		String routineName = wizard.getRoutineName();
		if ((project == null) || (routineName == null) || routineName.isEmpty()) {
			wizard.setNextError("Invalid routine name and/or project.");
		} else {
			this.setNextPageOffExistingFile(wizard, project, routineName); 
		}
	}
	
	private void setNextPageOffExistingFile(final SelectRoutineWizard wizard, final IProject project, final String routineName) {
		Job job = new Job("Find Existing File") {
			@Override
			protected IStatus run(IProgressMonitor progressMonitor) {				
				try {
					progressMonitor.beginTask("Searching file", IProgressMonitor.UNKNOWN);
					FileSearchVisitor fsv = new FileSearchVisitor(routineName + ".m", null);
					project.accept(fsv, IContainer.NONE);
					final IFile file = fsv.getFile();
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							wizard.setNextPage(file);
							SelectRoutineDialog.super.nextPressed();
						}
					});
				} catch (final CoreException coreException) {
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							wizard.setNextError(coreException.getMessage());
						}
					});
				}
				progressMonitor.done();
				return Status.OK_STATUS;
			}		
		};
		job.setUser(true);
		job.schedule();
	}

	@Override
	protected void finishPressed() {
		super.finishPressed();
		SelectRoutineWizard wizard = (SelectRoutineWizard) this.getWizard();
		this.fileHandle = wizard.getFileHandle();
		this.serverData = wizard.getServerData();
	}
	
	public static SelectRoutineDialog getInstance(IWorkbench workbench, Shell shell) {
		SelectRoutineWizard wizard = new SelectRoutineWizard();
		wizard.init(workbench, null);
		wizard.setForcePreviousAndNextButtons(true);
		SelectRoutineDialog dialog = new SelectRoutineDialog(shell, wizard);
		return dialog;
	}
	
	public IFile getFileHandle() {
		return this.fileHandle;
	}

	public ServerData getServerData() {
		return this.serverData;
	}
}
