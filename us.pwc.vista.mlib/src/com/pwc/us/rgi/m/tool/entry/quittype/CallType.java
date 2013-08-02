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

import com.pwc.us.rgi.m.struct.CodeLocation;

public class CallType {
	private CallTypeState state;
	private CodeLocation location;
	private CodeLocation conflictingLocation;
	
	public CallType(CallTypeState state, CodeLocation location) {
		this.state = state;
		this.location = location;
	}

	public CallTypeState getState() {
		return this.state;
	}
	
	public CodeLocation getLocation() {
		return this.location;
	}
	
	public CodeLocation getConflictingLocation() {
		return this.conflictingLocation;
	}
	
	private int updateFromFanoutQuitsWithValue(CodeLocation location) {
		switch (this.state) {
		case EXTRINSIC_UNVERIFIED:
			this.state = CallTypeState.EXTRINSIC_VERIFIED;
			return 1;
		case DO_UNVERIFIED:
			this.state = CallTypeState.DO_CONFLICTING;
			this.conflictingLocation = location;
			return 1;
		case DO_VERIFIED:
			this.state = CallTypeState.INTERNAL_ERROR;
			return 1;
		default:
			return 0;
		}
	}
	
	private int updateFromFanoutQuitsWithoutValue(CodeLocation location) {
		switch (this.state) {
		case DO_UNVERIFIED:
			this.state = CallTypeState.DO_VERIFIED;
			return 1;
		case EXTRINSIC_UNVERIFIED:
			this.state = CallTypeState.EXTRINSIC_CONFLICTING;
			this.conflictingLocation = location;
			return 1;
		case EXTRINSIC_VERIFIED:
			this.state = CallTypeState.INTERNAL_ERROR;
			return 1;
		default:
			return 0;
		}
	}

	public int updateFromFanout(QuitType fanoutQuitType) {
		QuitTypeState qts = fanoutQuitType.getQuitTypeState();
		CodeLocation location = fanoutQuitType.getFirstQuitLocation();
		switch (qts) {
		case QUITS_WITH_VALUE:
			return this.updateFromFanoutQuitsWithValue(location);
		case QUITS_WITHOUT_VALUE:
			return this.updateFromFanoutQuitsWithoutValue(location);			
		case CONFLICTING_QUITS:
			if (this.state != CallTypeState.FANOUT_CONFLICTING) {
				this.state = CallTypeState.FANOUT_CONFLICTING;
				return 1;
			} else {
				return 0;
			}
		default:
			return 0;
		}
	}
}
