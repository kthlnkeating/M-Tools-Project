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

import java.util.ArrayList;
import java.util.List;

import com.pwc.us.rgi.parsergen.ObjectSupply;
import com.pwc.us.rgi.struct.Pair;

public class TFForkedSequence<T extends Token> extends TokenFactory<T> {
	private TokenFactory<T> leader;
	private List<Pair<TFSequence<T>, Adapter<T>>> followers;
	private Adapter<T> singleAdapter;
	
	public TokenFactory<T> getLeader() {
		return this.leader;
	}
	
	public List<Pair<TFSequence<T>, Adapter<T>>> getFollowers() {
		return this.followers;
	}
	
	public TFForkedSequence(String name) {
		super(name);
	}

	public TFForkedSequence(String name, TokenFactory<T> leader) {
		super(name);
		this.leader = leader;
	}
	
	public void addSequence(TFSequence<T> sequence, Adapter<T> adapter) {
		if (this.followers == null) {
			this.followers = new ArrayList<Pair<TFSequence<T>, Adapter<T>>>();
		}
		Pair<TFSequence<T>, Adapter<T>> e = new Pair<TFSequence<T>, Adapter<T>>(sequence, adapter);
		this.followers.add(e);
	}
	
	public void setLeader(TokenFactory<T> leader) {
		this.leader = leader;
	}
	
	public void setSingleAdapter(Adapter<T> adapter) {
		this.singleAdapter = adapter;
	}
	
	private int getMaxSequenceCount() {
		int result = 0;
		for (Pair<TFSequence<T>, Adapter<T>> pair : this.followers) {
			int count = pair.first.getSequenceCount();
			if (count > result) return result;
		}
		return result;
	}
	
	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) throws SyntaxErrorException {
		T leading = this.leader.tokenize(text, objectSupply);
		if (leading == null) {
			return null;
		}
		SequenceOfTokens<T> foundTokens = new SequenceOfTokens<T>(this.getMaxSequenceCount());
		foundTokens.addToken(leading);
		if (text.onChar()) {
			int textIndex = text.getIndex();
			for (Pair<TFSequence<T>, Adapter<T>> pair : this.followers) {
				foundTokens.resetIndex(1);
				TFSequence<T> follower = pair.first;
				SequenceOfTokens<T> result = follower.tokenizeCommon(text, objectSupply, 1, foundTokens, true);
				if (result != null) {
					T t0th = result.getToken(0);
					T adapted = pair.second.adapt(t0th);
					foundTokens.setToken(0, adapted);
					foundTokens.setLength(follower.getSequenceCount());
					return follower.convertSequence(result, objectSupply);
				}
				text.resetIndex(textIndex);				
			}
		} else {
			for (Pair<TFSequence<T>, Adapter<T>> pair : this.followers) {
				if (pair.first.validateEnd(0, foundTokens, true)) {
					return pair.first.convertSequence(foundTokens, objectSupply);
				}
			}
		}
		if (this.singleAdapter != null) {
			return this.singleAdapter.adapt(leading);
		}
		throw new SyntaxErrorException();
	}
}
