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

import com.pwc.us.rgi.parser.TFList;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public class FSRList<T extends Token> extends FSRContainer<T> {
	private FactorySupplyRule<T> element;
	private TFList<T> factory;
	
	public FSRList(String name) {
		this.factory = new TFList<T>(name);
	}
	
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	@Override
	public boolean update() {
		TokenFactory<T> element = this.element.getShellFactory();
		this.factory.setElement(element);
		return true;
	}

	@Override
	public TFList<T> getShellFactory() {
		return this.factory;
	}

	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		 Constructor<? extends T> a = spec.getListTokenAdapter();
		 if (a != null) this.factory.setListTargetType(a);
	}
	
	@Override
	public void set(int index, RuleSupplyFlag flag, FactorySupplyRule<T> r) {
		if (index == 0) {
			this.element = r;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
}