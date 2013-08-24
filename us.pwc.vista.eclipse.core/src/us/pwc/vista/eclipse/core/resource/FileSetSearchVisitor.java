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

package us.pwc.vista.eclipse.core.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceProxy;

public class FileSetSearchVisitor extends VistAProjectFileVisitor {
	private Map<String, IFile> files = new HashMap<String, IFile>();
	private Set<String> fileNames;
	private Set<String> multiples = new HashSet<String>();
	
	public FileSetSearchVisitor(Collection<String> fileNames) {
		this.fileNames = new HashSet<String>(fileNames);
	}
	
	@Override
	protected boolean isDone() {
		return false;
	}
	
	@Override
	protected void handleFile(IFile file) {
		this.files.put(file.getName(), file);
	}

	@Override
	protected void handleFileProxy(IResourceProxy proxy) {
		IFile file = (IFile) proxy.requestResource();
		this.files.put(proxy.getName(), file);
	}

	@Override
	protected boolean checkName(String name) {
		if (fileNames.contains(name)) {
			if (this.files.containsKey(name)) {
				multiples.add(name);
			} else {
				return true;
			}
		}
		return false;
	}
	
	public Set<String> getMultiplyExists() {
		return this.multiples;
	}
	
	public List<IFile> getFiles(IFolder defaultFolder) {
		List<IFile> result = new ArrayList<IFile>();
		for (String name : this.fileNames) {
			IFile file = this.files.get(name);
			if (file == null) {
				file = defaultFolder.getFile(name);
			}
			result.add(file);
		}
		return result;
	}
}
