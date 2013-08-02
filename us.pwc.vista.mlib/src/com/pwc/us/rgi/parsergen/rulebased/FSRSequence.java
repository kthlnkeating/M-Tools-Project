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

import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ParseErrorException;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public class FSRSequence<T extends Token> extends FSRCollection<T> {
	private TFSequence<T> factory;
	private boolean[] required;
	
	public FSRSequence(String name, int length) {
		super(length);
		this.factory = new TFSequence<T>(name);
		this.required = new boolean[length];
	}
	
	@Override
	public void set(int index, RuleSupplyFlag flag, FactorySupplyRule<T> r) {
		super.set(index, flag, r);
		this.required[index] = (flag != RuleSupplyFlag.INNER_OPTIONAL);
		if (flag == RuleSupplyFlag.TOP) {
			throw new ParseErrorException("Internal error: attempt to get required flag for a top symbol.");			
		}
	}
	
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	@Override
	public FactorySupplyRule<T> getLeading(int level) {
		if (level == 0) {
			return this.list.get(0).getLeading(1);
		} else {
			return this;
		}
	}
	
	@Override
	public boolean update() {
		this.factory.reset(this.list.size());
		int index = 0;
		for (FactorySupplyRule<T> spg : this.list) {
			TokenFactory<T> f = spg.getShellFactory();
			boolean b = this.required[index];
			this.factory.add(f, b);
			++index;
		}

		return true;		
	}
	
	@Override
	public TFSequence<T> getShellFactory() {
		return this.factory;
	}
	
	@Override
	public int getSequenceCount() {
		return this.list.size();
	}

	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		 Constructor<? extends T> a = spec.getSequenceTokenAdapter();
		 if (a != null) this.factory.setSequenceTargetType(a);
	}	
}
