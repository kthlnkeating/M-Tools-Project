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

package com.pwc.us.rgi.m.tool.entry.localassignment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;

public class LocalAssignmentToolParams extends CommonToolParams {
	private Set<String> locals = new HashSet<String>(); 
	
	public LocalAssignmentToolParams(ParseTreeSupply pts) {
		super(pts);
	}
	
	public void addLocal(String local) {
		this.locals.add(local);
	}
	
	public void addLocals(Collection<String> locals) {
		this.locals.addAll(locals);
	}
	
	public Set<String> getLocals() {
		return this.locals;
	}
}
