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

package com.pwc.us.rgi.m.parsetree.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.struct.Indexed;

public class TagLocations {
	private List<Indexed<String>> locations = new ArrayList<Indexed<String>>();	
	boolean needsSorting = true;
	
	public void add(String tag, int lineIndex) {
		this.locations.add(new Indexed<String>(tag, lineIndex));
		this.needsSorting = true;
	}
	
	public void update(TagLocations rhs) {
		for (Indexed<String> rhsLocation : rhs.locations) {
			this.add(rhsLocation.getObject(), rhsLocation.getIndex());
			this.needsSorting = true;
		}
	}

	public LineLocation getLineLocation(int lineIndex) {
		if (this.needsSorting) {
			Collections.sort(this.locations);
			this.needsSorting = false;
		}
		int i = Collections.binarySearch(this.locations, new Indexed<>("", lineIndex));
		if (i >= 0) {
			Indexed<String> result = this.locations.get(i);
			return new LineLocation(result.getObject(), 0);
		} else if (i == -1){
			return new LineLocation(null, lineIndex);
		} else {
			i = - i - 2;
			Indexed<String> result = this.locations.get(i);
			return new LineLocation(result.getObject(), lineIndex - result.getIndex());			
		}
	}
}
