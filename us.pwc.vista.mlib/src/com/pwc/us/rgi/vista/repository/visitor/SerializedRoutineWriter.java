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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.vista.repository.RepositoryVisitor;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;

public class SerializedRoutineWriter extends RepositoryVisitor {
	private final static Logger LOGGER = Logger.getLogger(SerializedRoutineWriter.class.getName());

	private String outputDirectory;
	private int packageCount;
	private List<String> nameRegexs;
	
	public SerializedRoutineWriter(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void addRoutineNameFilter(String regex) {
		if (this.nameRegexs == null) {
			this.nameRegexs = new ArrayList<String>();
		}
		this.nameRegexs.add(regex);
	}
	
	private void writeObject(String fileName, Serializable object) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
	}
	
	private void writeObject(Routine routine, Serializable object, String extension) {
		String routineName = routine.getName();
		Path path = Paths.get(this.outputDirectory, routineName + extension);
		String fileName = path.toString();
		try {
			this.writeObject(fileName, object);
		} catch (IOException ioException) {
			String msg = "Unable to write object to file " + fileName;
			LOGGER.log(Level.SEVERE, msg, ioException);
		}
	}
	
	private boolean matches(String name) {
		for (String nameRegex : this.nameRegexs) {
			if (name.matches(nameRegex)) return true;
		}
		return false;
	}
	
	@Override
	public void visitRoutine(Routine routine) {
		if ((this.nameRegexs == null) || (this.matches(routine.getName()))) {
			this.writeObject(routine, routine, ".ser");
		}
	}
	
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		++this.packageCount;
		LOGGER.info(String.valueOf(this.packageCount) + ". " + routinePackage.getPackageName() + "...writing");
		super.visitVistaPackage(routinePackage);
		LOGGER.info("..done.\n");
	}

	private boolean testFile() {
		Path path = Paths.get(this.outputDirectory, "test.tst");
		String fileName = path.toString();
		try {
			this.writeObject(fileName, "test");
			Files.delete(path);
			return true;
		} catch (IOException ioException) {
			String msg = "Unable to write object to directory " + this.outputDirectory;
			LOGGER.log(Level.SEVERE, msg, ioException);
			return false;
		}
	}
	
	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		if (testFile()) {
			super.visitRoutinePackages(rps);			
		}
	}
	
	public void visitRoutines(List<Routine> routines) {
		for (Routine r : routines) {
			this.visitRoutine(r);			
		}
	}
}
