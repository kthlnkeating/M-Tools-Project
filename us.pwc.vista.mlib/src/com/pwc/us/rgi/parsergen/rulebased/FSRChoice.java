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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwc.us.rgi.parser.Adapter;
import com.pwc.us.rgi.parser.TFChoice;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;

public class FSRChoice<T extends Token> extends FSRCollection<T> {
	private static class ForkAlgorithm<T extends Token> {	
		private String appliedOnName;
		
		private List<FactorySupplyRule<T>> list = new ArrayList<FactorySupplyRule<T>>();
		
		private Map<String, Integer> choiceOrder = new HashMap<String, Integer>();
		private Map<Integer, FSRForkedSequence<T>> leadingShared; 
		
		public ForkAlgorithm(String name) {
			this.appliedOnName = name;
		}
	 	
		public void add(FactorySupplyRule<T> tf) {
			FactorySupplyRule<T> leading = tf.getLeading(0);
			String name = leading.getName();
			Integer existing = this.choiceOrder.get(name);
			if (existing == null) {
				int n = this.list.size();
				this.list.add(tf);
				this.choiceOrder.put(name, n);
			} else {
				FSRForkedSequence<T> fseq = (this.leadingShared == null) ? null : this.leadingShared.get(existing);
				if (fseq != null) {
					fseq.add(tf);
				} else {
					int n = existing.intValue();
					fseq = new FSRForkedSequence<T>(this.appliedOnName + "." + name, leading);
					FactorySupplyRule<T> current = this.list.get(n);
					fseq.add(current);
					fseq.add(tf);
					this.list.set(n, fseq);
					if (this.leadingShared == null) {
						this.leadingShared = new HashMap<Integer, FSRForkedSequence<T>>();
					}
					this.leadingShared.put(existing, fseq);
				}
			}
		}	
	}

	private TFChoice<T> factory;
	
	public FSRChoice(String name, int length) {
		super(length);
		this.factory = new TFChoice<T>(name);
	}
	
	@Override
	public String getName() {
		return this.factory.getName();
	}
	
	private List<TokenFactory<T>> getChoiceFactories() {
		List<TokenFactory<T>> result = new ArrayList<TokenFactory<T>>();
		
		ForkAlgorithm<T> algorithm = new ForkAlgorithm<T>(this.getName());
		for (FactorySupplyRule<T> r : this.list) {
			algorithm.add(r);
		}
		for (FactorySupplyRule<T> on : algorithm.list) {
			if (on instanceof FSRForkedSequence) on.update();
			result.add(on.getShellFactory());
		}
		return result;
	}
	
	@Override
	public boolean update() {
		List<TokenFactory<T>> fs = this.getChoiceFactories();
		this.factory.reset(fs.size());
		for (TokenFactory<T> f : fs) {
			this.factory.add(f);
		}
		return true;
	}

	@Override
	public TFChoice<T> getShellFactory() {
		return this.factory;	
	}
	
	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		Constructor<? extends T> constructor = spec.getTokenAdapter();
		if (constructor != null) this.factory.setTargetType(constructor);
	}
	
	@Override
	public Adapter<T> getAdapter() {
		return this.factory;
	}
}
