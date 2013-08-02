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

package com.pwc.us.rgi.vista.repository.visitor;

import java.io.IOException;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.visitor.CacheOccuranceRecorder;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class CacheOccuranceWriter extends RepositoryVisitor {
	private FileTerminal fileWrapper;
	
	public CacheOccuranceWriter(FileTerminal fileWrapper) {
		this.fileWrapper = fileWrapper;
	}
		
	@Override
	protected void visitRoutine(Routine routine) {
		CacheOccuranceRecorder cor = new CacheOccuranceRecorder();
		cor.visitRoutine(routine);
		if (cor.getNumOccurance() > 0) {
			try {
				this.fileWrapper.writeEOL(routine.getName());
			} catch (IOException e) {
				MRALogger.logError("Unable to write result", e);
			}
		}
	}
	
	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		super.visitRoutinePackages(rps);
		try {
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
}