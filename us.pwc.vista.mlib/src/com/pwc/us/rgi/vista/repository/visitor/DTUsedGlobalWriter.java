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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.visitor.GlobalRecorder;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class DTUsedGlobalWriter extends RepositoryVisitor {
	private static class OutputLine implements Comparable<OutputLine> {
		public String routineName;
		public String global;
		public String pkg;
		
		@Override
		public int compareTo(OutputLine rhs) {
			int result = this.routineName.compareTo(rhs.routineName);
			if (result == 0) {
				result = this.global.compareTo(rhs.global);
			}
			return result;
		}
	}
		
	private RepositoryInfo repositoryInfo;
	private FileTerminal fileWrapper;
	private GlobalRecorder recorder = new GlobalRecorder();
	private String lastPackageName;
	private List<OutputLine> outputLines = new ArrayList<OutputLine>();
	private Set<String> selectedVPs;	
	
	public DTUsedGlobalWriter(RepositoryInfo repositoryInfo, FileTerminal fileWrapper) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
	}
		
	@Override
	public void visitRoutine(Routine routine) {
		this.recorder.reset();
		routine.accept(recorder);
		ArrayList<String> result = new ArrayList<String>(recorder.getGlobals());
		Collections.sort(result);
		for (String r : result) {
			String g = r;
			String globalPkgName = this.repositoryInfo.getPackageFromGlobal(g);
			if (globalPkgName == null) {
				g = g.split("\\(")[0] + "(";
				globalPkgName = this.repositoryInfo.getPackageFromGlobal(g);
			}
			if ((globalPkgName != null) && this.selectedVPs.contains(globalPkgName)) {
				OutputLine ol = new OutputLine();
				ol.routineName = routine.getName();
				ol.global = r;
				ol.pkg = this.lastPackageName;
				this.outputLines.add(ol);
			}
		}
	}
	
	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		this.selectedVPs = new HashSet<String>();
		List<VistaPackage> vps = rps.getPackages();
		for (VistaPackage vp : vps) {
			String name = vp.getPackageName();
			this.selectedVPs.add(name);
		}		
		VistaPackages vpksall = new VistaPackages(this.repositoryInfo.getAllPackages());
		for (VistaPackage vp : vpksall.getPackages()) {
			String name = vp.getPackageName();
			if (! this.selectedVPs.contains(name)) {
				this.lastPackageName = name;
				this.visitVistaPackage(vp);
			}
		}
		Collections.sort(this.outputLines);
		try  {
			for (OutputLine ol : this.outputLines) {
				this.fileWrapper.write("Routine ");
				this.fileWrapper.write(ol.routineName);
				this.fileWrapper.write(": ");
				this.fileWrapper.write(ol.global);
				this.fileWrapper.write(" (");
				this.fileWrapper.write(ol.pkg);
				this.fileWrapper.write(")");
				this.fileWrapper.writeEOL();				
			}
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
}
