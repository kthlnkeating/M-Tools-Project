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

public interface ICommonRegexs {
	public static final String DATE = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}"; //$NON-NLS-1$
	public static final String M_NAME = "[%A-Z][A-Z0-9]{0,7}"; //$NON-NLS-1$
	public static final String M_ROUTINE_NAME = M_NAME;
	public static final String M_CODE_LOCATION = '(' + M_NAME + ")?(\\+\\d+)?\\^" + M_NAME; 
}
