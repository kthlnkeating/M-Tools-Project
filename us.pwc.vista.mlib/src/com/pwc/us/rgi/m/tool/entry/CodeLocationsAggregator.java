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

package com.pwc.us.rgi.m.tool.entry;

import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.struct.CodeLocation;

public class CodeLocationsAggregator extends AdditiveDataAggregator<CodeLocations, Fanout, CodeLocations> {
	public CodeLocationsAggregator(Block<Fanout, CodeLocations> block, BlocksSupply<Fanout, CodeLocations> supply) {
		super(block, supply);
	}
	
	protected CodeLocations getNewDataInstance(Block<Fanout, CodeLocations> block) {
		return new CodeLocations(block.getEntryId());
	}
	
	protected void updateData(CodeLocations targetData, CodeLocations fanoutData) {
		List<CodeLocation> cls = fanoutData.getCodeLocations();
		if (cls != null) {
			for (CodeLocation c : cls) {
				targetData.add(c);
			}
		}
	}		
}
