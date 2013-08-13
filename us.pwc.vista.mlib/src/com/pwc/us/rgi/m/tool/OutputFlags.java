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

public class OutputFlags {
	private Boolean showDetail;
	private Boolean skipEmpty;
	private String emptyMessage = "None found.";
	
	public void setShowDetail(boolean b) {
		this.showDetail = new Boolean(b);
	}
	
	public void setSkipEmpty(boolean b) {
		this.skipEmpty = new Boolean(b);
	}
	
	public void setEmptyMessage(String message) {
		this.emptyMessage = message;
	}
	
	public boolean getShowDetail(boolean defaultValue) {
		if (this.showDetail == null) {
			return defaultValue;
		} else {
			return this.showDetail.booleanValue();
		}		
	}

	public boolean getSkipEmpty(boolean defaultValue) {
		if (this.skipEmpty == null) {
			return defaultValue;
		} else {
			return this.skipEmpty.booleanValue();
		}		
	}
	
	public String getEmptyMessage() {
		return this.emptyMessage;
	}
}
