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

package com.pwc.us.rgi.m.parsetree.visitor;

import com.pwc.us.rgi.m.parsetree.CacheClassMethod;
import com.pwc.us.rgi.m.parsetree.CacheObjectDoRoutine;
import com.pwc.us.rgi.m.parsetree.CacheSystemCall;
import com.pwc.us.rgi.m.parsetree.ObjectMethodCall;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.Visitor;

public class CacheOccuranceRecorder extends Visitor  {
	private int numOccurance;
	
	public int getNumOccurance() {
		return this.numOccurance;
	}
	
	public void reset() {
		this.numOccurance = 0;
	}
	
	@Override
	protected void visitCacheClassMethod(CacheClassMethod ccm) {
		++this.numOccurance;
		super.visitCacheClassMethod(ccm);
	}

	@Override
	protected void visitCacheSystemCall(CacheSystemCall csc) {
		++this.numOccurance;
		super.visitCacheSystemCall(csc);
	}

	@Override
	protected void visitCacheObjectDoRoutine(CacheObjectDoRoutine codr) {
		++this.numOccurance;
		super.visitCacheObjectDoRoutine(codr);
	}

	
	@Override
	protected void visitObjectMethodCall(ObjectMethodCall omc) {
		++this.numOccurance;
		super.visitObjectMethodCall(omc);
	}

	@Override
	public void visitRoutine(Routine routine) {
		super.visitRoutine(routine);
	}
}
