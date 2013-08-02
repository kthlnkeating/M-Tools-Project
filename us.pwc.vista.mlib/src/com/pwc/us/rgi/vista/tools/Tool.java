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

import java.io.IOException;
import java.util.List;

import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.repository.VistaPackages;

public abstract class Tool {
	protected CLIParams params;
	
	protected Tool(CLIParams params) {
		this.params = params;
	}
		
	public abstract void run() throws IOException;

	protected VistaPackages getAllVistaPackages(RepositoryInfo ri) {
		List<VistaPackage> packages = ri.getAllPackages(this.params.packageExceptions);
		VistaPackages packageNodes = new VistaPackages(packages);
		return packageNodes;				
	}
	
	public VistaPackages getVistaPackages(RepositoryInfo ri)  {
		List<VistaPackage> packages = null; 
		if (this.params.packages.size() == 0) {
			packages = ri.getAllPackages(this.params.packageExceptions);
			if ((packages == null) || (packages.size() == 0)) {
				MRALogger.logError("Error loading package information from the repository.");
				return null;								
			}
		} else {
			packages = ri.getPackages(this.params.packages);
			if (packages.size() != this.params.packages.size()) {
				MRALogger.logError("Invalid package specification.");
				return null;				
			}
		}
		VistaPackages packageNodes = new VistaPackages(packages);
		return packageNodes;		
	}
}
