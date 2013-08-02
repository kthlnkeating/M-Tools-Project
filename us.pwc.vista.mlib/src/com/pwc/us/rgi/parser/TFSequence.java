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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFSequence<T extends Token> extends TokenFactory<T> {
	public enum ValidateResult {
		CONTINUE, BREAK, NULL_RESULT
	}

	private final static class RequiredFlags {
		private List<Boolean> flags;
		private int firstRequired = Integer.MAX_VALUE;
		private int lastRequired = Integer.MIN_VALUE;

		public RequiredFlags() {
			this.flags = new ArrayList<Boolean>();
		}

		public RequiredFlags(int size) {
			this.flags = new ArrayList<Boolean>(size);
		}

		public void add(boolean b) {
			int n = this.flags.size();			
			this.flags.add(b);
			if (b) {
				if (this.firstRequired == Integer.MAX_VALUE) {
					this.firstRequired = n;
				}
				this.lastRequired = n;
			}
		}
		
		public int getFirstRequiredIndex() {
			return this.firstRequired;
		}
		
		public int getLastRequiredIndex() {
			return this.lastRequired;
		}
		
		public boolean isRequired(int i) {
			return this.flags.get(i);
		}		
	}
	
	private List<TokenFactory<T>> factories = new ArrayList<TokenFactory<T>>();
	private RequiredFlags requiredFlags = new RequiredFlags();

	private Constructor<? extends T> constructor;
	
	public TFSequence(String name) {		
		super(name);
	}
	
	public TFSequence(String name, int length) {		
		super(name);
		this.factories = new ArrayList<TokenFactory<T>>(length);
		this.requiredFlags = new RequiredFlags(length);
	}
	
	public void reset(int length) {
		this.factories = new ArrayList<TokenFactory<T>>(length);
		this.requiredFlags = new RequiredFlags(length);		
	}
	
	public void add(TokenFactory<T> tf, boolean required) {
		this.factories.add(tf);
		this.requiredFlags.add(required);
	}
	
	public int getSequenceCount() {
		return this.factories.size();
	}
		
	protected ValidateResult validateNull(int seqIndex, SequenceOfTokens<T> foundTokens, boolean noException) throws SyntaxErrorException {
		int firstRequired = this.requiredFlags.getFirstRequiredIndex();
		int lastRequired = this.requiredFlags.getLastRequiredIndex();
		
		if ((seqIndex < firstRequired) || (seqIndex > lastRequired)) {
			return ValidateResult.CONTINUE;
		}		
		if (seqIndex == firstRequired) {
			if (noException) return ValidateResult.NULL_RESULT;
			if (! foundTokens.isAllNull()) {
				throw new SyntaxErrorException();
			}
			return ValidateResult.NULL_RESULT;
		}
		if (this.requiredFlags.isRequired(seqIndex)) {
			if (noException) return ValidateResult.NULL_RESULT;
			throw new SyntaxErrorException();
		} else {
			return ValidateResult.CONTINUE;
		}
	}
	
	protected boolean validateEnd(int seqIndex, SequenceOfTokens<T> foundTokens, boolean noException) throws SyntaxErrorException {
		if (seqIndex < this.requiredFlags.getLastRequiredIndex()) {
			if (noException) return false;
			throw new SyntaxErrorException();
		}
		return true;
	}
	
	@Override
	public final T tokenize(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		SequenceOfTokens<T> foundTokens = this.tokenizeCommon(text, objectSupply);
		return this.convertSequence(foundTokens, objectSupply);
	}
	
	public void setSequenceTargetType(Constructor<? extends T> constructor) {
		this.constructor = constructor;		
	}
	
	public T convertSequence(SequenceOfTokens<T> tokens, ObjectSupply<T> objectSupply) {
		if (tokens == null) return null;
		if (this.constructor == null) {
			return objectSupply.newSequence(tokens);
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
	
	final public SequenceOfTokens<T> tokenizeCommon(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		if (text.onChar()) {
			int length = this.factories.size();
			SequenceOfTokens<T> foundTokens = new SequenceOfTokens<T>(length);
			return this.tokenizeCommon(text, objectSupply, 0, foundTokens, false);
		} else {
			return null;
		}
	}
	
	final SequenceOfTokens<T> tokenizeCommon(Text text, ObjectSupply<T> objectSupply, int firstSeqIndex, SequenceOfTokens<T> foundTokens, boolean noException) throws SyntaxErrorException {
		int factoryCount = this.factories.size();
		for (int i=firstSeqIndex; i<factoryCount; ++i) {
			TokenFactory<T> factory = this.factories.get(i);
			T token = null;
			try {
				token = factory.tokenize(text, objectSupply);				
			} catch (SyntaxErrorException e) {
				if (noException) return null;
				throw e;
			}
			
			if (token == null) {
				ValidateResult vr = this.validateNull(i, foundTokens, noException);
				if (vr == ValidateResult.BREAK) break;
				if (vr == ValidateResult.NULL_RESULT) return null;
			}

			foundTokens.addToken(token);
			if (token != null) {				
				if (text.onEndOfText() && (i < factoryCount-1)) {
					if (! this.validateEnd(i, foundTokens, noException)) return null;
					break;
				}
			}
		}
		if (foundTokens.isAllNull()) return null;
		return foundTokens;
	}
	
	public TokenFactory<T> getFactory(int index) {
		return this.factories.get(index);
	}
}
