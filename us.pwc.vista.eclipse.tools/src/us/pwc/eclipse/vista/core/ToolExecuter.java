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

package us.pwc.eclipse.vista.core;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeFiles;
import com.pwc.us.rgi.m.tool.SourceCodeToParseTreeAdapter;
import com.pwc.us.rgi.m.tool.ToolResult;

import us.pwc.eclipse.vista.Activator;
import us.pwc.eclipse.vista.command.MToolsCommand;
import us.pwc.eclipse.vista.util.MRAParamSupply;

public abstract class ToolExecuter {
	private IWorkbenchWindow window;
	private Shell shell;
	private MToolsCommand command;

	public ToolExecuter(MToolsCommand command, ExecutionEvent event) {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		this.shell = HandlerUtil.getActiveShell(event);
		this.command = command;
	}
	
	protected MToolsCommand getCommand() {
		return this.command;
	}
	
	protected abstract ToolResult getResult(IProject project, ParseTreeSupply pts);
	
	public static void handleException(Shell shell, Throwable t) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, t.getMessage(), t);
		Activator.getDefault().getLog().log(status);
		MessageDialog.openInformation(shell, "M Tools", t.getMessage());		
	}
	
	public void run(final IProject project) {
		final ToolExecuter thiz = this;

		Job job = new Job("MTools Fetch Report") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {				
				try {
					final SourceCodeFiles scf = MRAParamSupply.getSourceCodeFiles(project);
					SourceCodeToParseTreeAdapter pts = new SourceCodeToParseTreeAdapter(scf);				
					final ToolResult result = thiz.getResult(project, pts);
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							try {
								thiz.command.writeResult(project, thiz.window, result, scf);
							} catch (Throwable t) {
								handleException(thiz.shell, t);								
							}
						}
					});
				} catch (final Throwable t) {
					handleException(thiz.shell, t);
				}
				return Status.OK_STATUS;
			}		
		};
		job.setUser(true);
		job.schedule();
	}
}
