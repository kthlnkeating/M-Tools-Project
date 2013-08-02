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

package com.pwc.us.rgi.m.tool;

import java.io.InputStream;
import java.util.Collection;

import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.struct.MRoutineContent;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.token.TFRoutine;
import com.pwc.us.rgi.parsergen.ParseException;
import com.pwc.us.rgi.vista.tools.MRALogger;

public class SourceCodeToParseTreeAdapter implements ParseTreeSupply {
	private SourceCodeSupply sourceCodeSupply;
	private TFRoutine tokenFactory;
	private boolean inError = false;
	private MVersion version;
	
	public SourceCodeToParseTreeAdapter(SourceCodeSupply sourceCodeSupply) {
		this(sourceCodeSupply, MVersion.CACHE);
	}
	
	public SourceCodeToParseTreeAdapter(SourceCodeSupply sourceCodeSupply, MVersion version) {
		this.sourceCodeSupply = sourceCodeSupply;
		this.version = version;
	}
	
	private TFRoutine getTokenFactory() {
		try {
			MTFSupply mtf = MTFSupply.getInstance(this.version);
			TFRoutine tf = new TFRoutine(mtf);
			return tf;
		} catch (ParseException e) {
			this.inError = true;
			MRALogger.logError("Unable to load M parser definitions.");
			return null;
		}
		
	}
	
	@Override
	public Routine getParseTree(String routineName) {
		if (this.inError) return null;
		if (this.tokenFactory == null) {
			this.tokenFactory = this.getTokenFactory();
		}
		InputStream is = this.sourceCodeSupply.getStream(routineName);
		if (is != null) {
			MRoutineContent mrc = MRoutineContent.getInstance(routineName, is);
			MRoutine tokenizedSourceCode = this.tokenFactory.tokenize(mrc);
			Node node = tokenizedSourceCode.getNode();
			if (node instanceof Routine) {
				return (Routine) node;
			} else {
				this.inError = true;
				return null;
			}
		}
		return null;
	}
	
	@Override
	public Collection<String> getAllRoutineNames() {
		return this.sourceCodeSupply.getAllRoutineNames();
	}
}
