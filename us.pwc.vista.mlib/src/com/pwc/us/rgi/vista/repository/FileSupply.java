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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.stringlib.EndsWithFilter;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.util.IOUtil;

public class FileSupply {
	private List<Path> paths;
	
	public void addPath(Path path) {
		if (path.getRoot() == null) {
			String vistaFOIARoot = RepositoryInfo.getLocation();
			if (vistaFOIARoot != null) {
				path = Paths.get(vistaFOIARoot, path.toString());
			}
		}		
		if (this.paths == null) {
			this.paths = new ArrayList<Path>();
		}
		this.paths.add(path);
	}
	
	public void addPath(String path) {
		Path p = Paths.get(path);
		this.addPath(p);
	}
	
	public List<Path> getFiles(Filter<String> nameFilter) throws IOException {
		if (this.paths == null) {
			String vistaFOIARoot = RepositoryInfo.getLocation();
			return IOUtil.getFiles(vistaFOIARoot, nameFilter);
		} else {			
			return IOUtil.getFiles(this.paths, nameFilter);
		}
	}
	
	public List<Path> getMFiles() throws IOException {
		Filter<String> nameFilter = new EndsWithFilter(".m");
		return this.getFiles(nameFilter);
	}
	
	public List<Path> getZWRFiles() throws IOException {
		Filter<String> nameFilter = new EndsWithFilter(".zwr");
		return this.getFiles(nameFilter);
	}
	
	public static List<Path> getAllMFiles() throws IOException {
		FileSupply s = new FileSupply();
		List<Path> paths = s.getMFiles();
		return paths;
	}

	public static List<Path> getAllZWRFiles() throws IOException {
		FileSupply s = new FileSupply();
		List<Path> paths = s.getZWRFiles();
		return paths;
	}
}
