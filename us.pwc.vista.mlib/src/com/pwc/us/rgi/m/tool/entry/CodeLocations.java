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

package com.pwc.us.rgi.m.tool.entry;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class CodeLocations extends BlockData<Fanout> implements Serializable, ToolResult {
	private static final long serialVersionUID = 1L;

	private List<CodeLocation> codeLocations;

	public CodeLocations(EntryId entryId) {
		super(entryId);
	}
	
	public void add(CodeLocation codeLocation) {
		if (this.codeLocations == null) {
			this.codeLocations = new ArrayList<CodeLocation>();
		} 
		this.codeLocations.add(codeLocation);
	}
	
	public List<CodeLocation> getCodeLocations() {
		if (this.codeLocations == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.codeLocations);
		}
	}
	
	public boolean isIdenticalTo(CodeLocation[] rhs) {
		if (rhs.length == (this.codeLocations == null ? 0 : this.codeLocations.size())) {
			if (rhs.length == 0) return true;			
			int i = 0;
			for (CodeLocation c : this.codeLocations) {
				if (! rhs[i].equals(c)) return false;
				++i;
			}
			return true;
		}		
		return false;
	}

	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		List<CodeLocation> cl = this.getCodeLocations();
		if (cl == null) {
			t.writeEOL("  --");				
		} else {
			for (CodeLocation c : cl) {
				t.writeEOL("  " + c.toString());
			}
		}
	}	
	
	@Override
	public boolean isEmpty() {
		return false;
	}
}
