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

import com.pwc.us.rgi.parser.Adapter;
import com.pwc.us.rgi.parser.TFForkedSequence;
import com.pwc.us.rgi.parser.TFSequence;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parser.TokenFactory;
import com.pwc.us.rgi.parsergen.ParseErrorException;

public class FSRForkedSequence<T extends Token> extends FSRBase<T> {
	private String name;
	private FactorySupplyRule<T> leader;
	private List<FSRSequence<T>> followers = new ArrayList<FSRSequence<T>>();
	private FactorySupplyRule<T> single;
	private TFForkedSequence<T> factory;

	public FSRForkedSequence(String name, FactorySupplyRule<T> leader) {
		this.name = name;
		this.leader = leader;
		this.factory = new TFForkedSequence<T>(name);
	}
	
	@Override
	public TokenFactory<T> getShellFactory() {
		return this.factory;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public FactorySupplyRule<T> getLeading(int level) {
		return null;
	}
	
	public int getSequenceCount() {
		return 1;
	}
	
	private void addNonSequence(FactorySupplyRule<T> single) {
		if (this.single != null) {
			throw new ParseErrorException("Cannot have two rules that are not sequences in the same rule");
		}
		this.single = single;
	}
	
	private void addSequence(FSRSequence<T> sequence) {
		this.followers.add(sequence);
	}
	
	public void add(FactorySupplyRule<T> follower) {
		if (follower.getSequenceCount() == 1) {
			this.addNonSequence(follower);
		} else {
			FSRSequence<T> sequence = (FSRSequence<T>) follower;
			this.addSequence(sequence);
		}
	}
	
	@Override
	public boolean update() {
		this.factory.setLeader(this.leader.getShellFactory());
		if (this.single != null) {
			this.factory.setSingleAdapter(this.single.getAdapter());
		}
		for (FSRSequence<T> follower : this.followers) {
			TFSequence<T> tf = follower.getShellFactory();
			Adapter<T> a = follower.get(0).getAdapter();
			this.factory.addSequence(tf, a);
		}		
		return true;
	}
}
