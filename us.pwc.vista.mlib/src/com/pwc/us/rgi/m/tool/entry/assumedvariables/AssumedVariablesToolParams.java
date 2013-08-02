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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;

public class AssumedVariablesToolParams extends CommonToolParams {
	private List<String> expected;

	public AssumedVariablesToolParams(ParseTreeSupply pts) {
		super(pts);
	}

	public void addExpected(String variable) {
		if (this.expected == null) {
			this.expected = new ArrayList<String>();
		}
		this.expected.add(variable);
	}

	public void addExpected(List<String> variables) {
		if (this.expected == null) {
			this.expected = new ArrayList<String>();
		}
		this.expected.addAll(variables);
	}

	public List<String> getExpected() {
		if (this.expected == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(this.expected);
		}
	}
}
