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
import java.util.List;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.visitor.FilemanCallRecorder;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class DTFilemanCallWriter extends RepositoryVisitor {
	private RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	private int pkgCount;
	private String lastPackageName;
	private boolean packageFirst;
	
	public DTFilemanCallWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
	}
		
	@Override
	public void visitRoutine(Routine routine) {
		FilemanCallRecorder recorder = new FilemanCallRecorder(this.repositoryInfo);
		routine.accept(recorder);
		List<String> filemanGlobals = recorder.getFilemanGlobals();
		List<String> filemanCalls = recorder.getFilemanCalls();
		try {
			if ((filemanCalls.size() > 0) || (filemanGlobals.size() > 0)) {	
				if (this.packageFirst) {
					this.packageFirst = false;
					++this.pkgCount;
					this.fileWrapper.writeEOL("--------------------------------------------------------------");
					this.fileWrapper.writeEOL();
					this.fileWrapper.write(String.valueOf(this.pkgCount));
					this.fileWrapper.write(". PACKAGE NAME: ");
					this.fileWrapper.writeEOL(this.lastPackageName);
					this.fileWrapper.writeEOL();
				}			
				this.fileWrapper.writeEOL(" " + routine.getName());
				this.fileWrapper.getTerminalFormatter().setTitleWidth(17);
				this.fileWrapper.writeFormatted("Globals", filemanGlobals);
				this.fileWrapper.writeFormatted("FileMan calls", filemanCalls);
				this.fileWrapper.writeEOL();
			}
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
	
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		String prefix = routinePackage.getDefaultPrefix();
		if ((! prefix.equals("DI")) && (! prefix.equals("XU")) && (! prefix.equals("UNCATEGORIZED"))) {		
			this.lastPackageName = routinePackage.getPackageName();
			this.packageFirst = true;
			super.visitVistaPackage(routinePackage);
			if (! this.packageFirst) {
				try {
					this.fileWrapper.writeEOL("--------------------------------------------------------------");
					this.fileWrapper.writeEOL();				
				} catch (IOException e) {
					MRALogger.logError("Unable to write result", e);
				}
			}
		}
	}

	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		try {			
			super.visitRoutinePackages(rps);
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
}
