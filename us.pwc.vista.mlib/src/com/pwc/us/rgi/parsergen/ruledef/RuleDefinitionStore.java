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

package com.pwc.us.rgi.parsergen.ruledef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.charlib.Predicate;
import com.pwc.us.rgi.charlib.PredicateFactory;
import com.pwc.us.rgi.parser.TFCharacter;
import com.pwc.us.rgi.parser.TFChoice;
import com.pwc.us.rgi.parser.TFConstant;
import com.pwc.us.rgi.parser.TFDelimitedList;
import com.pwc.us.rgi.parser.TFEnd;
import com.pwc.us.rgi.parser.TFList;
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.TFString;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.parsergen.TokenFactoryStore;

public class RuleDefinitionStore extends TokenFactoryStore<RuleSupply> {
	private static final class Triple<T extends TokenFactory<RuleSupply>, A extends Annotation> {
		public T factory;
		public A annotation;
		
		public Triple(T factory, A annotation) {
			this.factory = factory;
			this.annotation = annotation;
		}
	}
	
	public Map<String, TokenFactory<RuleSupply>> symbols = new HashMap<String, TokenFactory<RuleSupply>>();
	
	private java.util.List<Triple<TFChoice<RuleSupply>, Choice>> choices  = new ArrayList<Triple<TFChoice<RuleSupply>, Choice>>();
	private java.util.List<Triple<TFSequence<RuleSupply>, Sequence>> sequences  = new ArrayList<Triple<TFSequence<RuleSupply>, Sequence>>();
	private java.util.List<Triple<TFList<RuleSupply>, List>> lists  = new ArrayList<Triple<TFList<RuleSupply>, List>>();
	private java.util.List<Triple<TFSequence<RuleSupply>, List>> enclosedLists  = new ArrayList<Triple<TFSequence<RuleSupply>, List>>();
	private java.util.List<Triple<TFDelimitedList<RuleSupply>, List>> delimitedLists  = new ArrayList<Triple<TFDelimitedList<RuleSupply>, List>>();
	private java.util.List<Triple<TFSequence<RuleSupply>, List>> enclosedDelimitedLists  = new ArrayList<Triple<TFSequence<RuleSupply>, List>>();
	
	public RuleDefinitionStore() {
	}
	
	private TokenFactory<RuleSupply> addChoice(String name, Choice choice, AdapterSpecification<RuleSupply> spec) {
		TFChoice<RuleSupply> value = new TFChoice<RuleSupply>(name);
		Constructor<? extends RuleSupply> constructor = spec.getTokenAdapter();
		if (constructor != null) value.setTargetType(constructor);
		this.choices.add(new Triple<TFChoice<RuleSupply>, Choice>(value, choice));
		return value;			
	}
	
	private TokenFactory<RuleSupply> addSequence(String name, Sequence sequence, Field f, AdapterSpecification<RuleSupply> spec) {
		TFSequence<RuleSupply> value = new TFSequence<RuleSupply>(name);
		Constructor<? extends RuleSupply> a = spec.getSequenceTokenAdapter();
		if (a != null) value.setSequenceTargetType(a);
		this.sequences.add(new Triple<TFSequence<RuleSupply>, Sequence>(value, sequence));
		return value;			
	}
	
