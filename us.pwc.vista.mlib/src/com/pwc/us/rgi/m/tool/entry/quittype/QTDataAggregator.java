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

import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.parsetree.data.FanoutWithLocation;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.entry.Block;
import com.pwc.us.rgi.m.tool.entry.BlocksSupply;
import com.pwc.us.rgi.m.tool.entry.RecursiveDataAggregator;

public class QTDataAggregator extends RecursiveDataAggregator<QuitType, FanoutWithLocation, QTBlockData> {
	public QTDataAggregator(Block<FanoutWithLocation, QTBlockData> block, BlocksSupply<FanoutWithLocation, QTBlockData> supply) {
		super(block, supply);
	}

	@Override
	protected QuitType getNewDataInstance(QTBlockData data) {
		QuitType result = new QuitType(data.getQuitType());
		List<FanoutWithLocation> fs = data.getFanouts();
		for (FanoutWithLocation f : fs) {
			EntryId id = f.getEntryId();
			FanoutType type = f.getType();
			CodeLocation location = f.getCodeLocation();
			switch (type) {
			case DO:
				result.addFanout(id, new CallType(CallTypeState.DO_UNVERIFIED, location));
				break;
			case EXTRINSIC:
				result.addFanout(id, new CallType(CallTypeState.EXTRINSIC_UNVERIFIED, location));
				break;
			default:
				break;
			}
		}
		return result;
	}
	
	@Override
	protected int updateData(QTBlockData targetBlockData, QuitType targetData, QuitType sourceData, int indexInTarget) {
		FanoutWithLocation fwl = targetBlockData.getFanout(indexInTarget);
		FanoutType ft = fwl.getType();
		EntryId id = fwl.getEntryId();
		int result = 0;
		switch (ft) {
		case DO:
		case EXTRINSIC:
			CallType ct = targetData.getFanout(id);
			return ct.updateFromFanout(sourceData);
		case GOTO:
		case ASSUMED_GOTO:
			result = targetData.markQuitFromGoto(sourceData, fwl.getCodeLocation());
		default:
			result += targetData.updateCallTypes(sourceData);
		}
		return result;
	}		
}
