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

package com.pwc.us.rgi.struct;

public enum StringCase {
	UPPER_CASE {
		@Override
		public String change(String input) {
			return input.toUpperCase();
		}
	},
	LOWER_CASE {
		@Override
		public String change(String input) {
			return input.toLowerCase();
		}
	},
	TITLE_CASE {
		@Override
		public String change(String input) {
			if (input.length() > 0) {
				char ch = Character.toTitleCase(input.charAt(0));				
				if (input .length() == 1) {
					return String.valueOf(ch);
				} else {
					return ch + input.toLowerCase().substring(1);
				}
			}
			return "";
		}
	},
	SAME_CASE {
		@Override
		public String change(String input) {
			return input;
		}
	};
	
	public abstract String change(String input);
}
