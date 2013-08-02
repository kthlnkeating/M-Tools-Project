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

package com.pwc.us.rgi.m.struct;

import com.pwc.us.rgi.struct.StringCase;

public class KeywordRefactorFlags {
	private StringCase caseFlag;
	private KeywordRefactorUseFlag useFlag;
	
	public KeywordRefactorFlags(StringCase caseFlag, KeywordRefactorUseFlag useFlag) {
		this.caseFlag = caseFlag;
		this.useFlag = useFlag;
	}
	
	public StringCase getStringCaseFlag() {
		return this.caseFlag;
	}
	
	public KeywordRefactorUseFlag getUseFlag() {
		return this.useFlag;
	}
	
	public void setStringCaseFlag(StringCase caseFlag) {
		this.caseFlag = caseFlag;
	}
	
	public void setUseFlag(KeywordRefactorUseFlag useFlag) {
		this.useFlag = useFlag;
	}
}
