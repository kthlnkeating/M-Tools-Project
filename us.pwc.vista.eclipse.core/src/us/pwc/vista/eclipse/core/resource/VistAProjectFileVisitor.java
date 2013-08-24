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

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

public abstract class VistAProjectFileVisitor implements IResourceProxyVisitor {
	private class ProjectLevelVisitor extends VistaProjectSubFolderVisitor {
		public ProjectLevelVisitor(IProject project, String serverName) {
			super(project, serverName);
		}
		
		@Override
		protected void visitFile(IFile file) {
			String name = file.getName();
			if (VistAProjectFileVisitor.this.checkName(name)) {
				VistAProjectFileVisitor.this.handleFile(file);
			}			
		}
	}
		
	protected abstract void handleFile(IFile file);

	protected abstract boolean checkName(String name);

	protected abstract boolean isDone();
	
	protected abstract void handleFileProxy(IResourceProxy proxy);

	@Override
	public boolean visit(IResourceProxy proxy) { 
		if (this.isDone()) {
			return false;
		}		
		if (proxy.getType() == IResource.FILE) {
			if (this.checkName(proxy.getName())) {
				this.handleFileProxy(proxy);
			}
		}
		return true;
    } 
	
	public void run(IProject project, String serverName) throws CoreException {
		ProjectLevelVisitor plv = this.new ProjectLevelVisitor(project, serverName);
		project.accept(plv);
		if (this.isDone()) return;
		List<IFolder> folders = plv.getFolders();
		for (IFolder folder : folders) {
			if (this.isDone()) return;
			folder.accept(this, IContainer.NONE);
		}
	}

}
