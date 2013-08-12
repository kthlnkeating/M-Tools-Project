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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


public class FilesToolExecuter extends RoutinesToolExecuter {
	public FilesToolExecuter(MToolWrap wrap, ExecutionEvent event, List<String> routines) {
		super(wrap, event, routines);
	}
	
	public static void run(ExecutionEvent event, MToolWrap wrap) {
		List<String> routineNames = new ArrayList<String>();
		IProject project = null;
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		@SuppressWarnings("rawtypes")
		Iterator x = selection.iterator();
		while (x.hasNext()) {
			IFile file = (IFile) x.next();
			project = file.getProject();
			String fileName = file.getName();
			String routineName = fileName.substring(0, fileName.length()-2);
			routineNames.add(routineName);
		}
				
		ToolExecuter executer = new RoutinesToolExecuter(wrap, event, routineNames);
		executer.run(project);
	}	
}
