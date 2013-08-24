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

package us.pwc.vista.eclipse.server.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class FileNoticePage extends FileHandleSupplyPage {
	private Label targetFileCtrl;

	private IFile fileHandle;

	public FileNoticePage(String pageName) {
		super(pageName);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = SWTHelper.createComposite(parent, 2);

		this.targetFileCtrl = SWTHelper.createLabelLabelPair(container, "Target file:");
		
		this.setControl(container);
		this.validate();
	}
	
	private void validate() {
		if (this.fileHandle == null) {
			this.targetFileCtrl.setText("<none>");
			this.setPageComplete(false);
		} else {
			String path = this.fileHandle.getFullPath().toOSString();
			this.targetFileCtrl.setText(path);
			this.setPageComplete(true);			
		}
	}

	public void setFileHandle(IFile fileHandle) {
		this.fileHandle = fileHandle;
		this.validate();
	}

	@Override
	public IFile getFileHandle() {
		return this.fileHandle;
	}
	
	@Override
	public void reset() {
		this.fileHandle = null;
		this.validate();
	}
}