	private TokenFactory<RuleSupply> addList(String name, List list, Field f, AdapterSpecification<RuleSupply> spec) {
		String delimiter = list.delim();
		String left = list.left();
		String right = list.right();
		if (delimiter.length() == 0) {
			if ((left.length() == 0) || (right.length() == 0)) {
				TFList<RuleSupply> value = new TFList<RuleSupply>(name);
				Constructor<? extends RuleSupply> constructor = spec.getTokenAdapter();
				if (constructor != null) value.setListTargetType(constructor);
				this.lists.add(new Triple<TFList<RuleSupply>, List>(value, list));
				return value;
			} else {
				TFSequence<RuleSupply> value = new TFSequence<RuleSupply>(name);
				Constructor<? extends RuleSupply> a = spec.getSequenceTokenAdapter();
				if (a != null) value.setSequenceTargetType(a);
				this.enclosedLists.add(new Triple<TFSequence<RuleSupply>, List>(value, list));
				return value;
			}
		} else {			
			if ((left.length() == 0) || (right.length() == 0)) {
				TFDelimitedList<RuleSupply> value = new TFDelimitedList<RuleSupply>(name);
				Constructor<? extends RuleSupply> a = spec.getDelimitedListTokenAdapter();
				value.setDelimitedListTargetType(a);
				this.delimitedLists.add(new Triple<TFDelimitedList<RuleSupply>, List>(value, list));
				return value;
			} else {
				TFSequence<RuleSupply> value = new TFSequence<RuleSupply>(name);
				Constructor<? extends RuleSupply> a = spec.getSequenceTokenAdapter();
				if (a != null) value.setSequenceTargetType(a);
				this.enclosedDelimitedLists.add(new Triple<TFSequence<RuleSupply>, List>(value, list));
				return value;					
			}
		}
	}
	
	private TokenFactory<RuleSupply> addCharacters(String name, CharSpecified characters, Field f, AdapterSpecification<RuleSupply> spec) {
		PredicateFactory pf = new PredicateFactory();
		pf.addChars(characters.chars());
		pf.addRanges(characters.ranges());
		pf.removeChars(characters.excludechars());
		pf.removeRanges(characters.excluderanges());
		Predicate result = pf.generate();
				
		if (characters.single()) {
			TFCharacter<RuleSupply> tf = new TFCharacter<RuleSupply>(name, result);
			Constructor<? extends RuleSupply> constructor = spec.getStringTokenAdapter();
			if (constructor != null) tf.setStringTargetType(constructor);
			return tf;
		} else {		
			TFString<RuleSupply> tf = new TFString<RuleSupply>(name, result);
			Constructor<? extends RuleSupply> constructor = spec.getStringTokenAdapter();
			if (constructor != null) tf.setStringTargetType(constructor);
			return tf;
		}
	}
	
	private TokenFactory<RuleSupply> addWords(String name, WordSpecified wordSpecied, Field f, AdapterSpecification<RuleSupply> spec) {
		String word = wordSpecied.value();
		TFConstant<RuleSupply> tf = new TFConstant<RuleSupply>(name, word, wordSpecied.ignorecase());
		return tf;
	}
	
	@Override
	protected TokenFactory<RuleSupply> add(Field f, Class<RuleSupply> tokenCls)  {
		String name = f.getName();
		AdapterSpecification<RuleSupply> spec = AdapterSpecification.getInstance(f, tokenCls);
		Choice choice = f.getAnnotation(Choice.class);
		if (choice != null) {
			return this.addChoice(name, choice, spec);
		}			
		Sequence sequence = f.getAnnotation(Sequence.class);
		if (sequence != null) {
			return this.addSequence(name, sequence, f, spec);
		}			
		List list = f.getAnnotation(List.class);
		if (list != null) {
			return this.addList(name, list, f, spec);
		}			
		CharSpecified characters = f.getAnnotation(CharSpecified.class);
		if (characters != null) {
			return this.addCharacters(name, characters, f, spec);
		}
		WordSpecified words = f.getAnnotation(WordSpecified.class);
		if (words != null) {
			return this.addWords(name, words, f, spec);
		}
		return null;			
	}
	
	protected <M> boolean handleField(M target, Field f, Class<RuleSupply> tokenCls) throws IllegalAccessException {
		String name = f.getName();
		TokenFactory<RuleSupply> already = this.symbols.get(name);
		if (already == null) {
			@SuppressWarnings("unchecked")
			TokenFactory<RuleSupply> value = (TokenFactory<RuleSupply>) f.get(target);
			if (value == null) {
				value = this.add(f, tokenCls);
				if (value != null) {
					f.set(target, value);
				} else {
					return false;
				}
			} 
			if (value != null) {
				this.symbols.put(name, value);
			}
		} else {
			f.set(target, already);						
		}
		return true;
	}
	
