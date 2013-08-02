//---------------------------------------------------------------------------
//Copyright 2013 PwC
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//---------------------------------------------------------------------------

package com.pwc.us.rgi.m.token;

import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.struct.MNameWithMnemonic;
import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.parser.TextPiece;
import com.pwc.us.rgi.parser.Tokens;

public abstract class MCommandBase implements MToken {
	private TextPiece name;
	private MSequence whatFollows;
	
	public MCommandBase(TextPiece name) {
		this.name = name;
	}

	protected abstract MNameWithMnemonic getNameWithMnemonic();

	protected void setName(TextPiece name) {
		this.name = name;
	}

	protected void setWhatFollows(MSequence whatFollows) {
		this.whatFollows = whatFollows;
	}
		
	protected Node getArgumentNode() {
		if (this.whatFollows == null) return null;
		return this.whatFollows.getNode(2);
	}

	protected Node getPostConditionNode() {
		if (this.whatFollows == null) return null;
		return this.whatFollows.getNode(0, 1);
	}

	protected MToken getArgument() {
		if (this.whatFollows == null) return null;
		return this.whatFollows.getToken(2);
	}

	protected Tokens<MToken> getArgumentTokens(int index) {
		if (this.whatFollows == null) return null;
		return this.whatFollows.getTokens(index);
	}

	protected Node getArgumentNode(int index) {
		if (this.whatFollows == null) return null;
		return this.whatFollows.getNode(2, index);

	}
	
	@Override 
	public TextPiece toValue() {
		TextPiece result = new TextPiece(this.name);
		if (this.whatFollows != null) {
			result.add(this.whatFollows.toValue());
		}
		return result;
	}
	
	@Override
	public void refactor(MRefactorSettings settings) {
		MNameWithMnemonic mnwm = this.getNameWithMnemonic();
		String newExpression = mnwm.refactor(settings.commandNameSettings, this.name.toString());
		TextPiece newName = new TextPiece(newExpression);
		this.name.set(newName);
		if (this.whatFollows != null) {
			this.whatFollows.refactor(settings);
		}
	}
}
