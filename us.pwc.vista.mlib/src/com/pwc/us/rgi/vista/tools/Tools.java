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

package com.pwc.us.rgi.vista.tools;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public abstract class Tools {
	private String name; 
	
	protected static interface MemberFactory {
		Tool getInstance(CLIParams params);		
	}

	private Map<String, MemberFactory> tools = new HashMap<String, MemberFactory>();
	
	protected Tools(String name) {
		this.updateTools(this.tools);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	protected abstract void updateTools(Map<String, MemberFactory> tools);

	public Set<String> getRunTypeOptions() {
		return this.tools.keySet();
	}
		
	public Tool getRunType(String runTypeOption, CLIParams params) {
		MemberFactory specifiedFactory = this.tools.get(runTypeOption);
		if (specifiedFactory == null) {
			return null;
		}
		return specifiedFactory.getInstance(params);			
	}	
}
