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

package com.pwc.us.rgi.m.parsetree.data;

import java.io.IOException;
import java.io.Serializable;

import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.ToolResultPiece;
import com.pwc.us.rgi.output.Terminal;

public class EntryId implements Comparable<EntryId>, Serializable, ToolResult, ToolResultPiece {
	private static final long serialVersionUID = 1L;

	public enum StringFormat {
		SF_SINGLE_LABEL,
		SF_SINGLE_ROUTINE;
	}
	
	private String routineName;
	private String label;
	
	public EntryId(String routineName, String label) {
		this.routineName = routineName;
		this.label = label;
	}
	
	public String getRoutineName() {
		return routineName;
	}
	
	public String getTag() {
		return this.label;
	}
	
	public boolean hasDedicatedLabel() {
		return (this.label != null) && ! this.label.equals(this.routineName);
	}
	
	public String getLabelOrDefault() {
		if ((this.label == null) || (this.label.isEmpty())) {
			return this.routineName;
		} else {
			return this.label;
		}
	}
	
	@Override
	public boolean equals(Object rhs) {
		if ((rhs != null) && (rhs instanceof EntryId)) {	
			String lhsString = this.toString();
			String rhsString = rhs.toString();
			return lhsString.equals(rhsString);
		}
		return false;
	}
	
	public boolean equals(EntryId rhs, String routineName) {
		if ((rhs.routineName == null) && (this.routineName != null)) {
			return this.routineName.equals(routineName) && this.getLabelOrDefault().equals(rhs.label);
		} else {
			return this.equals(rhs);
		}
	}
	
	@Override
	public int hashCode() {
		int result = this.toString().hashCode(); 
		return result;
	}
	
	@Override
	public String toString() {
		String lbl = this.getTag();
		String rou = this.getRoutineName();
		if (rou != null) {
			rou = "^" + rou;
		} else {
			rou = "";
		}
		if (lbl == null) {
			lbl = "";
		}					
		return lbl + rou;		
	}

	public String toString2() {
		String lbl = this.getTag();
		String rou = this.getRoutineName();
		if (lbl == null) {
			lbl = "";
		} else if (rou != null) {
			if (lbl.equals(rou)) {
				lbl = "";
			}			
		}
		if (rou != null) {
			rou = "^" + rou;
		} else {
			rou = "";
		}
		return lbl + rou;		
	}

	private int compareLabels(String lhs, String rhs) {
		if ((lhs == null) || (lhs.isEmpty())) {
			if ((rhs == null) || (rhs.isEmpty())) return 0;
			return -1;
		}
		if ((rhs == null) || (rhs.isEmpty())) return 1;
		return lhs.compareTo(rhs);
	}
	
	@Override
	public int compareTo(EntryId rhs) {
		if (rhs.routineName == null) {
			if (this.routineName != null) return -1;
			return compareLabels(this.label, rhs.label);
		}
		if (this.routineName == null) return 1;
		int result = this.routineName.compareTo(rhs.routineName);
		if (result == 0) {
			return compareLabels(this.label, rhs.label);
		} else {
			return result;
		}
	}
	
	public void localize(String routineName) {
		if (routineName.equals(this.routineName)) {
			this.routineName = null;
			if (this.label == null) {
				this.label = routineName;
			}
		}
	}
	
	public EntryId getFullCopy(String routineName) {
		String r = (this.routineName == null) ? routineName : this.routineName;
		String l = ((this.label == null) || this.label.isEmpty()) ? this.routineName : this.label; 
		return new EntryId(r, l);
	}
	
	public static EntryId getInstance(String tag, StringFormat format) {
		if (tag != null) {
			String[] pieces = tag.split("\\^");
			if ((pieces != null) && (pieces.length > 0) && (pieces.length < 3)) {
				if (pieces.length > 1) {
					String label = pieces[0];
					String routine = pieces[1];
					return new EntryId(routine, label);
				} else {
					switch (format) {
					case SF_SINGLE_ROUTINE:
						return new EntryId(pieces[0], null);
					default:
						return new EntryId(null, pieces[0]);
					}
				}
			}
		}
		return null;
	}

	public static EntryId getInstance(String tag) {
		return getInstance(tag, StringFormat.SF_SINGLE_LABEL);
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public void write(Terminal t, OutputFlags flags) throws IOException {
		t.writeIndented(this.toString2());
	}

	@Override
	public void write(Terminal t, EntryId EntryUnderTest, OutputFlags flags) throws IOException {
		t.writeIndented(this.toString2());
	}
}
