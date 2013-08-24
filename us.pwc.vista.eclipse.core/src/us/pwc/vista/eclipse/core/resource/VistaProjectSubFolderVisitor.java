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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

import us.pwc.vista.eclipse.core.VistACorePrefs;

public abstract class VistaProjectSubFolderVisitor implements IResourceVisitor {
	private String[] doNotUseFolders;
	List<IFolder> folders = new ArrayList<IFolder>();
	
	public VistaProjectSubFolderVisitor(IProject project, String serverName) {
		this.doNotUseFolders = VistACorePrefs.getDoNotUseProjectSubfolders(project, serverName);
	}
	
	protected abstract void visitFile(IFile file);

	@Override
	public boolean visit(IResource resource) { 
		String name = resource.getName();
		if (resource.getType() == IResource.FILE) {
			this.visitFile((IFile) resource);
			return false;
		} else if (resource.getType() == IResource.FOLDER) {
			if (this.doNotUseFolders != null) {
				boolean found = false;
				for (String doNotUseFolder : this.doNotUseFolders) {
					if (doNotUseFolder.equals(name)) {
						found = true;
						break;
					}
				}
				if (! found) {
					this.folders.add((IFolder) resource);					
				}
			}
			return false;
		} else {
			return true;
		}
    } 
	
	public List<IFolder> getFolders() {
		return this.folders;
	}
}
