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

package us.pwc.vista.eclipse.tools.core;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeFiles;
import com.pwc.us.rgi.m.tool.SourceCodeToParseTreeAdapter;
import com.pwc.us.rgi.m.tool.ToolResult;

import us.pwc.vista.eclipse.core.CommonUtil;
import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.tools.VistAToolsPlugin;
import us.pwc.vista.eclipse.tools.util.MRAParamSupply;

public abstract class ToolExecuter {
	private IWorkbenchWindow window;
	private String name;
	private MToolWrap wrap;

	public ToolExecuter(MToolWrap command, ExecutionEvent event) {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		try {
			this.name = event.getCommand().getName();
		} catch (Exception e) {
			this.name = null;
		}
		this.wrap = command;
	}
	
	protected MToolWrap getMToolWrap() {
		return this.wrap;
	}
	
	protected abstract ToolResult getResult(IProject project, ParseTreeSupply pts);
	
	public void run(final IProject project) {
		final ToolExecuter thiz = this;

		Job job = new Job("MTools Fetch Report") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {				
				try {
					String backupDirectory = VistACorePrefs.getServerBackupDirectory(project);					
					final SourceCodeFiles scf = MRAParamSupply.getSourceCodeFiles(project, backupDirectory);
					SourceCodeToParseTreeAdapter pts = new SourceCodeToParseTreeAdapter(scf);				
					final ToolResult result = thiz.getResult(project, pts);
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							try {
								thiz.wrap.writeResult(project, thiz.window, result, scf);
							} catch (Throwable t) {
								CommonUtil.showException(VistAToolsPlugin.PLUGIN_ID, name, t);
							}
						}
					});
				} catch (final Throwable t) {
					CommonUtil.showException(VistAToolsPlugin.PLUGIN_ID, name, t);
				}
				return Status.OK_STATUS;
			}		
		};
		job.setUser(true);
		job.schedule();
	}
}
