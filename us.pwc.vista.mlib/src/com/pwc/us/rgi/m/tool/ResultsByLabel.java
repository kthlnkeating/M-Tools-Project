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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ResultsByLabel<T, U extends Collection<T>> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<String, U> resultsMap;
	
	public U put(String label, U results) {
		if (this.resultsMap == null) {
			this.resultsMap = new HashMap<String, U>();
		}
		return this.resultsMap.put(label, results);
	}
	
	public Set<String> getLabels() {
		if (this.resultsMap == null) {
			return Collections.emptySet();
		} else {
			return this.resultsMap.keySet();
		}
	}
	
	public U getResults(String label) {
		if (this.resultsMap == null) {
			return null;
		} else {
			return this.resultsMap.get(label);			
		}
	}
	
	public abstract U getNewResultsInstance();
	
	private U addEmptyResults(String label) {
		U results = this.getNewResultsInstance();
		this.resultsMap.put(label, results);
		return results;
	}
	
	public U getResultsAddingWhenNone(String label) {
		if (this.resultsMap == null) {
			this.resultsMap = new HashMap<String, U>();
			return this.addEmptyResults(label);
		} else {
			U results = this.resultsMap.get(label);
			if (results == null) {
				return this.addEmptyResults(label);
			} else {
				return results;
			}
		}
	}
	
	public List<String> getLabelsWithEmptyResults() {
		if (this.resultsMap == null) {
			return Collections.emptyList();
		} else {
			List<String> labelsWithEmptyResults = new ArrayList<String>();
			Set<String> labels = this.resultsMap.keySet();
			for (String label : labels) {
				U results = this.resultsMap.get(label);
				if (results.size() == 0) {
					labelsWithEmptyResults.add(label);
				}
			}
			return labelsWithEmptyResults;
		}
	}
	
	public boolean isEmpty() {
		if (this.resultsMap != null) {
			for (U results : this.resultsMap.values()) {
				if (results.size() > 0) return false;
			}
		}
		return true;
	}
	
	public List<T> getAllFlattened() {
		List<T> allFlattened = new ArrayList<T>();
		if (this.resultsMap != null) {
			Set<String> labels = this.resultsMap.keySet();
			for (String label : labels) {
				U results = this.resultsMap.get(label);
				allFlattened.addAll(results);
			}			
		}
		return allFlattened;
	}
}
