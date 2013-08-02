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
import java.util.ArrayList;
import java.util.Collections;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.visitor.GlobalRecorder;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.struct.ExcludeValueFilter;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class DTUsesGlobalWriter extends RepositoryVisitor {
	private RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	private GlobalRecorder recorder = new GlobalRecorder();
	private String lastPackageName;
	
	public DTUsesGlobalWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
		ExcludeValueFilter<String> filter = new ExcludeValueFilter<String>();
		filter.add("TMP");
		filter.add("XTMP");
		filter.add("UTILITY");
		filter.add("%ZOSF");
		this.recorder.setFilter(filter);
	}
		
	@Override
	public void visitRoutine(Routine routine) {
		this.recorder.reset();
		routine.accept(recorder);
		ArrayList<String> result = new ArrayList<String>(recorder.getGlobals());
		Collections.sort(result);
		String routinePrefix = "Routine " + routine.getName() + ": ";
		try {
			for (String r : result) {
				String g = r;
				this.fileWrapper.write(routinePrefix);
				this.fileWrapper.write(r);
				this.fileWrapper.write(" ");
				String packageName = this.repositoryInfo.getPackageFromGlobal(g);
				if (packageName == null) {
					g = g.split("\\(")[0] + "(";
					packageName = this.repositoryInfo.getPackageFromGlobal(g);
				}
				if (packageName == null) {
					this.fileWrapper.write("(Dependency Unknown)");				
				} else if (! packageName.equalsIgnoreCase(this.lastPackageName)) {
					String fileName = this.repositoryInfo.getFileNameFromGlobal(g);
					this.fileWrapper.write("(Dependency to ");
					this.fileWrapper.write(packageName);
					this.fileWrapper.write(", ");
					if ((fileName != null) && (! fileName.isEmpty())) {
						this.fileWrapper.write(fileName);					
					} else {
						this.fileWrapper.write("Unknown");
					}
					this.fileWrapper.write(" File)");					
				}
				this.fileWrapper.writeEOL();
			}
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
	
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		try {
			this.fileWrapper.writeEOL("Directly Used Globals By " + routinePackage.getPackageName());
			this.fileWrapper.writeEOL();
			this.lastPackageName = routinePackage.getPackageName();
			super.visitVistaPackage(routinePackage);
			this.fileWrapper.writeEOL();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
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
