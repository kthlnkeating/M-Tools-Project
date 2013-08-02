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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwc.us.rgi.m.parsetree.AtomicDo;
import com.pwc.us.rgi.m.parsetree.AtomicGoto;
import com.pwc.us.rgi.m.parsetree.Extrinsic;
import com.pwc.us.rgi.m.parsetree.InnerEntryList;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.struct.LineLocation;
import com.pwc.us.rgi.struct.Filter;

public class FanoutRecorder extends LocationMarker {
	private Map<LineLocation, List<EntryId>> fanouts;
	private Filter<EntryId> filter;
	private InnerEntryList lastInnerEntryList;
	
	public FanoutRecorder() {
	}
	
	public FanoutRecorder(Filter<EntryId> filter) {
		this.filter = filter;
	}
	
	protected void updateFanout(EntryId fanoutId) {
		if (fanoutId != null) {
			if (this.filter != null) {
				if (! this.filter.isValid(fanoutId)) return;
			}			
			LineLocation location = this.getLastLocation();
			List<EntryId> fanoutsOnLocation = this.fanouts.get(location);
			if (fanoutsOnLocation == null) {
				fanoutsOnLocation = new ArrayList<EntryId>();
				this.fanouts.put(location, fanoutsOnLocation);
			}
			fanoutsOnLocation.add(fanoutId);
		}
	}
		
	@Override
	protected void visitAtomicDo(AtomicDo atomicDo) {
		super.visitAtomicDo(atomicDo);		
		this.updateFanout(atomicDo.getFanoutId());
	}
	
	@Override
	protected void visitAtomicGoto(AtomicGoto atomicGoto) {
		super.visitAtomicGoto(atomicGoto);
		this.updateFanout(atomicGoto.getFanoutId());
	}
	
	@Override
	protected void visitExtrinsic(Extrinsic extrinsic) {
		super.visitExtrinsic(extrinsic);
		this.updateFanout(extrinsic.getFanoutId());
	}
	
	@Override
	protected void visitInnerEntryList(InnerEntryList entryList) {
		if (entryList != this.lastInnerEntryList) {
			this.lastInnerEntryList = entryList;
			super.visitInnerEntryList(entryList);
		}
	}
		
	@Override
	protected void visitRoutine(Routine routine) {
		this.fanouts = new HashMap<LineLocation, List<EntryId>>();
		super.visitRoutine(routine);
	}

	public Map<LineLocation, List<EntryId>> getRoutineFanouts() {
		return this.fanouts;
	}
	
	public void setFilter(Filter<EntryId> filter) {
		this.filter = filter;
	}
	
	public Map<LineLocation, List<EntryId>> getFanouts(Routine routine) {
		routine.accept(this);
		return this.fanouts;
	}
}