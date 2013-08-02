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

import java.util.Arrays;
import java.util.List;

public class NamespaceFilter {
	private String[] includedNamespaces;
	private String[] excludedNamespaces;
	private String[] excludedExceptionNamespaces;

	public void setIncludedNamespaces(String[] namespaces) {
		this.includedNamespaces = namespaces;
	}

	public void setExcludedNamespaces(String[] namespaces) {
		this.excludedNamespaces = namespaces;
	}
	
	public void setExcludedExceptionNamespaces(String[] namespaces) {
		this.excludedExceptionNamespaces = namespaces;
	}

	public void addIncludedNamespaces(List<String> namespaces) {
		this.includedNamespaces = addNamespaces(this.includedNamespaces, namespaces);
	}
	
	public void addExcludedNamespaces(List<String> namespaces) {
		this.excludedNamespaces = addNamespaces(this.excludedNamespaces, namespaces);		
	}

	public void addExcludedExceptionNamespaces(List<String> namespaces) {
		this.excludedExceptionNamespaces = addNamespaces(this.excludedExceptionNamespaces, namespaces);		
	}

	private String[] addNamespaces(String[] original, List<String> additional) {
		if ((additional != null) && (additional.size() > 0)) {
			if (original == null) {
				return additional.toArray(new String[0]);				
			} else {
				int n = original.length;
				int m = additional.size();
				String[] result = Arrays.copyOf(original, n+m);
				for (String namespace : additional) {
					result[n] = namespace;
					++n;
				}
				return result;
			}
		}
		return original;
	}
	
	private static boolean checkNamespace(String routineName, String namespace) {
		int n = routineName.length();
		int m = namespace.length();
		if (n < m) return false;
		int count = Math.min(n, m);
		for (int i=0; i<count; ++i) {
			if (routineName.charAt(i) != namespace.charAt(i)) return false;
		}
		return true;
	}
	
	private static boolean checkNamespace(String routineName, String[] namespaces) {
		for (int i=0; i<namespaces.length; ++i) {
			if (checkNamespace(routineName, namespaces[i])) {
				return true;
			} 
		}
		return false;
	}
	
	public boolean contains(String name) {
		if (this.includedNamespaces != null) {
			boolean b = checkNamespace(name, this.includedNamespaces);
			if (!b) return false;
		}
		if (this.excludedNamespaces != null) {
			boolean b = checkNamespace(name, this.excludedNamespaces);
			if (b) {
				if (this.excludedExceptionNamespaces == null) return false;
				return checkNamespace(name, this.excludedExceptionNamespaces);
			}
		}
		return true;
	}
}
