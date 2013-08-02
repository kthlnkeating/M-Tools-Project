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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.pwc.us.rgi.parser.DelimitedListOfTokens;
import com.pwc.us.rgi.parser.ListOfTokens;
import com.pwc.us.rgi.parser.SequenceOfTokens;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Token;

public class AdapterSpecification<T extends Token> {
	private Constructor<? extends T> token;
	private Constructor<? extends T> string;
	private Constructor<? extends T> list;
	private Constructor<? extends T> delimitedList;
	private Constructor<? extends T> sequence;

	private int count;
	
	protected static <M, N> Constructor<M> getConstructor(String name, Class<M> cls, Class<N> argument) {
		try {
			int modifiers = cls.getModifiers();
			if (! Modifier.isPublic(modifiers)) {
				throw new IllegalArgumentException(name + ": " + cls.getName() + " is not public.");
			}
			if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
				throw new IllegalArgumentException(name + ": " + cls.getName() + " is abstract.");
			}
			final Constructor<M> constructor = cls.getConstructor(argument);
			if (! Modifier.isPublic(constructor.getModifiers())) {
				throw new IllegalArgumentException(name + ": " + cls.getName() + " constructor (" + argument.getName() + ") is not public.");			
			}
			return constructor;
		} catch (NoSuchMethodException nsm) {
			throw new IllegalArgumentException(name + ": " + cls.getName() + " does not have a constructor that accepts " + argument.getName() + ".");
		}
	}

	public <M> M getNull() {
		if (this.count > 0) {
			throw new ParseErrorException("Uncompatible adapter type.");
		}
		return null;
	}
	
	public Constructor<? extends T> getTokenAdapter() {
		if (this.token != null) {
			return this.token;
		}
		return getNull();
	}
	
	public Constructor<? extends T> getStringTokenAdapter() {
		if (this.string != null) {
			return this.string;
		}
		return getNull();
	}
	
	public Constructor<? extends T> getListTokenAdapter() {
		if (this.list != null) {
			return this.list;
		}
		return getNull();
	}
	
	public Constructor<? extends T> getDelimitedListTokenAdapter() {
		if (this.delimitedList != null) {
			return this.delimitedList;
		}
		return getNull();
	}
	
	public Constructor<? extends T> getSequenceTokenAdapter() {
		if (this.sequence != null) {
			return this.sequence;
		}
		return getNull();
	}
	
	private Class<? extends T> addGeneric(Class<? extends Token> raw, Class<T> actualTokencls) {
		if (actualTokencls.isAssignableFrom(raw)) {
			@SuppressWarnings("unchecked")
			Class<? extends T> local = (Class<? extends T>) raw;
			++this.count;
			return local;
		}
		throw new ParseErrorException(raw.getName() + " is not a sub class of " + actualTokencls.getName() + ".");
	}
	
	public void addCopy(Field f, Class<T> actualTokencls) {
		TokenType tokenType = f.getAnnotation(TokenType.class);
		if (tokenType != null) {
			Class<? extends T> t = this.addGeneric(tokenType.value(), actualTokencls);
			Constructor<? extends T> constructor = getConstructor(f.getName(), t, actualTokencls);
			this.token = constructor;
		}		
	}
	
	public void addString(Field f, Class<T> actualTokencls) {
		StringTokenType stringTokenType = f.getAnnotation(StringTokenType.class);
		if (stringTokenType != null) {
			Class<? extends T> t = this.addGeneric(stringTokenType.value(), actualTokencls);
			Constructor<? extends T> constructor = getConstructor(f.getName(), t, TextPiece.class);
			this.string = constructor;
		}
	}
	
	public void addList(Field f, Class<T> actualTokencls) {
		ListTokenType listTokenType = f.getAnnotation(ListTokenType.class);
		if (listTokenType != null) {
			Class<? extends T> t = this.addGeneric(listTokenType.value(), actualTokencls);
			Constructor<? extends T> constructor = getConstructor(f.getName(), t, ListOfTokens.class);
			this.list = constructor;
		}
	}
	
	public void addSequence(Field f, Class<T> actualTokencls) {
		SequenceTokenType seqTokenType = f.getAnnotation(SequenceTokenType.class);
		if (seqTokenType != null) {
			Class<? extends T> t = this.addGeneric(seqTokenType.value(), actualTokencls);
			Constructor<? extends T> constructor = getConstructor(f.getName(), t, SequenceOfTokens.class);
			this.sequence = constructor;
		}
	}
	
	public void addDelimitedList(Field f, Class<T> actualTokencls) {
		DelimitedListTokenType dlTokenType = f.getAnnotation(DelimitedListTokenType.class);
		if (dlTokenType != null) {
			Class<? extends T> t = this.addGeneric(dlTokenType.value(), actualTokencls);
			try {
				Constructor<? extends T> constructor = getConstructor(f.getName(), t, DelimitedListOfTokens.class);
				this.delimitedList = constructor;
			} catch (Throwable e) {
				throw new ParseErrorException("Uncompatible adapter type.");			
			}
		}		
	}
	
	public static <T extends Token> AdapterSpecification<T> getInstance(Field f, Class<T> tokenCls) {
		AdapterSpecification<T> result = new AdapterSpecification<T>();
		result.addCopy(f, tokenCls);
		result.addString(f, tokenCls);
		result.addList(f, tokenCls);
		result.addSequence(f, tokenCls);
		result.addDelimitedList(f, tokenCls);
		
		if (result.count > 1) {
			throw new ParseErrorException("Multiple adapters are not allowed.");								
		}
		
		return result;
	}
}
