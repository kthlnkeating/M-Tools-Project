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

package com.pwc.us.rgi.m.parsetree;

import com.pwc.us.rgi.m.parsetree.data.EntryId;

public abstract class FanoutNode extends BasicNode {
	private static final long serialVersionUID = 1L;

	public IndirectFanoutLabel indirectFanoutLabel;
	public FanoutLabel fanoutLabel;
	
	public IndirectFanoutRoutine indirectFanoutRoutine;
	public EnvironmentFanoutRoutine environmentFanoutRoutine;
	public FanoutRoutine fanoutRoutine;
	
	public void setIndirectFanoutLabel(IndirectFanoutLabel indirectFanoutLabel) {
		this.indirectFanoutLabel = indirectFanoutLabel;
	}
	public void setFanoutLabel(FanoutLabel fanoutLabel) {
		this.fanoutLabel = fanoutLabel;
	}
	
	public void setIndirectFanoutRoutine(IndirectFanoutRoutine indirectFanoutRoutine) {
		this.indirectFanoutRoutine = indirectFanoutRoutine;
	}
	
	public void setEnvironmentFanoutRoutine(EnvironmentFanoutRoutine environmentFanoutRoutine) {
		this.environmentFanoutRoutine = environmentFanoutRoutine;		
	}
	
	public void setFanoutRoutine(FanoutRoutine fanoutRoutine) {
		this.fanoutRoutine = fanoutRoutine;
	}

	protected void acceptLabelNodes(Visitor visitor) {
		if (this.indirectFanoutLabel != null) {
			this.indirectFanoutLabel.accept(visitor);
		}
		if (this.fanoutLabel != null) {
			this.fanoutLabel.accept(visitor);
		}
	}
	
	protected void acceptRoutineNodes(Visitor visitor) {
		if (this.indirectFanoutRoutine != null) {
			this.indirectFanoutRoutine.accept(visitor);
		}
		if (this.environmentFanoutRoutine != null) {
			this.environmentFanoutRoutine.accept(visitor);
		}
		if (this.fanoutRoutine != null) {
			this.fanoutRoutine.accept(visitor);
		}
	}
	
	public EntryId getFanoutId() {
		if ((this.fanoutLabel == null) && (this.fanoutRoutine == null)) {
			return null;
		}
		if ((this.indirectFanoutLabel != null) || (this.indirectFanoutRoutine != null) || this.environmentFanoutRoutine != null) {
			return null;
		}
		String label = (this.fanoutLabel == null) ? null : this.fanoutLabel.getValue();
		String routine = (this.fanoutRoutine == null) ? null : this.fanoutRoutine.getName();
	
		return new EntryId(routine, label);
	}	
}
