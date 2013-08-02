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

package com.pwc.us.rgi.m.tool.routine;

import java.io.IOException;
import java.util.Collection;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.ToolResultPiece;
import com.pwc.us.rgi.output.Terminal;

public class CollectionAsToolResult<T extends ToolResultPiece> implements ToolResult {
	private EntryId entryUnderTest;
	private Collection<T> collection;
	
	public CollectionAsToolResult(EntryId entryUnderTest, Collection<T> collection) {
		this.entryUnderTest = entryUnderTest;
		this.collection = collection;
	}
	
	public Collection<T> getCollection() {
		return this.collection;
	}
	
	@Override
	public boolean isEmpty() {
		return this.collection.isEmpty();
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		for (T r : this.collection) {
			r.write(t, this.entryUnderTest, flags);
		}		
	}	
}
