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

package com.pwc.us.rgi.stringlib;

import java.util.Comparator;

public class MComparator implements Comparator<String> {
	private static Double toNumber(String value) {
		try {
			Double result = Double.valueOf(value);
			return result;
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	@Override
	public int compare(String o1, String o2) {
		Double d1 = MComparator.toNumber(o1);
		Double d2 = MComparator.toNumber(o2);
		if (d2 == null) {
			if (d1 == null) {
				return o1.compareTo(o2);
			} else {
				return 1;
			}			
		} else {
			if (d1 == null) {
				return -1;
			} else {
				return d1.compareTo(d2);
			}
		}
	}
}
