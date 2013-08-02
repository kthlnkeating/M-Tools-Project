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

import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public abstract class FSRCollection<T extends Token> extends FSRContainer<T> {
	protected List<FactorySupplyRule<T>> list = new ArrayList<FactorySupplyRule<T>>();
	
	protected FSRCollection(int length) {
		this.list = new ArrayList<FactorySupplyRule<T>>();
		for (int i=0; i<length; ++i) {
			this.list.add(null);
		}
	}

	@Override
	public void set(int index, RuleSupplyFlag flag, FactorySupplyRule<T> r) {
		this.list.set(index, r);
	}
	
	public FactorySupplyRule<T> get(int index) {
		return this.list.get(index);
	}
}
