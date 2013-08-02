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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicCodeInfo {
	private Set<String> globals;
	private Set<String> filemanGlobals = new HashSet<String>();
	private Set<String> filemanCalls = new HashSet<String>();
	
	private int indirectionCount;
	private int writeCount;
	private int readCount;
	private int executeCount;
		
	public void mergeGlobals(Set<String> globals) {
		if (this.globals == null) {
			this.globals = new HashSet<String>(globals);	
		} else {
			this.globals.addAll(globals);		
		}
	}
	
	public void mergeFilemanGlobals(Set<String> filemanGlobals) {
		if (this.filemanGlobals == null) {
			this.filemanGlobals = new HashSet<String>(filemanGlobals);	
		} else {
			this.filemanGlobals.addAll(filemanGlobals);		
		}
	}
	
	public void mergeFilemanCalls(Set<String> filemanCalls) {
		if (this.filemanCalls == null) {
			this.filemanCalls = new HashSet<String>(filemanCalls);	
		} else {
			this.filemanCalls.addAll(filemanCalls);		
		}
	}
	
	private List<String> getIO(Set<String> source) {
		if (source == null) {
			return Collections.emptyList();
		} else {
			List<String> result = new ArrayList<String>(source);
			Collections.sort(result);
			return result;
		}
		
	}
	
	public void incrementIndirectionCount(int count) {
		this.indirectionCount += count;
	}
	
	public void incrementWriteCount(int count) {
		this.writeCount += count;
	}
	
	public void incrementReadCount(int count) {
		this.readCount += count;
	}
	
	public void incrementExecuteCount(int count) {
		this.executeCount += count;
	}
	
	public List<String> getGlobals() {
		return getIO(this.globals);
	}
	
	public List<String> getFilemanGlobals() {
		return getIO(this.filemanGlobals);
	}
	
	public List<String> getFilemanCalls() {
		return getIO(this.filemanCalls);
	}
	
	public int getIndirectionCount() {
		return this.indirectionCount;
	}
	
	public int getWriteCount() {
		return this.writeCount;
	}
	
	public int getReadCount() {
		return this.readCount;
	}
	
	public int getExecuteCount() {
		return this.executeCount;
	}
}