	private void updateChoices() {		
		for (Triple<TFChoice<RuleSupply>, Choice> p : this.choices) {
			String[] names = p.annotation.value();
			int n = names.length;
			p.factory.reset(n);
			for (int i=0; i<n; ++i) {
				String name = names[i];
				TokenFactory<RuleSupply> tf = this.symbols.get(name);
				p.factory.add(tf);
			}			
		}
	}
	
	private static boolean[] getRequiredFlags(String specification, int n) {
		boolean[] result = new boolean[n];
		if (specification.equals("all")) {
			Arrays.fill(result, true);
			return result;
		}
		if (specification.equals("none")) {
			return result;
		}
		for (int i=0; i<specification.length(); ++i) {
			char ch = specification.charAt(i);
			if (ch == 'r') {
				result[i] = true;
			}
		}
		return result;
	}

	private void updateSequences() {
		for (Triple<TFSequence<RuleSupply>, Sequence> p : this.sequences) {
			String[] names = p.annotation.value();
			int n = names.length;
			boolean[] required = getRequiredFlags(p.annotation.required(), names.length);
			p.factory.reset(n);
			for (int i=0; i<n; ++i) {
				String name = names[i];
				TokenFactory<RuleSupply> tf = this.symbols.get(name);
				p.factory.add(tf, required[i]);
			}			
		}
	}

	private void updateLists() {
		for (Triple<TFList<RuleSupply>, List> p : this.lists) {
			TokenFactory<RuleSupply> f = this.symbols.get(p.annotation.value());
			p.factory.setElement(f);
		}	
	}
	
	private void updateEnclosedLists() {
		for (Triple<TFSequence<RuleSupply>, List> p : this.enclosedLists) {
			TokenFactory<RuleSupply> e = this.symbols.get(p.annotation.value());
			TokenFactory<RuleSupply> l = this.symbols.get(p.annotation.left());
			TokenFactory<RuleSupply> r = this.symbols.get(p.annotation.right());
			TFList<RuleSupply> f = new TFList<RuleSupply>(p.factory.getName() + ".list", e);
			p.factory.reset(3);
			p.factory.add(l, true);
			p.factory.add(f, ! p.annotation.none());
			p.factory.add(r, true);
		}	
	}
	
	private void updateEnclosedDelimitedLists() {
		for (Triple<TFSequence<RuleSupply>, List> p : this.enclosedDelimitedLists) {
			TokenFactory<RuleSupply> e = this.symbols.get(p.annotation.value());
			TokenFactory<RuleSupply> d = this.symbols.get(p.annotation.delim());
			TokenFactory<RuleSupply> l = this.symbols.get(p.annotation.left());
			TokenFactory<RuleSupply> r = this.symbols.get(p.annotation.right());
			
			TFDelimitedList<RuleSupply> dl = new TFDelimitedList<RuleSupply>(p.factory.getName() + ".list");
			dl.set(e, d, p.annotation.empty());
			
			p.factory.reset(3);
			p.factory.add(l, true);
			p.factory.add(dl, ! p.annotation.none());
			p.factory.add(r, true);
		}	
	}
	
	private void updateDelimitedLists() {
		for (Triple<TFDelimitedList<RuleSupply>, List> p : this.delimitedLists) {
			TokenFactory<RuleSupply> e = this.symbols.get(p.annotation.value());
			TokenFactory<RuleSupply> d = this.symbols.get(p.annotation.delim());
			boolean empty = p.annotation.empty();
			p.factory.set(e, d, empty);
		}	
	}
	
	@Override
	public void addAssumed() {		
		TokenFactory<RuleSupply> end = new TFEnd<RuleSupply>("end");
		this.symbols.put("end", end);
	}

	@Override
	public void update(Class<?> cls) throws ParseException {
		this.updateChoices();
		this.updateSequences();
		this.updateLists();
		this.updateEnclosedLists();
		this.updateEnclosedDelimitedLists();
		this.updateDelimitedLists();
	}
}