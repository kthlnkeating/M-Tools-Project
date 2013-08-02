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

public enum MToolErrorType {
	ROUTINE_NOT_FOUND(1, "Routine {0} is not found."),
	LABEL_NOT_FOUND(1, "Label {0} is not found.");
	
	private int numArguments;
	private String message;
	
	private MToolErrorType(int numArguments, String message) {
		this.numArguments = numArguments;
		this.message = message;
	}
	
	public int getNumArgument() {
		return this.numArguments;
	}
	
	public String getMessage() {
		return this.message;
	}
}
