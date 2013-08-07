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

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;

import us.pwc.eclipse.vista.command.MToolsCommand;

public class ToolExecuterOnTags extends ToolExecuter {
	private EntryId entryId;
	
	public ToolExecuterOnTags(MToolsCommand command, ExecutionEvent event, EntryId entryId) {
		super(command, event);
		this.entryId = entryId;
	}
		
	@Override
	public ToolResult getResult(IProject project, ParseTreeSupply pts) {
		MToolsCommand c = this.getCommand();
		return c.getResult(project, pts, this.entryId);
	}	
}
