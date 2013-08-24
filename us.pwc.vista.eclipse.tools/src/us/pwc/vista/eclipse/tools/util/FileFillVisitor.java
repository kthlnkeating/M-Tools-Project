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

package us.pwc.vista.eclipse.tools.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

import com.pwc.us.rgi.m.tool.SourceCodeFiles;

public class FileFillVisitor implements IResourceVisitor {
	private SourceCodeFiles scf;
	private String[] doNotUseSubFolders;
	
	public FileFillVisitor(IProject project, String[] doNotUseSubFolders) {
		String root = project.getLocation().toString();
		this.scf = new SourceCodeFiles(root);
		this.doNotUseSubFolders = doNotUseSubFolders;
	}
	
	@Override
	public boolean visit(IResource resource) { 
		String name = resource.getName();
		if (resource instanceof IFile) {
			if (name.endsWith(".m")) {
				IFile file = (IFile) resource;
				String filePath = file.getProjectRelativePath().toString();
				name = name.substring(0, name.length()-2);
				scf.put(name, filePath);
			}
			return true;
		}
		if (doNotUseSubFolders != null) {
			if ((resource instanceof IFolder) && (resource.getParent() instanceof IProject)) {				
				for (String doNotUseSubfolder : this.doNotUseSubFolders) {
					if (doNotUseSubfolder.equals(name)) {
						return false;
					}
				}
			}
		}
		return true;
    } 
	
	public SourceCodeFiles getSourceCodeFiles() {
		return this.scf;
	}
}
