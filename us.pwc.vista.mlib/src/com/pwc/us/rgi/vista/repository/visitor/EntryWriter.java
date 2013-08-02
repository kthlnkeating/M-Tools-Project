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
import java.util.List;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.output.FileTerminal;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class EntryWriter extends RepositoryVisitor {
	private FileTerminal fileWrapper;
	private List<String> nameRegexs;
	private List<EntryId> allEntries = new ArrayList<EntryId>();
	
	public EntryWriter(FileTerminal fileWrapper) {
		this.fileWrapper = fileWrapper;
	}
	
	public void addRoutineNameFilter(String regex) {
		if (this.nameRegexs == null) {
			this.nameRegexs = new ArrayList<String>();
		}
		this.nameRegexs.add(regex);
	}
	
	private boolean matches(String name) {
		for (String nameRegex : this.nameRegexs) {
			if (name.matches(nameRegex)) return true;
		}
		return false;
	}
	
	@Override
	protected void visitRoutine(Routine routine) {
		if ((this.nameRegexs == null) || (this.matches(routine.getName()))) {
			List<EntryId> routineEntries = routine.getEntryIdList();
			this.allEntries.addAll(routineEntries);
		}
	}
	
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		routinePackage.acceptSubNodes(this);
	}

	private void writeEntries() throws IOException {
		List<EntryId> entries = new ArrayList<EntryId>(this.allEntries);
		Collections.sort(entries);
		for (EntryId entry : entries) {
			String entryString = entry.toString();
			this.fileWrapper.writeEOL(entryString);
		}		
	}
	
	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		try  {
			super.visitRoutinePackages(rps);
			this.writeEntries();
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}
	}
	
	public void writeForRoutines(List<Routine> routines) {
		try {
			for (Routine r : routines) {
				this.visitRoutine(r);
			}
			this.writeEntries();
			this.fileWrapper.stop();
		} catch (IOException e) {
			MRALogger.logError("Unable to write result", e);
		}		
	}
}