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

package com.pwc.us.rgi.m.struct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MRoutineContent {
	private String name;
	private List<String> lines = new ArrayList<String>();

	public MRoutineContent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getLines() {
		return this.lines;
	}
	
	private static MRoutineContent getInstance(String name, Scanner scanner) {
		MRoutineContent result = new MRoutineContent(name);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			result.lines.add(line);
		}
		return result;				
	}

	public static MRoutineContent getInstance(String name, InputStream is) {
		Scanner scanner = new Scanner(is);
		MRoutineContent result = MRoutineContent.getInstance(name, scanner);
		scanner.close();
		return result;				
	}

	public static MRoutineContent getInstance(Path path) throws IOException {
		String fileName = path.getFileName().toString();
		String name = fileName.split(".m")[0];
		Scanner scanner = new Scanner(path);
		MRoutineContent result = MRoutineContent.getInstance(name, scanner);
		scanner.close();
		return result;		
	}	
}
