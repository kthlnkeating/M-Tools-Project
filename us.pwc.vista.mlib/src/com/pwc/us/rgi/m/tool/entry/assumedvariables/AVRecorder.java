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

package com.pwc.us.rgi.m.tool.entry.assumedvariables;

import java.util.HashSet;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.Local;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.OpenCloseUseCmdNodes;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.parsetree.visitor.BlockRecorder;
import com.pwc.us.rgi.m.struct.CodeLocation;

class AVRecorder extends BlockRecorder<IndexedFanout, AVBlockData> {
	private int index;
	private boolean underDeviceParameter;
	
	public void reset() {
		this.underDeviceParameter = false;
		this.index = 0;
		super.reset();
	}
	
	@Override
	protected void updateFanout(EntryId fanoutId, FanoutType type) {
		if (fanoutId != null) {
			super.updateFanout(fanoutId, type);
			++this.index;
		} 
	}

	private void addLocal(Local local) {
		AVBlockData d = this.getCurrentBlockData();
		if (d != null) {
			CodeLocation codeLocation = this.getCodeLocation();
			d.addLocal(local, codeLocation);	
		}
	}
	
	private void addOutput(Local local) {
		this.addLocal(local);
	}
	
	@Override
	protected void visitDeviceParameters(OpenCloseUseCmdNodes.DeviceParameters deviceParameters) {
		boolean current = this.underDeviceParameter;
		this.underDeviceParameter = true;
		super.visitDeviceParameters(deviceParameters);
		this.underDeviceParameter = current;
	}
		
	@Override
	protected void setLocal(Local local, Node rhs) {
		AVBlockData d = this.getCurrentBlockData();
		if (d != null) {
			this.addOutput(local);
		}
	}
	
	@Override
	protected void mergeLocal(Local local, Node rhs) {
		this.addOutput(local);
	}
	
	@Override
	protected void killLocal(Local local) {		
		this.addOutput(local);
	}
	
	@Override
	protected void newLocal(Local local) {
		AVBlockData d = this.getCurrentBlockData();
		if (d != null) d.addNewed(this.index, local);
	}
	
	private static Set<String> DEVICE_PARAMS = new HashSet<String>();
	static {
		DEVICE_PARAMS.add("LINE");
		DEVICE_PARAMS.add("NOLINE");
		DEVICE_PARAMS.add("VT");
		DEVICE_PARAMS.add("NOESCAPE");
		DEVICE_PARAMS.add("ESCAPE");
	}
	
	
	private boolean isDeviceParameter(Local local) {
		String name = local.getName().toString();
		if (DEVICE_PARAMS.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void visitLocal(Local local) {
		if ((! this.underDeviceParameter) || (! isDeviceParameter(local))) { 
			super.visitLocal(local);
			this.addLocal(local);
		}
	}

	@Override
	protected void passLocalByVal(Local local, int index) {		
		this.addLocal(local);
	}
	
	@Override
	protected void passLocalByRef(Local local, int index) {
		this.addLocal(local);
		super.passLocalByRef(local, index);
	}

	@Override
	protected AVBlockData getNewBlockData(EntryId entryId, String[] params) {
		AVBlockData result = new AVBlockData(entryId);
		result.setFormals(params);
		return result;
	}
	
	@Override
	protected IndexedFanout getFanout(EntryId id, FanoutType type) {
		return new IndexedFanout(this.index, id, type);
	}
}
