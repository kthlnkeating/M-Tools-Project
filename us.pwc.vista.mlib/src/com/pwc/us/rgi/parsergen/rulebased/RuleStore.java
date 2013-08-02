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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.pwc.us.rgi.parser.TFEnd;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.AdapterSpecification;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.parsergen.TokenFactoryStore;
import com.pwc.us.rgi.parsergen.ruledef.RuleParser;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupply;
import com.pwc.us.rgi.parsergen.ruledef.RuleSupplyFlag;

public class RuleStore<T extends Token> extends TokenFactoryStore<T>  {
	private RuleParser ruleParser = new RuleParser();
	
	public Map<String, TokenFactory<T>> symbols = new HashMap<String, TokenFactory<T>>();
	
	private DefinitionVisitor<T> dv = new DefinitionVisitor<T>();
	
	public RuleStore() {
	}
	
	private FactorySupplyRule<T> addRule(String name, Rule ruleAnnotation, Field f, Class<T> tokenCls) {
		String ruleText = ruleAnnotation.value();
		RuleSupply ruleSupply = ruleParser.getTopTFRule(name, ruleText);
		if (ruleSupply == null) return null;
		ruleSupply.accept(this.dv, name, RuleSupplyFlag.TOP);
		FactorySupplyRule<T> topRule = this.dv.getTopRule(name);
		AdapterSpecification<T> spec = AdapterSpecification.getInstance(f, tokenCls);
		topRule.setAdapter(spec);
		return topRule;
	}
	
	@Override
	protected TokenFactory<T> add(Field f, Class<T> tokenCls)  {
		String name = f.getName();			
		Rule description = f.getAnnotation(Rule.class);
		if (description != null) {
			FactorySupplyRule<T> topRule = this.addRule(name, description, f, tokenCls);
			return topRule.getShellFactory();
		} 
		return null;
	}
	
	protected <M> boolean handleField(M target, Field f, Class<T> tokenCls) throws IllegalAccessException {
		String name = f.getName();
		TokenFactory<T> already = this.symbols.get(name);
		if (already == null) {
			@SuppressWarnings("unchecked")
			TokenFactory<T> value = (TokenFactory<T>) f.get(target);
			if (value == null) {
				value = this.add(f, tokenCls);
				if (value != null) {
					f.set(target, value);
				} else {
					return false;
				}
			} else {
				FSRCustom<T> fsr = new FSRCustom<T>(value);
				this.dv.addTopRule(name, fsr);
			}
			if (value != null) {
				this.symbols.put(name, value);
			}
		} else {
			f.set(target, already);						
		}
		return true;
	}
	
	@Override
	public void addAssumed() {		
		TokenFactory<T> end = new TFEnd<T>("end");
		this.symbols.put("end", end);
		FactorySupplyRule<T> fsr = new FSRCustom<T>(end);
		this.dv.addTopRule("end", fsr);
	}

	@Override
	public void update(Class<?> cls) throws ParseException {
		String errorSymbols = "";
		for (String name : this.dv.getMissing()) {
			errorSymbols += ", " + name;								
		}
				
		if (! errorSymbols.isEmpty()) {
			throw new ParseException("Following symbols are not resolved: " + errorSymbols.substring(1));			
		}
		
		for (FactorySupplyRule<T> r : this.dv.toBeUpdated) {
			boolean result = r.update();			
			assert(result);
		}
	}		
}

