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

package us.pwc.vista.eclipse.server.dialog;

import org.eclipse.jface.dialogs.IInputValidator;

class BasicRequiredValidator implements IInputValidator {
	private String requiredMessage;
	private String invalidMessage;
	private String regex;
	
	public BasicRequiredValidator(String requiredMessage, String invalidMessage, String regex) {
		super();
		this.requiredMessage = requiredMessage;
		this.invalidMessage = invalidMessage;
		this.regex = regex;
	}

	@Override
	public String isValid(String newText) {
		if ((newText == null) || newText.isEmpty()) {
			return this.requiredMessage;
		}
		if (this.regex != null) {
			if (! newText.matches(this.regex)) {
				return this.invalidMessage;
			}
		}
		return null;			
	}
}