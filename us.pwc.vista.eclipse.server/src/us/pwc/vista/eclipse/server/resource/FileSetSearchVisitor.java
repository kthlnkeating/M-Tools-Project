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

package us.pwc.vista.eclipse.server.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;

public class FileSetSearchVisitor implements IResourceProxyVisitor {
	private Map<String, IFile> files = new HashMap<String, IFile>();
	private Set<String> fileNames;
	private String excludeDirectory;
	private Set<String> multiples = new HashSet<String>();
	
	public FileSetSearchVisitor(Collection<String> fileNames, String excludeDirectory) {
		this.fileNames = new HashSet<String>(fileNames);
		this.excludeDirectory = excludeDirectory;
	}
	
	@Override
	public boolean visit(IResourceProxy proxy) { 
		String name = proxy.getName();
		if (proxy.getType() != IResource.FILE) {
			return ! name.equals(this.excludeDirectory);
		}
		if (! fileNames.contains(name)) {
			return true;
		}
		if (this.files.containsKey(name)) {
			multiples.add(name);
		} else {
			IFile file = (IFile) proxy.requestResource();
			this.files.put(name, file);
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
