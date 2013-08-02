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

import com.pwc.us.rgi.m.parsetree.Line;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.Visitor;
import com.pwc.us.rgi.m.struct.LineLocation;

public class LocationMarker extends Visitor {
	private LineLocation lastLocation;
	private String lastRoutineName;
	
	@Override
	protected void visitLine(Line line) {
		String tag = line.getTag();
		int index = line.getIndex();
		this.lastLocation = new LineLocation(tag, index);
		super.visitLine(line);
	}

	protected LineLocation getLastLocation() {
		return this.lastLocation;
	}

	@Override
	protected void visitRoutine(Routine routine) {
		this.lastRoutineName = routine.getName();
		this.lastLocation = null;
		super.visitRoutine(routine);
	}

	protected String getLastRoutineName() {
		return this.lastRoutineName;
	}
}
