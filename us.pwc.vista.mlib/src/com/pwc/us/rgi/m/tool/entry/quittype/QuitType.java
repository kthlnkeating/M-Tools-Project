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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.struct.CodeLocation;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class QuitType implements ToolResult {
	private QuitTypeState state = QuitTypeState.NO_QUITS;
	private CodeLocation firstQuitLocation;
	private CodeLocation conflictingLocation;
	
	private Map<EntryId, CallType> fanoutCalls = new HashMap<EntryId, CallType>();
	
	public QuitType() {		
	}
	
	public QuitType(QuitType rhs) {
		this.state = rhs.state;
		this.firstQuitLocation = rhs.firstQuitLocation;
		this.conflictingLocation = rhs.conflictingLocation;
	}
	
	public boolean addFanout(EntryId id, CallType type) {
		CallType ct = this.fanoutCalls.get(id);
		if (ct == null) {
			this.fanoutCalls.put(id, type);
			return true;						
		} else {
			if (ct.getState() == type.getState()) {
				return false;
			} else {
				this.fanoutCalls.put(id, type);
				return true;
			}
		}
	}
	
	public CallType getFanout(EntryId id) {
		return this.fanoutCalls.get(id);
	}
		
	public Map<EntryId, CallType> getFanoutCalls() {
		if (this.fanoutCalls == null) {
			return Collections.emptyMap();
		} else {
			return this.fanoutCalls;
		}
	}
	
	public QuitTypeState getQuitTypeState() {
		return this.state;
	}

	public CodeLocation getFirstQuitLocation() {
		return this.firstQuitLocation;
	}
	
	public CodeLocation getConflictingLocation() {
		return this.conflictingLocation;
	}
	
	public void markQuitWithValue(CodeLocation location) {
		switch (this.state) {
		case NO_QUITS:
			this.state = QuitTypeState.QUITS_WITH_VALUE;
			this.firstQuitLocation = location;
			break;
		case QUITS_WITH_VALUE:
		case CONFLICTING_QUITS:
			break;
		default:
			this.state = QuitTypeState.CONFLICTING_QUITS;
			this.conflictingLocation = location;
			break;
		}
	}
	
	public void markQuitWithoutValue(CodeLocation location) {
		switch (this.state) {
		case NO_QUITS:
			this.state = QuitTypeState.QUITS_WITHOUT_VALUE;
			this.firstQuitLocation = location;
			break;
		case QUITS_WITHOUT_VALUE:
		case CONFLICTING_QUITS:
			break;
		default:
			this.state = QuitTypeState.CONFLICTING_QUITS;
			this.conflictingLocation = location;
			break;
		}		
	}
	
	public int markQuitFromGoto(QuitType gotoQuitType, CodeLocation location) {
		QuitTypeState gotoState = gotoQuitType.state;
		if (gotoState == QuitTypeState.NO_QUITS) {
			return 0;
		}
		if (this.state == QuitTypeState.CONFLICTING_QUITS) {
			return 0;
		}
		if ((this.state == QuitTypeState.NO_QUITS) || (gotoState == QuitTypeState.CONFLICTING_QUITS)) {
			this.state = gotoQuitType.state;
			this.firstQuitLocation = gotoQuitType.firstQuitLocation;
			this.conflictingLocation = gotoQuitType.conflictingLocation;
			return 1;		
		}
		if (this.state == QuitTypeState.QUITS_WITHOUT_VALUE) {
			if (gotoState == QuitTypeState.QUITS_WITH_VALUE) {
				this.state = QuitTypeState.CONFLICTING_QUITS;
				this.conflictingLocation = gotoQuitType.firstQuitLocation;
				return 1;
			}
			return 0;				
		}
		if (this.state == QuitTypeState.QUITS_WITH_VALUE) {
			if (gotoState == QuitTypeState.QUITS_WITHOUT_VALUE) {
				this.state = QuitTypeState.CONFLICTING_QUITS;
				this.conflictingLocation = gotoQuitType.firstQuitLocation;
				return 1;
			}
			return 0;				
		}
		return 0;
	}
	
	public int updateCallTypes(QuitType source) {
		int result = 0;
		if (source.fanoutCalls != null) {
			Set<EntryId> entryIds = source.fanoutCalls.keySet();
			for (EntryId entryId : entryIds) {
				CallType ct = source.fanoutCalls.get(entryId);
				boolean updated = this.addFanout(entryId, ct);
				if (updated) ++result;
			}
		}
		return result;
	}
	
	public boolean hasConflict() {
		if (this.state == QuitTypeState.CONFLICTING_QUITS) {
			return true;
		}
		for (CallType ct : this.fanoutCalls.values()) {
			if (ct.getState().isConflictingState()) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		boolean skipEmpty = flags.getSkipEmpty(false);		
		QuitTypeState qts = this.getQuitTypeState();
		switch (qts) {
			case NO_QUITS:
				if (! skipEmpty) {
					t.writeFormatted("QUIT", "No quits.");
				}
			break;
			case QUITS_WITH_VALUE:
				if (! skipEmpty) {
					String fl = this.getFirstQuitLocation().toString(); 
					t.writeFormatted("QUIT", "With value " + fl);
				}
			break;
			case QUITS_WITHOUT_VALUE:
				if (! skipEmpty) {
					String fl = this.getFirstQuitLocation().toString(); 
					t.writeFormatted("QUIT", "Without value " + fl);
				}
			break;
			case CONFLICTING_QUITS:
			{
				String fl = this.getFirstQuitLocation().toString(); 
				String cl = this.getConflictingLocation().toString();
				t.writeFormatted("QUIT", "Conflicted " + fl + " vs " + cl);
			}
			break;
		}
		for (CallType ct : this.getFanoutCalls().values()) {
			CallTypeState state = ct.getState();
			switch (state) {
			case DO_CONFLICTING:
			{
				String fl = ct.getLocation().toString(); 
				t.writeFormatted("CALL", "Invalid DO at " + fl);				
			}				
			break;
			case EXTRINSIC_CONFLICTING:
			{
				String fl = ct.getLocation().toString(); 
				t.writeFormatted("CALL", "Invalid extrinsic at " + fl);				
			}				
			break;
			case FANOUT_CONFLICTING:
			{
				String fl = ct.getLocation().toString(); 
				t.writeFormatted("CALL", "Fanout has invalid quit type at " + fl);				
			}				
			break;			
			case DO_UNVERIFIED:
			case DO_VERIFIED:
				if (! skipEmpty) {
					String fl = ct.getLocation().toString(); 
					t.writeFormatted("CALL", "DO at " + fl);									
				}
			break;
			case EXTRINSIC_UNVERIFIED:
			case EXTRINSIC_VERIFIED:
				if (! skipEmpty) {
					String fl = ct.getLocation().toString(); 
					t.writeFormatted("CALL", "Extrinsic at " + fl);									
				}
			break;
			default:
				break;
			}			
		}
	}
	
	@Override
	public boolean isEmpty() {
		return ! this.hasConflict();
	}
}
