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

package com.pwc.us.rgi.m.tool.entry.quittype;

import com.pwc.us.rgi.m.parsetree.DoBlock;
import com.pwc.us.rgi.m.parsetree.ForLoop;
import com.pwc.us.rgi.m.parsetree.QuitCmd;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.parsetree.data.FanoutWithLocation;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorder;
import com.pwc.us.rgi.m.struct.CodeLocation;

public class QTRecorder extends BlockRecorder<FanoutWithLocation, QTBlockData> {
	private boolean inForLoop;
	
	@Override
	public void reset() {
		super.reset();
		this.inForLoop = false;
	}
	
	@Override
	protected void visitQuit(QuitCmd quitCmd) {
		super.visitQuit(quitCmd);
		if (this.inForLoop) return;
		QTBlockData data = this.getCurrentBlockData();
		QuitType qt = data.getQuitType();
		CodeLocation cl = this.getCodeLocation();
		if (quitCmd.hasArgument()) {
			qt.markQuitWithValue(cl);
		} else {
			qt.markQuitWithoutValue(cl);
		}
	}

	@Override
	protected void visitForLoop(ForLoop forLoop) {
		this.inForLoop = true;
		super.visitForLoop(forLoop);
		this.inForLoop = false;
	}
	
	@Override
	protected void visitDoBlock(DoBlock doBlock) {
	}

	@Override
	protected QTBlockData getNewBlockData(EntryId entryId, String[] params) {
		return new QTBlockData(entryId);
	}
	
	@Override
	protected FanoutWithLocation getFanout(EntryId id, FanoutType type) {
		CodeLocation codeLocation = this.getCodeLocation();
		return new FanoutWithLocation(id, type, codeLocation);
	}
}
