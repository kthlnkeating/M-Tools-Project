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

package com.pwc.us.rgi.m.tool.entry.legacycodeinfo;

import java.io.IOException;
import java.util.Set;

import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariables;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfo;
import com.pwc.us.rgi.m.tool.entry.basiccodeinfo.BasicCodeInfoTR;
import com.pwc.us.rgi.output.Terminal;

public class LegacyCodeInfo implements ToolResult {
	public AssumedVariables assumedVariables;
	public BasicCodeInfoTR basicCodeInfo;

	public LegacyCodeInfo(AssumedVariables assumedVariables, BasicCodeInfoTR basicCodeInfo) {
		this.assumedVariables = assumedVariables;
		this.basicCodeInfo = basicCodeInfo;
	}
	
	public String[] getFormals() {
		return this.basicCodeInfo.getFormals();
	}
	
	public Set<String> getAssumedVariables() {
		return this.assumedVariables.toSet();
	}
	
	public BasicCodeInfo getBasicCodeInfo() {
		return this.basicCodeInfo.getData();
	}
	
	public AssumedVariables getAssumedVariablesTR() {
		return this.assumedVariables;
	}
	
	public BasicCodeInfoTR getBasicCodeInfoTR() {
		return this.basicCodeInfo;
	}
	

	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		String[] f = this.getFormals();
		AssumedVariables av = this.getAssumedVariablesTR();
		BasicCodeInfoTR ci = this.getBasicCodeInfoTR();
		t.writeFormatted("FORMAL", f);
		t.writeSortedFormatted("ASSUMED", av.toSet());
		ci.write(t, flags);
		t.writeEOL();
	}	
	
	@Override
	public boolean isEmpty() {
		return false;
	}
}
