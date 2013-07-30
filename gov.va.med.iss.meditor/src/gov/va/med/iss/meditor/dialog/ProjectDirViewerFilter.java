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

package gov.va.med.iss.meditor.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ProjectDirViewerFilter extends ViewerFilter {
	private String projectName; 
	
	public ProjectDirViewerFilter(String projectName) {
		this.projectName = projectName;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		IResource eRes = (IResource) element;		
		if (eRes.getType() == IResource.PROJECT) {
			return this.projectName.equals(eRes.getName());
		}
		if (eRes.getType() == IResource.FILE) {
			return false;
		}		
		if (eRes.getType() == IResource.FOLDER) {
			IProject project = eRes.getProject();
			if (! this.projectName.equals(project.getName())) {
				return false;
			}			
			if (eRes.getParent().getType() == IResource.PROJECT) {
				return ! eRes.getName().equals("backups");
			} else {
				return true;
			}
		}		
		return true;
	}
}
