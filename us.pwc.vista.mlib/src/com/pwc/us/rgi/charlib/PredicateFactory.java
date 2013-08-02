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

package com.pwc.us.rgi.charlib;

import java.util.ArrayList;
import java.util.List;

public class PredicateFactory {
	private static class CharRange {
		public char lowerBound;
		public char higherBound;
		
		public CharRange(char lowerBound, char higherBound) {
			if (lowerBound < higherBound) {
				this.lowerBound = lowerBound;
				this.higherBound = higherBound;
			} else {
				this.lowerBound = higherBound;
				this.higherBound = lowerBound;
			}
		}
		
		public Predicate toPredicate() {
			return new CharRangePredicate(this.lowerBound, this.higherBound);	
		}
	}
	
	private List<Character> includeChars;
	private List<CharRange> includeRanges;
	
	private List<Character> excludeChars;
	private List<CharRange> excludeRanges;
	
	public void addChar(char ch) {
		if (this.includeChars == null) {
			this.includeChars = new ArrayList<Character>();
		}
		this.includeChars.add(ch);
	}
	
	public void addChars(char[] chs) {
		if (chs != null) for (char ch : chs) {
			this.addChar(ch);
		}
	}
	
	public void removeChar(char ch) {
		if (this.excludeChars == null) {
			this.excludeChars = new ArrayList<Character>();
		}
		this.excludeChars.add(ch);
	}

	public void removeChars(char[] chs) {
		if (chs != null) for (char ch : chs) {
			this.removeChar(ch);
		}
	}
	
	public void addRange(char ch0, char ch1) {
		if (this.includeRanges == null) {
			this.includeRanges = new ArrayList<CharRange>();
		}
		this.includeRanges.add(new CharRange(ch0, ch1));
	}
	
	public void addRanges(char[] chs) {
		if (chs != null) for (int i=0; i<chs.length; i+=2) {
			char ch0 = chs[i];
			char ch1 = chs[i+1];
			this.addRange(ch0, ch1);
		}
	}
	
	public void removeRange(char ch0, char ch1) {
		if (this.excludeRanges == null) {
			this.excludeRanges = new ArrayList<CharRange>();
		}
		this.excludeRanges.add(new CharRange(ch0, ch1));
	}
	
	public void removeRanges(char[] chs) {
		if (chs != null) for (int i=0; i<chs.length; i+=2) {
			char ch0 = chs[i];
			char ch1 = chs[i+1];
			this.removeRange(ch0, ch1);
		}
	}
	
	private Predicate getCharPredicate(List<Character> chars) {
		if (chars == null) {
			return null;
		} else if (chars.size() == 1) {
			return new CharPredicate(chars.get(0));
		} else {
			char[] chs = new char[chars.size()];
			int index = 0;
			for (Character ch : chars) {
				chs[index] = ch.charValue();
				++index;
			}
			return new CharsPredicate(chs);
		} 		
	}
	
	private Predicate getRangePredicate(List<CharRange> ranges) {
		if (ranges == null) {
			return null;
		} else if (ranges.size() == 1) {
			CharRange range = ranges.get(0);
			return range.toPredicate();
		} else {
			Predicate[] ps = new Predicate[ranges.size()];
			int index = 0;
			for (CharRange r : ranges) {
				ps[index] = r.toPredicate();
				++index;
			}
			return new OrPredicates(ps);
		} 		
	}
	
	private static Predicate getOrPredicate(Predicate p0, Predicate p1) {
		if (p1 == null) return p0;
		if (p0 == null) return p1;
		return new OrPredicate(p0, p1);
	}

	private static Predicate getAndPredicate(Predicate p0, Predicate p1) {
		if (p1 == null) return p0;
		if (p0 == null) return p1;
		return new AndPredicate(p0, p1);
	}
	
	public Predicate generate() {
			Predicate p0 = getCharPredicate(this.includeChars);
			Predicate p1 = getRangePredicate(this.includeRanges);
			Predicate p2 = getCharPredicate(this.excludeChars);
			if (p2 != null) {
				p2 = new ExcludePredicate(p2);
			}			
			Predicate p3 = getRangePredicate(this.excludeRanges);
			if (p3 != null) {
				p3 = new ExcludePredicate(p3);
			}			
			Predicate result = getAndPredicate(getOrPredicate(p0, p1), getAndPredicate(p2, p3));
			if (result == null) {
				result = new FalsePredicate();
			}
			return result;
	}
}
