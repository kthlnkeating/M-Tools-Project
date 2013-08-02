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

package com.pwc.us.rgi.m.tool.entry.fanin;

import com.pwc.us.rgi.m.parsetree.AtomicDo;
import com.pwc.us.rgi.m.parsetree.AtomicGoto;
import com.pwc.us.rgi.m.parsetree.Extrinsic;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorder;

public class MarkedAsFaninBR extends BlockRecorder<Fanout, FaninMark> {
	private EntryId entryId;
	
	public MarkedAsFaninBR(EntryId entryId) {
		this.entryId = entryId;
	}

	protected void updateMark(EntryId fanout) {
		if ((fanout != null) && this.entryId.equals(fanout, this.getCurrentRoutineName())) {
			FaninMark b = this.getCurrentBlockData();
			b.set(this.entryId);
		}
	}
	
	@Override
	protected void visitAtomicDo(AtomicDo atomicDo) {
		super.visitAtomicDo(atomicDo);		
		this.updateMark(atomicDo.getFanoutId());
	}
	
	@Override
	protected void visitAtomicGoto(AtomicGoto atomicGoto) {
		super.visitAtomicGoto(atomicGoto);
		this.updateMark(atomicGoto.getFanoutId());
	}
	
	@Override
	protected void visitExtrinsic(Extrinsic extrinsic) {
		super.visitExtrinsic(extrinsic);
		this.updateMark(extrinsic.getFanoutId());
	}

	@Override
	protected FaninMark getNewBlockData(EntryId entryId, String[] params) {
		return new FaninMark(entryId);
	}

	@Override
	protected Fanout getFanout(EntryId id, FanoutType type) {
		return new Fanout(id, type);
	}
}
	
