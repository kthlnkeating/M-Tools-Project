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

package com.pwc.us.rgi.vista.repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.filter.LocalFanoutFilter;
import com.pwc.us.rgi.m.parsetree.filter.PackageFanoutFilter;
import com.pwc.us.rgi.struct.AndFilter;
import com.pwc.us.rgi.struct.Filter;


public class VistaPackage  implements RepositoryNode {
	public static class FileInfo {
		private String number;
		private String name;

		public FileInfo(String number, String name) {
			this.number = number;
			this.name = name;
		}
		
		public String getNumber() {
			return number;
		}
		
		public String getName() {
			return this.name;
		}
	}
		
	private String packageName;
	private String directoryName;
	private List<String> prefixes;
	private List<String> exceptionPrefixes;
	private List<FileInfo> files;
	public RoutineFactory rf;
	private List<Path> additionalMFiles;
	
	public VistaPackage(String packageName, String directoryName, RoutineFactory rf) {
		this.packageName = packageName;
		this.directoryName = directoryName;
		this.rf = rf;
	}
	
	public void acceptSubNodes(RepositoryVisitor visitor) {
		RoutineFactory rf = this.getRoutineFactory();
		for (Path path : this.getPaths()) {
			Node node = rf.getNode(path);
			if (node instanceof Routine) {
				visitor.visitRoutine((Routine) node);
			} else {
				visitor.visitError();
			}
		}
	}
	
	public String getPackageName() {
		return this.packageName;
	}
	
	public String getDirectoryName() {
		return this.directoryName;
	}
	
	public void addFile(String number, String name) {
		FileInfo file = new FileInfo(number, name);
		if (this.files == null) {
			this.files = new ArrayList<FileInfo>();
		}
		this.files.add(file);
	}
	
	public void addPrefix(String prefix) {
		if (prefix.charAt(0) == '!') {
			if (this.exceptionPrefixes == null) {
				this.exceptionPrefixes = new ArrayList<String>();
			}
			this.exceptionPrefixes.add(prefix.substring(1));
		} else {
			if (this.prefixes == null) {
				this.prefixes = new ArrayList<String>();
			}
			this.prefixes.add(prefix);
		}
	}
	
	public List<FileInfo> getFiles() {
		if (this.files == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.files);
		}
	}
	
	public List<String> getPrefixes() {
		if (this.prefixes == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.prefixes);
		}
	}
	
	public String getPrimaryPrefix() {
		if (this.prefixes == null) {
			return "UNCATEGORIZED"; 
		} else {
			return this.prefixes.get(0);
		}
	}
	
	public String getDefaultPrefix() {
		if ((this.prefixes == null) || (this.prefixes.size() == 0)) {
			return "UNCATEGORIZED";
		} else {
			return this.prefixes.get(0);
		}
	}
	
	@Override
	public boolean equals(Object rhs) {
		if ((rhs != null) && (rhs instanceof VistaPackage)) {	
			VistaPackage p = (VistaPackage) rhs;
			return this.packageName.equals(p.packageName) && (this.directoryName == p.directoryName); 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String hashString = this.packageName + "\n" + this.directoryName;
		int result = hashString.hashCode(); 
		return result;
	}
	
	public Path getPath() {
		String vistaFOIARoot = RepositoryInfo.getLocation();
		if (vistaFOIARoot == null) {
			vistaFOIARoot = "";
		}
		String dir = this.getDirectoryName();
		Path path = Paths.get(vistaFOIARoot, "Packages", dir);
		return path;			
	}
	
	public Path getPathForRoutine(String routine) {
		String vistaFOIARoot = RepositoryInfo.getLocation();
		if (vistaFOIARoot == null) {
			vistaFOIARoot = "";
		}
		String dir = this.getDirectoryName();
		Path path = Paths.get(vistaFOIARoot, "Packages", dir, "Routines", routine+".m");
		return path;			
	}
	
	public boolean contains(String routineName) {
		List<String> prefixes = this.getPrefixes();
		for (String prefix : prefixes) {
			if (routineName.startsWith(prefix)) {				
				if (this.exceptionPrefixes != null) {
					for (String ePrefix : this.exceptionPrefixes) {
						if (routineName.startsWith(ePrefix)) return false;
					}
				}
				return true;
			}
		}
		return false;
	}		
	
	public List<Path> getPaths() {
		try {
			Path packagePath = this.getPath();
			FileSupply fs = new FileSupply();
			fs.addPath(packagePath);
			List<Path> paths = fs.getMFiles();
			if (this.additionalMFiles != null) {
				paths.addAll(this.additionalMFiles);
			}
			return paths;		
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}
	
	public RoutineFactory getRoutineFactory() {
		return this.rf;
	}
	
	public Filter<EntryId> getPackageFanoutFilter() {
		Filter<EntryId> localFilter = new LocalFanoutFilter();
		Filter<EntryId> packageFilter = new PackageFanoutFilter(this);
		Filter<EntryId> overallFilter = new AndFilter<EntryId>(localFilter, packageFilter);
		return overallFilter;
	}
	
	@Override
	public void accept(RepositoryVisitor visitor) {
		visitor.visitVistaPackage(this);
	}
	
	public boolean isUncategorized() {
		return this.packageName.equals("UNCATEGORIZED");
	}
	
	public void addAdditionalPath(Path path) {
		if (this.additionalMFiles == null) {
			this.additionalMFiles = new ArrayList<Path>();
		}
		this.additionalMFiles.add(path);
	}
	
	public void addAdditionalFile(Path path) {
		if (this.additionalMFiles == null) {
			this.additionalMFiles = new ArrayList<Path>();
		}
		this.additionalMFiles.add(path);
	}
}
