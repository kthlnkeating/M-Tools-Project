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

package com.pwc.us.rgi.parser;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFDelimitedList<T extends Token> extends TokenFactory<T> {
	private TFSequence<T> effective;	
	private Constructor<? extends T> constructor;
	
	public TFDelimitedList(String name) {
		super(name);
	}
		
	private TokenFactory<T> getLeadingFactory(TokenFactory<T> element, TokenFactory<T> delimiter, boolean emptyAllowed) {
		if (emptyAllowed) {
			String elementName = this.getName() + "." + element.getName();
			String emptyName = this.getName() + "." + "empty";
			TFChoice<T> result = new TFChoice<T>(elementName);
			result.add(element);
			result.add(new TFEmpty<T>(emptyName, delimiter));
			return result;
		} else {
			return element;
		}
	}
	
	public void set(TokenFactory<T> element, TokenFactory<T> delimiter, boolean emptyAllowed) {
		TokenFactory<T> leadingElement = this.getLeadingFactory(element, delimiter, emptyAllowed);
		String tailElementName = this.getName() + "." + "tailelement";
		TFSequence<T> tailElement = new TFSequence<T>(tailElementName, 2);
		tailElement.add(delimiter, true);
		tailElement.add(emptyAllowed ? leadingElement : element, !emptyAllowed);
		String tailListName = this.getName() + "." + "taillist";
		TokenFactory<T> tail = new TFList<T>(tailListName, tailElement);
		String name = this.getName() + "." + "effective";
		this.effective = new TFSequence<T>(name,2);
		this.effective.add(leadingElement, true);
		this.effective.add(tail, false);
	}
	
	public void set(TokenFactory<T> element, TokenFactory<T> delimiter) {
		this.set(element, delimiter, false);
	}

	public T convert(ObjectSupply<T> objectSupply, DelimitedListOfTokens<T> tokens) {
		if (tokens == null) return null;
		if (this.constructor == null) {
			return objectSupply.newDelimitedList(tokens);			
		} else {
			try {
				return this.constructor.newInstance(tokens);						
			} catch (Throwable t) {
				String clsName =  this.getClass().getName();
				Logger.getLogger(clsName).log(Level.SEVERE, "Unable to instantiate " + clsName + ".", t);			
			}
			return null;			
		}
	}
		
	public DelimitedListOfTokens<T> tokenizeCommon(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		if (this.effective == null) {
			throw new IllegalStateException("TFDelimitedList.set needs to be called before TFDelimitedList.tokenize");
		} else {
			SequenceOfTokens<T> internalResult = this.effective.tokenizeCommon(text, objectSupply);
			if (internalResult == null) {
				return null;
			} else {
				T leadingToken = internalResult.getToken(0);
				Tokens<T> tailTokens = internalResult.getTokens(1);
				if (tailTokens == null) {
					return new DelimitedListOfTokens<T>(leadingToken, null);
				} else {
					int lastIndex = tailTokens.size() - 1;
					Tokens<T> lastToken = tailTokens.getTokens(lastIndex);
					if (lastToken.getToken(1) == null) {
						lastToken.setToken(1, objectSupply.newEmpty());
					}
					return new DelimitedListOfTokens<T>(leadingToken, tailTokens);
				}
			}
		}		
	}
	
	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		DelimitedListOfTokens<T> result = this.tokenizeCommon(text, objectSupply);
		return this.convert(objectSupply, result);
	}
	
	public void setDelimitedListTargetType(Constructor<? extends T> constructor) {
		this.constructor = constructor;
	}
}
