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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwc.us.rgi.stringlib.EndsWithFilter;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.util.IOUtil;

public class SourceCodeFiles implements SourceCodeSupply {
	private Map<String, String> filesByRoutineName = new HashMap<String, String>();
	private String root;
	
	public SourceCodeFiles() {		
	}
	
	public SourceCodeFiles(String root) {
		this.root = root;
	}
	
	public void put(String routineName, String fileName) {
		this.filesByRoutineName.put(routineName, fileName);		
	}
	
	public String get(String routineName) {
		return this.filesByRoutineName.get(routineName);
	}
	
	@Override
	public InputStream getStream(String routineName) {
		String fileName = this.filesByRoutineName.get(routineName);
		if (fileName != null) {
			try {
				if (this.root != null) {
					Path path = Paths.get(this.root, fileName);
					fileName = path.toString();
				}				
				InputStream is = new FileInputStream(fileName);
				return is;
			} catch(IOException e) {
				return null;
			}
		}
		return null;
	}
	
	public static SourceCodeFiles getInstance(String rootDirectory) throws IOException {
		Filter<String> nameFilter = new EndsWithFilter(".m");
		List<Path> paths = IOUtil.getFiles(rootDirectory, nameFilter);
		SourceCodeFiles result = new SourceCodeFiles();
		for (Path path : paths) {
			String fileName = path.getFileName().toString();
			String routineName = fileName.substring(0, fileName.length()-2);
			result.put(routineName, path.toString());			
		}		
		return result;
	}
		
	@Override
	public Collection<String> getAllRoutineNames() {
		return this.filesByRoutineName.keySet();
	}	
}
