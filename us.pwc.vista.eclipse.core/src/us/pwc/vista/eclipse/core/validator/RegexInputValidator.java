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

import us.pwc.vista.eclipse.core.Messages;


public class RegexInputValidator extends InputValidator {
	private String regex;
	private String inputName;
	
	public RegexInputValidator(boolean entryRequired, String regex, String inputName) {
		super(entryRequired);
		this.regex = regex;
		this.inputName = inputName;
	}
	
	@Override
	protected String isValidWhenNotEmpty(String newText) {
		if (! newText.matches(this.regex)) {
			return Messages.bind(Messages.VAL_INVALID_INPUT, this.inputName);
		} else {
			return null;
		}
	}
}
