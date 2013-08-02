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

package com.pwc.us.rgi.parsergen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;

public abstract class TokenFactoryStore<T extends Token> {		
	protected abstract TokenFactory<T> add(Field f, Class<T> tokenCls);
	
	protected abstract <M> boolean handleField(M target, Field f, Class<T> tokenCls) throws IllegalAccessException;

	private <M> void handleWithRemaining(M target, Field f, Set<String> remainingNames, java.util.List<Field> remaining, Class<T> tokenCls) throws IllegalAccessException{
		String name = f.getName();
		if (remainingNames.contains(name)) {
			remaining.add(f);							
			return;
		}
		if (! this.handleField(target, f, tokenCls)) {
			remainingNames.add(name);
			remaining.add(f);
		}			
	}
	
	public <M> void add(M target, Class<T> tokenCls) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
		Set<String> remainingNames = new HashSet<String>();
		java.util.List<Field> remaining = new ArrayList<Field>();
		Class<?> cls = target.getClass();
		while (! cls.equals(Object.class)) {
			for (Field f : cls.getDeclaredFields()) {
				if (TokenFactory.class.isAssignableFrom(f.getType())) {
					this.handleWithRemaining(target, f, remainingNames, remaining, tokenCls);
				}
			}
			cls = cls.getSuperclass();
		}
		while (remaining.size() > 0) {
			remainingNames = new HashSet<String>();
			java.util.List<Field> loopRemaining = new ArrayList<Field>();
			for (Field f : remaining) {
				this.handleWithRemaining(target, f, remainingNames, loopRemaining, tokenCls);
			}
			if (remaining.size() == loopRemaining.size()) {
				String symbols = "";
				for (Field f : remaining) {
					symbols += ", " + f.getName();
				}
				throw new ParseErrorException("Following symbols are not resolved: " + symbols.substring(1));
			}
			remaining = loopRemaining;
		}			
	}
	
	public abstract void addAssumed();

	public abstract void update(Class<?> cls) throws ParseException;
}

