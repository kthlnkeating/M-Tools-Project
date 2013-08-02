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

import com.pwc.us.rgi.m.parsetree.AtomicDo;
import com.pwc.us.rgi.m.parsetree.AtomicGoto;
import com.pwc.us.rgi.m.parsetree.DeadCmds;
import com.pwc.us.rgi.m.parsetree.DoBlock;
import com.pwc.us.rgi.m.parsetree.Extrinsic;
import com.pwc.us.rgi.m.parsetree.InnerEntryList;
import com.pwc.us.rgi.m.parsetree.Label;
import com.pwc.us.rgi.m.parsetree.Line;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlockData;
import com.pwc.us.rgi.struct.HierarchicalMap;
public abstract class BlockRecorder<F extends Fanout, T extends BlockData<F>> extends LocationMarker {
	private HierarchicalMap<String, Block<F, T>> currentBlocks;
	private T currentBlockData;
	private String currentRoutineName;
	private InnerEntryList lastInnerEntryList;
	private int lineIndex;

	protected T getCurrentBlockData() {
		return this.currentBlockData;
	}
	
	protected String getCurrentRoutineName() {
		return this.currentRoutineName;
	}
	
	protected CodeLocation getCodeLocation() {
		return new CodeLocation(this.currentRoutineName, this.lineIndex);
	}
	
	public void reset() {
		this.currentBlocks = null;
		this.currentBlockData = null;
		this.currentRoutineName = null;
		this.lastInnerEntryList = null;
	}
	
	protected void updateFanout(EntryId fanoutId, FanoutType type) {
		if (fanoutId != null) {
			fanoutId.localize(this.currentRoutineName);
			F fo = this.getFanout(fanoutId, type);
			this.currentBlockData.addFanout(fo);	
		} 
	}

	@Override
	protected void visitAtomicDo(AtomicDo atomicDo) {
		super.visitAtomicDo(atomicDo);		
		this.updateFanout(atomicDo.getFanoutId(), FanoutType.DO);
	}
	
	@Override
	protected void visitAtomicGoto(AtomicGoto atomicGoto) {
		super.visitAtomicGoto(atomicGoto);
		this.updateFanout(atomicGoto.getFanoutId(), FanoutType.GOTO);
	}
	
	@Override
	protected void visitExtrinsic(Extrinsic extrinsic) {
		super.visitExtrinsic(extrinsic);
		this.updateFanout(extrinsic.getFanoutId(), FanoutType.EXTRINSIC);
	}
	
	@Override
	protected void visitAssumedGoto(Label fromEntry, Label toEntry) {
		super.visitAssumedGoto(fromEntry, toEntry);
		String tag = toEntry.getName();
		EntryId assumedGoto = new EntryId(null, tag);
		this.updateFanout(assumedGoto, FanoutType.ASSUMED_GOTO);
	}
	
	@Override
	protected void visitLine(Line line) {
		this.lineIndex = line.getLineIndex();
		super.visitLine(line);
	}
	
	protected abstract T getNewBlockData(EntryId entryId, String[] params);
	
	protected abstract F getFanout(EntryId id, FanoutType type); 
 	
	@Override
	protected void visitDeadCmds(DeadCmds deadCmds) {
		if (deadCmds.getLevel() > 0) {
			super.visitDeadCmds(deadCmds);
		}
	}
	
	@Override
	protected void visitLabel(Label entry) {
		String tag = entry.getName();
		EntryId entryId = entry.getFullEntryId();		
		this.currentBlockData = this.getNewBlockData(entryId, entry.getParameters());
		Block<F, T> b = new Block<F, T>(this.currentBlocks, this.currentBlockData);
		this.currentBlocks.put(tag, b);
		super.visitLabel(entry);
	}
			
	@Override
	protected void visitDoBlock(DoBlock doBlock) {
		int parentLineIndex = this.lineIndex;
		super.visitDoBlock(doBlock);
		this.lineIndex = parentLineIndex;
	}

	@Override
	protected void visitInnerEntryList(InnerEntryList entryList) {
		if (entryList != this.lastInnerEntryList) {
			this.lastInnerEntryList = entryList;
			EntryId defaultDo = new EntryId(null, entryList.getName());
			this.updateFanout(defaultDo, FanoutType.DO_BLOCK);
									
			HierarchicalMap<String, Block<F, T>> lastBlocks = this.currentBlocks;
			T lastBlock = this.currentBlockData;
			this.currentBlockData = null;
			this.currentBlocks = new HierarchicalMap<String, Block<F, T>>(lastBlocks);
			super.visitInnerEntryList(entryList);
			this.currentBlocks = lastBlocks;
			this.currentBlockData = lastBlock;
		}
	}
		
	public HierarchicalMap<String, Block<F, T>> getBlocks() {
		return this.currentBlocks;
	}
	
	@Override
	protected void visitRoutine(Routine routine) {
		this.reset();
		this.currentBlocks = new HierarchicalMap<String, Block<F, T>>();
		this.currentRoutineName = routine.getName();
		super.visitRoutine(routine);
	}
}
