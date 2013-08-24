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
import org.eclipse.core.resources.IResource;

import us.pwc.vista.eclipse.core.Messages;

public class SelectedMFileFilter implements IResourceFilter {
	public SelectedMFileFilter() {
		super();
	}
	
	@Override
	public String getError(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			String extension = file.getFullPath().getFileExtension();
			if ("m".equals(extension)) {
				return null;
			} else {
				return Messages.bind(Messages.SEL_FILE_NOT_M, file.getFullPath().toString());
			}
		} else {
			return null;
		}
	}		
}
