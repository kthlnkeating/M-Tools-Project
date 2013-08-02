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

package com.pwc.us.rgi.m.tool.routine;

import java.io.IOException;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResultPiece;
import com.pwc.us.rgi.output.Terminal;

public abstract class ToolResultPieceWithLineIndex implements ToolResultPiece {
	private int lineIndex;
	
	public ToolResultPieceWithLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}
	
	public int getLineIndex() {
		return this.lineIndex;
	}

	protected abstract String getActualResult();
	
	@Override
	public void write(Terminal t, EntryId entryUnderTest, OutputFlags flags) throws IOException {
		int lineIndex = this.getLineIndex();
		String routineName = entryUnderTest.getRoutineName();
		String location = " (" + routineName + ":" + String.valueOf(lineIndex) + ")";
		String actual = this.getActualResult();
		t.writeIndented(actual + location);
	}
}
