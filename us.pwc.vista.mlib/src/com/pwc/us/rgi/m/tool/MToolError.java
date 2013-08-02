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

public class MToolError {
	private MToolErrorType type;
	private String[] params;
	
	public MToolError(MToolErrorType type, String[] params) {
		this.type = type;
		this.params = params;
	}
	
	public String getMessage() {
		String message = type.getMessage();
		int n = this.params == null ? 0 : this.params.length;
		for (int i=0; i<type.getNumArgument(); ++i) {
			String param = (i < n) ? this.params[i] : "XXXXXX";
			if (param == null) {
				param = "XXXXXX";
			}
			message = message.replaceAll("\\{" + String.valueOf(i) + "\\}", param);
		}
		return message;
	}
}
