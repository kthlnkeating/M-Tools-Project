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

package com.pwc.us.rgi.m.tool.entry.basiccodeinfo;

import java.io.IOException;

import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.Terminal;

public class BasicCodeInfoTR implements ToolResult {
	public String[] formals;	
	private BasicCodeInfo info;

	public BasicCodeInfoTR(String[] formals, BasicCodeInfo info) {
		this.formals = formals;
		this.info = info;
	}
	
	public String[] getFormals() {
		return this.formals;
	}
	
	public BasicCodeInfo getData() {
		return this.info;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		t.writeFormatted("GLBS", this.info.getGlobals());
		t.writeFormatted("READ" , this.info.getReadCount());
		t.writeFormatted("WRITE", this.info.getWriteCount());
		t.writeFormatted("EXEC", this.info.getExecuteCount());
		t.writeFormatted("IND", this.info.getIndirectionCount());
		t.writeFormatted("FMGLBS", this.info.getFilemanGlobals());
		t.writeFormatted("FMCALLS", this.info.getFilemanCalls());		
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
}
