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

import java.util.Map;

public class MNameWithMnemonic {
	private String mnemonic;
	private String name;
	
	public MNameWithMnemonic(String mnemonic, String name) {
		this.mnemonic = mnemonic;
		this.name = name;
	}
	
	public String getMnemonic() {
		return this.mnemonic;
	}
	
	public String getName() {
		return this.name;
	}
		
	public String refactor(KeywordRefactorFlags flags, String current) {
		String result = flags.getUseFlag().change(this, current);
		return flags.getStringCaseFlag().change(result);
	}
	
	public static <M extends Map<String, MNameWithMnemonic>> void update(M target, String mnemonic, String name) {
		MNameWithMnemonic mnwm = new MNameWithMnemonic(mnemonic, name);
		target.put(mnemonic, mnwm);
		if (! mnemonic.equals(name)) {
			target.put(name, mnwm);
		}		
	}
}
