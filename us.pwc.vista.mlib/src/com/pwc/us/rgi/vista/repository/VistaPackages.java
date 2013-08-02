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

package com.pwc.us.rgi.vista.repository;

import java.util.List;


public class VistaPackages implements RepositoryNode {
	private List<VistaPackage> packages;
	
	public VistaPackages(List<VistaPackage> packages) {
		this.packages = packages;
	}
	
	public void acceptSubNodes(RepositoryVisitor visitor) {
		if (this.packages != null) {
			for (VistaPackage p : this.packages) {
				p.accept(visitor);
			}
		}
	}
	
	public List<VistaPackage> getPackages() {
		return this.packages;
	}
	
	public int getPackagesSize() {
		return this.packages.size();
	}
	
	@Override
	public void accept(RepositoryVisitor visitor) {
		visitor.visitRoutinePackages(this);
	}
}
