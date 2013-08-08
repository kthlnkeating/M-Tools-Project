//---------------------------------------------------------------------------
//Copyright 2013 PwC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.m.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.output.Terminal;

public class ToolResultCollection implements ToolResult {
	private List<ToolResult> list = new ArrayList<ToolResult>();
	
	public void add(ToolResult toolResult) {
		this.list.add(toolResult);
	}
	
	@Override
	public boolean isEmpty() {
		if (this.list.isEmpty()) return true;		
		for (ToolResult tr : this.list) {
			if (! tr.isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		for (ToolResult tr : this.list) {
			if (! tr.isEmpty()) {
				tr.write(t, flags);
			}
		}
	}
}
