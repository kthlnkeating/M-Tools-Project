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

import com.pwc.us.rgi.parser.Adapter;
import com.pwc.us.rgi.parser.AdapterIdentity;
import com.pwc.us.rgi.parser.Token;
import com.pwc.us.rgi.parsergen.AdapterSpecification;

public abstract class FSRBase<T extends Token> implements FactorySupplyRule<T> {
	@Override
	public boolean update() {
		return true;
	}

	@Override
	public FactorySupplyRule<T> getLeading(int level) {
		return this;
	}
	
	@Override
	public int getSequenceCount() {
		return 1;
	}

	@Override
	public void setAdapter(AdapterSpecification<T> spec) {
		spec.getNull();
	}
	
	@Override
	public Adapter<T> getAdapter() {
		return new AdapterIdentity<>();
	}
}
