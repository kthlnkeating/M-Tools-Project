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

package us.pwc.vista.eclipse.core.validator;

import org.eclipse.jface.dialogs.IInputValidator;

import us.pwc.vista.eclipse.core.Messages;

public abstract class InputValidator implements IInputValidator {
	private boolean entryRequired;
	
	public InputValidator(boolean entryRequired) {
		this.entryRequired = entryRequired;
	}
	
	protected abstract String isValidWhenNotEmpty(String newText);
		
	@Override
	public String isValid(String newText) {
		if ((newText == null) || newText.isEmpty()) {
			if (this.entryRequired) {
				return Messages.VAL_ENTRY_REQUIRED;
			} else {
				return null;
			}			
		}	
		return this.isValidWhenNotEmpty(newText);
	}
}
