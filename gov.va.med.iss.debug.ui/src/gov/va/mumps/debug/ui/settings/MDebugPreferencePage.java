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

package gov.va.mumps.debug.ui.settings;

import gov.va.mumps.debug.core.MDebugSettings;
import gov.va.mumps.debug.core.model.MDebugPreference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class MDebugPreferencePage extends PropertyPage implements IWorkbenchPreferencePage {
	private Button genericBtn;
	private Button cacheTelnetBtn;
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		contents.setLayout(gl);

		this.genericBtn = SWTHelper.createRadioBox(contents, "Generic", 1);
		this.cacheTelnetBtn = SWTHelper.createRadioBox(contents, "Cache Telnet", 1);
		
		this.initialize();
		return contents;
	}
	
	private void initialize() {
		MDebugPreference preference = MDebugSettings.getDebugPreference();
		switch (preference) {
		case CACHE_TELNET:
			this.cacheTelnetBtn.setSelection(true);
			break;
		default:
			this.genericBtn.setSelection(true);
			break;
		}
	}
	
	@Override
	public boolean performOk() {
		if (this.cacheTelnetBtn.getSelection()) {
			MDebugSettings.setDebugPreference(MDebugPreference.CACHE_TELNET);
		} else {
			MDebugSettings.setDebugPreference(MDebugPreference.GENERIC);
		}
		return super.performOk();
	}
}