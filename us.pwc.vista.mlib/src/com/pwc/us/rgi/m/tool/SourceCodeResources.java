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

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SourceCodeResources<T> implements SourceCodeSupply  {
	private Class<T> resourceClass;
	private Map<String, String> resourcesByRoutineName = new HashMap<String, String>();
	
	public SourceCodeResources(Class<T> resourceClass) {
		this.resourceClass = resourceClass;
	}
	
	public void put(String routineName, String resourceName) {
		this.resourcesByRoutineName.put(routineName, resourceName);		
	}
	
	public void put(String resourceName) {
		Path path = Paths.get(resourceName);
		String resourceLastPiece = path.getFileName().toString();
		String routineName = resourceLastPiece.substring(0, resourceLastPiece.length()-2);
		this.put(routineName, resourceName);
	}
	
	@Override
	public InputStream getStream(String routineName) {
		String resourcePath = this.resourcesByRoutineName.get(routineName);
		if (resourcePath != null) {
			InputStream is = resourceClass.getResourceAsStream(resourcePath);
			return is;
		}
		return null;
	}
	
	public static <T> SourceCodeResources<T> getInstance(Class<T> resourceClass, String[] resourcePaths) {
		SourceCodeResources<T> result = new SourceCodeResources<T>(resourceClass);
		for (String path : resourcePaths) {
			result.put(path);			
		}		
		return result;
	}
		
	@Override
	public Collection<String> getAllRoutineNames() {
		return this.resourcesByRoutineName.keySet();
	}		
}
