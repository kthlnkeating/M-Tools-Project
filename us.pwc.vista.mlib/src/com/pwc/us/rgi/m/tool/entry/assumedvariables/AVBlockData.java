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

package com.pwc.us.rgi.m.tool.entry.assumedvariables;

import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.m.parsetree.Local;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.entry.BlockData;

class AVBlockData extends BlockData<IndexedFanout> {
	private String[] formals;
	private Map<String, Integer> formalsMap;
	private Map<String, Integer> newedLocals = new HashMap<String, Integer>();
	private Map<String, CodeLocation> assumedLocals = new HashMap<String, CodeLocation>();
	
	public AVBlockData(EntryId entryId) {
		super(entryId);
	}
	
	public void setFormals(String[] formals) {
		this.formals = formals;
		if (formals != null) {
			this.formalsMap = new HashMap<String, Integer>(formals.length*2);
			int index = 0;
			for (String formal : formals) {
				formalsMap.put(formal, index);
				++index;
			}
		} else {
			this.formalsMap=null;
		}
	}
	
	public String[] getFormals() {
		return this.formals;
	}
	
	public void addNewed(int index, Local local) {
		String label = local.getName().toString();
		if (! this.newedLocals.containsKey(label)) {
			this.newedLocals.put(label, index);
		}
	}		
	
	public void addLocal(Local local, CodeLocation location) {
		String label = local.getName().toString();
		if ((this.formalsMap == null) || (! this.formalsMap.containsKey(label))) {
			if (! this.newedLocals.containsKey(label)) {
				if (! this.assumedLocals.containsKey(label)) {				
					this.assumedLocals.put(label, location);
				}
			}
		}
	}
	
	public Integer getAsFormal(String name) {
		if (this.formalsMap != null) {
			return this.formalsMap.get(name);			
		} else {
			return null;
		}
	}
	
	private boolean isNewed(String name, int sourceIndex) {
		Integer index = this.newedLocals.get(name);
		if (index == null) {
			return false;
		} else if (index.intValue() > sourceIndex) {
			return false;
		}
		return true;
	}
	
	public boolean isDefined(String name, int sourceIndex) {
		if ((this.formalsMap != null) && (this.formalsMap.containsKey(name))) {
			return true;
		}
		return this.isNewed(name, sourceIndex);
	}
	
	public Map<String, Integer> getNewedLocals() {
		return this.newedLocals;
	}
	
	public Map<String, CodeLocation> getAssumedLocals() {
		return this.assumedLocals;
	}
}
