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

import com.pwc.us.rgi.m.tool.CommonToolParams;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;

public class BasicCodeInfoToolParams extends CommonToolParams {
	public RepositoryInfo repositoryInfo;
	
	public BasicCodeInfoToolParams(ParseTreeSupply pts, RepositoryInfo repositoryInfo) {
		super(pts);
		this.repositoryInfo = repositoryInfo;
	}
	
	public RepositoryInfo getRepositoryInfo() {
		return this.repositoryInfo;
	}
}
