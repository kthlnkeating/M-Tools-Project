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
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public class FSREnclosedDelimitedList<T extends Token> extends FSRContainer<T> {
	private FactorySupplyRule<T> element;
	private FactorySupplyRule<T> delimiter;
	private FactorySupplyRule<T> left;
	private FactorySupplyRule<T> right;
	private boolean empty;
	private boolean none;
	private TFSequence<T> factory;
	
	public FSREnclosedDelimitedList(String name) {
		this.factory = new TFSequence<T>(name, 3);
	}
	
	public void setEmptyAllowed(boolean b) {
		this.empty = b;
	}
	
	
	public void setNoneAllowed(boolean b) {
		this.none = b;
	}
		
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	@Override
	public boolean update() {
		String name = this.factory.getName();
		TokenFactory<T> e = this.element.getShellFactory();
		TokenFactory<T> d = this.delimiter.getShellFactory();
		TFDelimitedList<T> dl = new TFDelimitedList<T>(name);		
		dl.set(e, d, this.empty);
		TokenFactory<T> l = this.left.getShellFactory();
		TokenFactory<T> r = this.right.getShellFactory();
		
		this.factory.reset(4);
		this.factory.add(l, true);
		this.factory.add(dl, ! this.none);
		this.factory.add(r, true);
		return true;		
	}

	@Override
	public TFSequence<T> getShellFactory() {
		return this.factory;
	}

	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		 Constructor<? extends T> a = spec.getSequenceTokenAdapter();
		 if (a != null) this.factory.setSequenceTargetType(a);
	}	
	
	@Override
	public void set(int index, RuleSupplyFlag flag, FactorySupplyRule<T> r) {
		switch(index) {
		case 0: 
			this.element = r; 
			break;
		case 1: 
			this.delimiter = r; 
			break;
		case 2:
			this.left = r;
			break;
		case 3:
			this.right = r;
			break;
		default:
			throw new IndexOutOfBoundsException();				
		}
	}		
}