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

package com.pwc.us.rgi.m.parsetree.visitor;

import java.util.HashSet;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Global;
import com.pwc.us.rgi.m.parsetree.Visitor;
import com.pwc.us.rgi.struct.Filter;

public class GlobalRecorder extends Visitor {
	private Set<String> globals = new HashSet<String>();
	private Filter<String> filter;
	
	@Override
	protected void visitGlobal(Global global) {
		super.visitGlobal(global);
		if (this.filter != null) {
			String name = global.getName().toString();
			if (! this.filter.isValid(name)) return;
		}		
		String globalString = global.getAsString();
		this.globals.add(globalString);
	}
	
	public void reset() {
		this.globals = new HashSet<String>(); 		
	}
	
	public void setFilter(Filter<String> filter) {
		this.filter = filter;
	}
	
	public Set<String> getGlobals() {
		return this.globals;
	}
}
