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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;

public class FileSearchVisitor implements IResourceProxyVisitor {
	private IFile file;
	private String fileName;
	private String excludeDirectory;
	
	public FileSearchVisitor(String fileName, String excludeDirectory) {
		this.fileName = fileName;
		this.excludeDirectory = excludeDirectory;
	}
	
	@Override
	public boolean visit(IResourceProxy proxy) { 
		if (this.file != null) {
			return false;
		}
		String name = proxy.getName();
		if (proxy.getType() != IResource.FILE) {
			return ! name.equals(this.excludeDirectory);
		}
		if (! name.equals(this.fileName)) {
			return true;
		}
		this.file = (IFile) proxy.requestResource();
		return false;
    } 
	
	public IFile getFile() {
		return this.file;
	}
}

