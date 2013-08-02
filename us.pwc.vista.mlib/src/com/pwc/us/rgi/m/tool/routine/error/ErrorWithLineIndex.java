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

package com.pwc.us.rgi.m.tool.routine.error;

import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.tool.routine.ToolResultPieceWithLineIndex;

public class ErrorWithLineIndex extends ToolResultPieceWithLineIndex {
	private MError error;

	public ErrorWithLineIndex(MError object, int lineIndex) {
		super(lineIndex);
		this.error = object;
	}
	
	public MError getError() {
		return this.error;
	}
	
	@Override
	protected String getActualResult() {
		return this.getError().getText();
	}
}
