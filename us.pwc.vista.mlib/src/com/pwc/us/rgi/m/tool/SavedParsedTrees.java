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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.stringlib.EndsWithFilter;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.util.IOUtil;

public class SavedParsedTrees implements ParseTreeSupply {
	private String inputPath;
	Collection<String> allRoutineNames;

	public SavedParsedTrees(String inputPath) {
		this.inputPath = inputPath;
	}
	
	@Override
	public Routine getParseTree(String routineName) {
		return Routine.readSerialized(this.inputPath, routineName);		
	}
	
	@Override
	public Collection<String> getAllRoutineNames() {
		if (this.allRoutineNames != null) {
			return this.allRoutineNames;
		}		
		try {
			Filter<String> nameFilter = new EndsWithFilter(".ser");
			List<Path> paths = IOUtil.getFiles(this.inputPath, nameFilter);
			List<String> routineNames = new ArrayList<String>();
			for (Path path : paths) {
				String fileName = path.getFileName().toString();
				String routineName = fileName.substring(0, fileName.length()-4);
				routineNames.add(routineName);
			}		
			this.allRoutineNames = routineNames;
			return routineNames;
		} catch (IOException e) {
			return null;
		}
	}
}
