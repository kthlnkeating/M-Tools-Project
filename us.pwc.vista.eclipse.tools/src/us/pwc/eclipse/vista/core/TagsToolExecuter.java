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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;


public class TagsToolExecuter extends ToolExecuter {
	private String routineName;
	private List<String> tags;
	
	public TagsToolExecuter(MToolWrap wrap, ExecutionEvent event, String routineName, List<String> tags) {
		super(wrap, event);
		this.routineName = routineName;
		this.tags = tags;
	}
		
	@Override
	public ToolResult getResult(IProject project, ParseTreeSupply pts) {
		MToolWrap w = this.getMToolWrap();
		return w.getTagsResult(project, pts, routineName, this.tags);
	}
	
	public static void run(ExecutionEvent event, MToolWrap wrap) {
		List<String> tags = new ArrayList<String>();
		
		IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		IFile file = (IFile) input.getAdapter(IFile.class);
		IProject project = file.getProject();
		String fileName = file.getName();
		String routineName = fileName.substring(0, fileName.length()-2);
			
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		@SuppressWarnings("rawtypes")
		Iterator x = selection.iterator();
		while (x.hasNext()) {
			Object object = x.next();
			String tag = object.toString();
			tags.add(tag);
		}
		
		ToolExecuter executer = new TagsToolExecuter(wrap, event, routineName, tags);
		executer.run(project);		
	}
}
