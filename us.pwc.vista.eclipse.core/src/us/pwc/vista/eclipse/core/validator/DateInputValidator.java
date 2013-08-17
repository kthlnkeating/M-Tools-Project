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

public class DateInputValidator extends RegexInputValidator {
	private static final String DATE_REGEX = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}"; //$NON-NLS-1$

	public DateInputValidator(boolean entryRequired) {
		super(entryRequired, DATE_REGEX, Messages.WORD_DATE);
	}
}
