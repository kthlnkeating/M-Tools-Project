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

import java.lang.reflect.Constructor;

import com.pwc.us.rgi.charlib.Predicate;
import com.pwc.us.rgi.parser.TFCharacter;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parsergen.AdapterSpecification;

public class FSRChar<T extends Token> extends FSRBase<T> {
	private String expr;
	private Predicate predicate;
	private TFCharacter<T> factory;
	
	public FSRChar(String expr, Predicate predicate) {
		this.expr = expr;
		this.predicate = predicate;
		this.factory = new TFCharacter<T>(this.expr, this.predicate);
	}
	
	@Override
	public String getName() {
		return this.expr;
	}
	
	@Override
	public TFCharacter<T> getShellFactory() {
		return this.factory;
	}
	
	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		 Constructor<? extends T> constructor = spec.getStringTokenAdapter();
		 if (constructor != null) this.factory.setStringTargetType(constructor);
	}
}
