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

package com.pwc.us.rgi.m.parsetree;

public abstract class EntryList extends NodeList<Label> {
	private static final long serialVersionUID = 1L;

	protected abstract Label getLabel(String tag, String routineName, String[] parameters);

	public Label addEntry(String tag, String routineName, String[] parameters) {
		Label entry = this.getLabel(tag, routineName, parameters);
		Label last = this.getLastNode();		
		if ((last != null) && ! (last.isClosed())) {
			last.setContinuationEntry(entry);
		}
		this.add(entry);
		return entry;
	}
}
