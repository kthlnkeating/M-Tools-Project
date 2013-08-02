//---------------------------------------------------------------------------
//Copyright 2013 PwC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.vista.tools.routine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.vista.tools.CLIParams;
import com.pwc.us.rgi.vista.tools.CLIParamsAdapter;
import com.pwc.us.rgi.vista.tools.MRALogger;
import com.pwc.us.rgi.vista.tools.MRARoutineFactory;
import com.pwc.us.rgi.vista.tools.Tool;

abstract class CLIRoutineTool extends Tool {
	private OutputFlags ofs;
	
	public CLIRoutineTool(CLIParams params) {
		super(params);
		this.ofs = CLIParamsAdapter.toOutputFlags(params);
	}
	
	private List<Path> getMFiles() {
		List<Path> absoluteFileNames = new ArrayList<Path>();
		for (String mf : this.params.additionalMFiles) {
			Path path = Paths.get(mf);
			String fileName = path.getFileName().toString();
			if (fileName.indexOf('.') < 0) {
				fileName = fileName + ".m";
			}
			Path parent = path.getParent();
			if (parent != null) {
				path = Paths.get(parent.toString(), fileName);
			} else {
				path = Paths.get(fileName);
			}
			if (! path.isAbsolute()) {
				String cwd = System.getProperty("user.dir");
				path = Paths.get(cwd, path.toString());
			}
			if (Files.exists(path)) {
				absoluteFileNames.add(path);
			} else {
				MRALogger.logError("Cannot find file: " + path.toString());					
			}
		}
		if (absoluteFileNames.size() > 0) {				
			return absoluteFileNames;
		} else {
			MRALogger.logError("No file specified or none of the specified files exists.");
			return null;
		}
	}
	
	protected List<Routine> getRoutines() {
		MRARoutineFactory rf = MRARoutineFactory.getInstance(MVersion.CACHE);
		if (rf != null) {
			List<Path> paths = this.getMFiles();
			if (paths != null) {
				List<Routine> routines = new ArrayList<Routine>();
				for (Path p : paths) {
					Routine routine = rf.getRoutineNode(p);
					routines.add(routine);
				}
				if (routines.size() > 0) {
					return routines;
				}
			}
		}
		return null;
	}
	
	
	protected void write(ToolResult result, Terminal t) throws IOException {
		t.getTerminalFormatter().setTitleWidth(12);
		result.write(t, this.ofs);
		t.stop();
	}
}
