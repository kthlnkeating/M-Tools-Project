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

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;

import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;


public class RoutinesToolExecuter extends ToolExecuter {
	private List<String> routines;
	
	public RoutinesToolExecuter(MToolWrap wrap, ExecutionEvent event, List<String> routines) {
		super(wrap, event);
		this.routines = routines;
	}
	
	@Override
	public ToolResult getResult(IProject project, ParseTreeSupply pts) {
		MToolWrap w = this.getMToolWrap();
		return w.getRoutinesResult(project, pts, this.routines);
	}	
}
