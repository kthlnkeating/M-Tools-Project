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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceProxy;

public class FileSearchVisitor extends VistAProjectFileVisitor {
	private IFile file;
	private String fileName;
	
	public FileSearchVisitor(String fileName) {
		this.fileName = fileName;
	}

	@Override
	protected boolean isDone() {
		return this.file != null;
	}
	
	@Override
	protected void handleFile(IFile file) {
		this.file = file;
	}

	@Override
	protected void handleFileProxy(IResourceProxy proxy) {
		this.file = (IFile) proxy.requestResource();
	}

	@Override
	protected boolean checkName(String name) {
		return name.equals(this.fileName);		
	}
	
	public IFile getFile() {
		return this.file;
	}
}
