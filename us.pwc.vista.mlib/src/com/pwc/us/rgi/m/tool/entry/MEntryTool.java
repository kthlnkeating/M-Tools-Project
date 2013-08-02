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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.Fanout;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorderFactory;
import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.MToolError;
import com.pwc.us.rgi.m.tool.MToolErrorType;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.struct.Filter;
import com.pwc.us.rgi.struct.HierarchicalMap;

public abstract class MEntryTool<T extends ToolResult, F extends Fanout, B extends BlockData<F>> {
	protected BlocksSupply<F, B> blocksSupply;
	private Filter<Fanout> filter;
	
	protected MEntryTool(CommonToolParams params) {
		BlockRecorderFactory<F, B> f = this.getBlockRecorderFactory();
		this.blocksSupply = params.getBlocksSupply(f);
		this.filter = params.getFanoutFilter();
	}

	protected abstract BlockRecorderFactory<F, B> getBlockRecorderFactory();
	
	protected abstract T getResult(Block<F, B> block, Filter<Fanout> filter, Set<EntryId> missing);
	
	public T getResult(EntryId entryId) {
		Block<F, B> b = this.blocksSupply.getBlock(entryId);
		Set<EntryId> missing = new HashSet<EntryId>();
		T r = this.getResult(b, this.filter, missing);	
		return r;
	}
	
	public MEntryToolResult<T> getResult(List<EntryId> entryIds) {
		MEntryToolResult<T> result = new MEntryToolResult<T>();
		for (EntryId entryId : entryIds) {
			Block<F, B> b = this.blocksSupply.getBlock(entryId);
			if (b == null) {
				MToolError error = new MToolError(MToolErrorType.LABEL_NOT_FOUND, new String[]{entryId.toString()});
				result.addError(entryId, error);
			} else {
				Set<EntryId> missing = new HashSet<EntryId>();
				T r = this.getResult(b, this.filter, missing);	
				result.add(entryId, r);
				if (missing.size() > 0) {
					result.setMissingEntries(missing);
				}
			}			
		}
		return result;
	}

	public MEntryToolResult<T> getResultForRoutines(List<String> routineNames) {
		MEntryToolResult<T> result = new MEntryToolResult<T>();
		for (String routineName : routineNames) {
			HierarchicalMap<String, Block<F, B>> hm = this.blocksSupply.getBlocks(routineName);
			if (hm == null) {
				MToolError error = new MToolError(MToolErrorType.ROUTINE_NOT_FOUND, new String[]{routineName});
				result.addError(new EntryId(routineName, null), error);
				continue;
			}
			Set<String> labels = hm.keySet();
			List<EntryId> entryIds = new ArrayList<EntryId>(labels.size());
			for (String label : labels) {
				EntryId entryId = new EntryId(routineName, label);
				entryIds.add(entryId);
			}
			MEntryToolResult<T> subResults = this.getResult(entryIds);
			result.add(subResults);
		}		
		return result;
	}
	
	public MEntryToolResult<T> getResult(MEntryToolInput input) {		
		List<String> routineNames = input.getRoutineNames();
		MEntryToolResult<T> result = this.getResultForRoutines(routineNames);
		List<EntryId> entryIds = input.getEntryIds();
		MEntryToolResult<T> resultEntries = this.getResult(entryIds);
		result.add(resultEntries);
		return result;		
	}
}
