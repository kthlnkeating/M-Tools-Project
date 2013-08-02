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

public enum CallTypeState {
	DO_UNVERIFIED(false),
	EXTRINSIC_UNVERIFIED(false),
	DO_VERIFIED(false),
	EXTRINSIC_VERIFIED(false),
	DO_CONFLICTING(true),
	EXTRINSIC_CONFLICTING(true),
	FANOUT_CONFLICTING(true),
	INTERNAL_ERROR(true);
	
	private boolean conflicted;

	private CallTypeState(boolean conflicted) {
		this.conflicted = conflicted;
	}
	
	public boolean isConflictingState() {
		return this.conflicted;
	}
}
