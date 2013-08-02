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

package com.pwc.us.rgi.m.tool.entry.assumedvariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class AssumedVariables implements ToolResult {
	private Map<String, CodeLocation> data;

	public AssumedVariables(Map<String, CodeLocation> data) {
		this.data = data;
	}
	
	public CodeLocation get(String name) {
		return this.data.get(name);
	}
	
	public Set<String> toSet() {
		return this.data.keySet();
	}

	@Override
	public boolean isEmpty() {
		return (this.data == null) || (this.data.size() ==0);
	}
				
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		if (flags.getShowDetail(false)) {
			List<String> tbw = new ArrayList<String>();
			for (String name : this.toSet()) {
				CodeLocation location = this.get(name);
				String out = " " + name + location.toString();
				tbw.add(out);
			}
			t.writeSortedFormatted("ASSUMED", tbw);
		} else {
			t.writeSortedFormatted("ASSUMED", this.toSet());
		}
	}

}
