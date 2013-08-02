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

package com.pwc.us.rgi.m.tool.entry.basiccodeinfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.tool.entry.BlockData;

public class CodeInfo extends BlockData<Fanout> {
	private String[] formals;
	private Map<String, Integer> formalsMap;
	private Set<String> globals = new HashSet<String>();
	private Set<String> filemanGlobals = new HashSet<String>();
	private Set<String> filemanCalls = new HashSet<String>();
	
	private int indirectionCount;
	private int writeCount;
	private int readCount;
	private int executeCount;
	
	public CodeInfo(EntryId entryId) {
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
	
	public void addGlobal(String value) {
		this.globals.add(value);
	}

	public Set<String> getGlobals() {
		return this.globals;
	}
	
	public void addFilemanGlobal(String value) {
		this.filemanGlobals.add(value);
	}

	public Set<String> getFilemanGlobals() {
		return this.filemanGlobals;
	}
	
	public void addFilemanCalls(String value) {
		this.filemanCalls.add(value);
	}

	public Set<String> getFilemanCalls() {
		return this.filemanCalls;
	}
	
	public Integer getAsFormal(String name) {
		if (this.formalsMap != null) {
			return this.formalsMap.get(name);			
		} else {
			return null;
		}
	}
	
	public void incrementIndirection() {
		++this.indirectionCount;
	}
	
	public int getIndirectionCount() {
		return this.indirectionCount;
	}
	
	public void incrementWrite() {
		++this.writeCount;
	}
	
	public int getWriteCount() {
		return this.writeCount;
	}
	
	public void incrementRead() {
		++this.readCount;
	}
	
	public int getReadCount() {
		return this.readCount;
	}
	
	public void incrementExecute() {
		++this.executeCount;
	}
	
	public int getExecuteCount() {
		return this.executeCount;
	}
}
