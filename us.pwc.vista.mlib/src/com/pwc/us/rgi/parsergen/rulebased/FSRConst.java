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

package com.pwc.us.rgi.parsergen.rulebased;

import com.pwc.us.rgi.parser.TFConstant;
import com.pwc.us.rgi.parser.Token;

public class FSRConst<T extends Token> extends FSRBase<T >{
	private String value;
	private TFConstant<T> factory;
	
	public FSRConst(String value, boolean ignoreCase) {
		this.value = value;
		String key = "\"" + this.value + "\"";
		this.factory = new TFConstant<T>(key, this.value, ignoreCase);
	}
	
	@Override
	public String getName() {
		return "\"" + this.value + "\"";
	}
	
	@Override
	public TFConstant<T> getShellFactory() {
		return this.factory;
	}
}