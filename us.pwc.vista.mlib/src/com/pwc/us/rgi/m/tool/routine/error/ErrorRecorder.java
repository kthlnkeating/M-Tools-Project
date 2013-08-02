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

package com.pwc.us.rgi.m.tool.routine.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.DoBlock;
import com.pwc.us.rgi.m.parsetree.Entry;
import com.pwc.us.rgi.m.parsetree.ErrorNode;
import com.pwc.us.rgi.m.parsetree.InnerEntryList;
import com.pwc.us.rgi.m.parsetree.Line;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.visitor.LocationMarker;
import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.vista.tools.ErrorExemptions;

public class ErrorRecorder extends LocationMarker {
	private ErrorsByLabel allErrors;
	private List<ErrorWithLineIndex> labelErrors;
	private String routineName;
	private ErrorExemptions exemptions;
	private Set<LineLocation> lineExemptions;
	private boolean onlyFatal;
	private int index;
	
	public ErrorRecorder(ErrorExemptions exemptions) {
		this.exemptions = exemptions;
	}
	
	public ErrorRecorder() {
	}
	
	public void setOnlyFatal(boolean b) {
		this.onlyFatal = b;
	}

	private void addError(MError error) {
		if ((! this.onlyFatal) || (error.isFatal())) {
			LineLocation location = this.getLastLocation();
			if ((this.lineExemptions == null) || (! this.lineExemptions.contains(location))) {
				ErrorWithLineIndex element = new ErrorWithLineIndex(error, this.index);  
				this.labelErrors.add(element);
			}
		}
	}
	
	@Override
	protected void visitErrorNode(ErrorNode errorNode) {		
		MError error = errorNode.getError();
		this.addError(error);
	}
	
	@Override
	protected void visitDoBlock(DoBlock doBlock) {
		InnerEntryList block = doBlock.getEntryList();
		if (block == null) {
			MError error = new MError(MError.ERR_NO_DO_BLOCK);
			this.addError(error);
		}
		super.visitDoBlock(doBlock);
	}
	
	@Override
	protected void visitLine(Line line) {
		this.index = line.getLineIndex();
		super.visitLine(line);
	}
	
	@Override
	protected void visitEntry(Entry entry) {
		this.labelErrors = new ArrayList<ErrorWithLineIndex>();
		super.visitEntry(entry);
		this.allErrors.put(entry.getName(), this.labelErrors);
	}
			
	@Override
	protected void visitRoutine(Routine routine) {
		this.allErrors = new ErrorsByLabel();
		this.routineName = routine.getName();
		if (this.exemptions == null) {
			super.visitRoutine(routine);			
		} else if (! this.exemptions.containsRoutine(this.routineName)) {			
			this.lineExemptions = this.exemptions.getLines(this.routineName);
			super.visitRoutine(routine);
		} 
	}
	
	public ErrorsByLabel getErrors(Routine routine) {
		routine.accept(this);
		return this.allErrors;
	}
}
