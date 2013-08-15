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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class FileFillState {
	private List<IFile> files = new ArrayList<IFile>();
	private List<IResource> invalidResources = new ArrayList<IResource>();
	private IResourceFilter filter;
	private Set<String> alreadyVisited = new HashSet<String>();	

	private class FileFillVisitor implements IResourceVisitor {
		@Override
		public boolean visit (IResource resource) {
			String fullPath = resource.getFullPath().toString();
			if (! FileFillState.this.check(resource)) {
				return false;
			}
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				FileFillState.this.files.add(file);
			} 
			FileFillState.this.alreadyVisited.add(fullPath);
			return true;
		}
	}

	public FileFillState(IResourceFilter filter) {
		this.filter = filter;
	}

	private boolean check(IResource resource) {
		String fullPath = resource.getFullPath().toString();
		if (this.alreadyVisited.contains(fullPath)) {
			return false;
		}
		if (! this.filter.isValid(resource)) {
			this.invalidResources.add(resource);
			this.alreadyVisited.add(fullPath);
			return false;
		}
		return true;
	}
	
	public void add(IResource resource) throws CoreException {		
		if (check(resource)) {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				this.files.add(file);
				String fullPath = resource.getFullPath().toString();
				this.alreadyVisited.add(fullPath);
			} else {
				IResourceVisitor v = this.new FileFillVisitor();
				resource.accept(v);
			}			
		}			
	}
	
	public List<IFile> getFiles() {
		return this.files;
	}
	
	public String getInvalidResourcesAsString(int max, String delimiter) {
		int n = this.invalidResources.size();
		if (n == 0) {
			return null;
		} else {
			IResource resource0 = this.invalidResources.get(0);
			String result = resource0.getFullPath().toString();
			for (int i=1; i<n; ++n) {
				if (i == max) {
					result += delimiter + "...";
					return result;
				}
				IResource resource = this.invalidResources.get(i);
				result += delimiter + resource.getFullPath().toString();			
			}
			return result;
		}
	}
}
