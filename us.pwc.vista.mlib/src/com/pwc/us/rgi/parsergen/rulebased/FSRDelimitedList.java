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

import com.pwc.us.rgi.parser.TFDelimitedList;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public class FSRDelimitedList<T extends Token> extends FSRContainer<T> {
	private FactorySupplyRule<T> element;
	private FactorySupplyRule<T> delimiter;
	private TFDelimitedList<T> factory;
	
	public FSRDelimitedList(String name) {
		this.factory = new TFDelimitedList<T>(name);
	}
	
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	@Override
	public boolean update() {
		this.factory.set(this.element.getShellFactory(), this.delimiter.getShellFactory(), false);				
		return true;
	}

	@Override
	public TFDelimitedList<T> getShellFactory() {
		return this.factory;
	}
	
	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		 Constructor<? extends T> a = spec.getDelimitedListTokenAdapter();
		 if (a != null) this.factory.setDelimitedListTargetType(a);
	}
	
	@Override
	public void set(int index, RuleSupplyFlag flag, FactorySupplyRule<T> r) {
		if (index == 0) {
			this.element = r;
		} else if (index == 1){
			this.delimiter = r;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}	
}