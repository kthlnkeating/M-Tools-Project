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

package com.pwc.us.rgi.m.tool;

import java.util.Collection;

import com.pwc.us.rgi.m.parsetree.Entry;
import com.pwc.us.rgi.m.parsetree.InnerEntryList;
import com.pwc.us.rgi.m.parsetree.Line;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.Visitor;

public abstract class ResultsByLabelVisitor<T, U extends Collection<T>> extends  Visitor  {
	private ResultsByLabel<T, U> resultsByLabel;
	private U results;
	
	private int index;
	private InnerEntryList lastInnerEntryList;

	protected void addResult(T result) {
		this.results.add(result);
	}
	
	protected int getIndex() {
		return this.index;
	}
	
	protected boolean isConsidered(InnerEntryList entryList) {
		return entryList == this.lastInnerEntryList;	
	}
	
	@Override
	protected void visitInnerEntryList(InnerEntryList entryList) {
		if (entryList != this.lastInnerEntryList) {
			this.lastInnerEntryList = entryList;
			super.visitInnerEntryList(entryList);
		}
	}
	
	@Override
	protected void visitLine(Line line) {
		this.index = line.getLineIndex();
		super.visitLine(line);
	}
	
	@Override
	protected void visitEntry(Entry entry) {
		this.results = this.resultsByLabel.getNewResultsInstance();
		super.visitEntry(entry);
		this.resultsByLabel.put(entry.getName(), this.results);
	}
	
	protected abstract ResultsByLabel<T, U> getNewResultsByLabelInstance();
	
	@Override
	protected void visitRoutine(Routine routine) {
		this.resultsByLabel = this.getNewResultsByLabelInstance();
		super.visitRoutine(routine);
	}
	
	public ResultsByLabel<T, U> getResults(Routine routine) {
		routine.accept(this);
		return this.resultsByLabel;
	}
}
