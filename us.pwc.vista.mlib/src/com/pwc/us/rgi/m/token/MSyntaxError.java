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

package com.pwc.us.rgi.m.token;

import com.pwc.us.rgi.m.parsetree.ErrorNode;
import com.pwc.us.rgi.m.struct.MError;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.parser.TextPiece;

public class MSyntaxError implements MToken {	
	private int errorCode = MError.ERR_GENERAL_SYNTAX;
	private TextPiece errorText;
	private int errorIndex;
	
	public MSyntaxError(int errorCode, TextPiece errorText, int errorIndex) {
		this.errorCode = errorCode;
		this.errorText = errorText;
		this.errorIndex = errorIndex;
	}
	
	public int getErrorIndex() {
		return this.errorIndex;
	}
	
	@Override
	public TextPiece toValue() {
		return this.errorText;
	}

	@Override
	public void refactor(MRefactorSettings settings) {		
	}
	
	@Override
	public ErrorNode getNode() {
		return new ErrorNode(this.errorCode > 0 ? this.errorCode : MError.ERR_GENERAL_SYNTAX);
	}
}