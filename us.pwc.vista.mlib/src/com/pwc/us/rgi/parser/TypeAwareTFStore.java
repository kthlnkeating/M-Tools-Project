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

package com.pwc.us.rgi.parser;

import java.util.HashMap;
import java.util.Map;


public class TypeAwareTFStore<T extends Token> {
	private Map<String, TFCharacter<T>> characters;
	private Map<String, TFString<T>> strings;
	private Map<String, TFChoice<T>> choices;
	private Map<String, TFConstant<T>> constants;
	private Map<String, TFSequence<T>> sequences;
	private Map<String, TFList<T>> lists;
	private Map<String, TFDelimitedList<T>> delimitedList;
	private Map<String, TFForkedSequence<T>> forkedSequences;
	
	public void putCharacter(String name, TFCharacter<T> tf) {
		if (this.characters == null) {
			this.characters = new HashMap<String, TFCharacter<T>>();
		}
		this.characters.put(name, tf);
	}
	
	public void putString(String name, TFString<T> tf) {
		if (this.strings == null) {
			this.strings = new HashMap<String, TFString<T>>();
		}
		this.strings.put(name, tf);
	}
	
	public void putChoice(String name, TFChoice<T> tf) {
		if (this.choices == null) {
			this.choices = new HashMap<String, TFChoice<T>>();
		}
		this.choices.put(name, tf);
	}
	
	public void putConstant(String name, TFConstant<T> tf) {
		if (this.constants == null) {
			this.constants = new HashMap<String, TFConstant<T>>();
		}
		this.constants.put(name, tf);
	}
	
	public void putSequence(String name, TFSequence<T> tf) {
		if (this.sequences == null) {
			this.sequences = new HashMap<String, TFSequence<T>>();
		}
		this.sequences.put(name, tf);
	}
	
	public void putList(String name, TFList<T> tf) {
		if (this.lists == null) {
			this.lists = new HashMap<String, TFList<T>>();
		}
		this.lists.put(name, tf);
	}
	
	public void putDelimitedList(String name, TFDelimitedList<T> tf) {
		if (this.delimitedList == null) {
			this.delimitedList = new HashMap<String, TFDelimitedList<T>>();
		}
		this.delimitedList.put(name, tf);
	}
	
	public void putForkedSequence(String name, TFForkedSequence<T> tf) {
		if (this.forkedSequences == null) {
			this.forkedSequences = new HashMap<String, TFForkedSequence<T>>();
		}
		this.forkedSequences.put(name, tf);
	}
	
	public TFCharacter<T> getCharacter(String name) {
		if (this.characters == null) {
			return null;
		}
		return this.characters.get(name);
	}
	
	public TFString<T> getString(String name) {
		if (this.strings == null) {
			return null;
		}
		return this.strings.get(name);
	}
	
	public TFChoice<T> getChoice(String name) {
		if (this.choices == null) {
			return null;
		}
		return this.choices.get(name);
	}
	
	public TFConstant<T> getConstant(String name) {
		if (this.constants == null) {
			return null;
		}
		return this.constants.get(name);
	}
	
	public TFSequence<T> getSequence(String name) {
		if (this.sequences == null) {
			return null;
		}
		return this.sequences.get(name);
	}
	
	public TFList<T> getList(String name) {
		if (this.lists == null) {
			return null;
		}
		return this.lists.get(name);
	}
	
	public TFDelimitedList<T> geDelimitedList(String name) {
		if (this.delimitedList == null) {
			return null;
		}
		return this.delimitedList.get(name);
	}
	
	public TFForkedSequence<T> getForkedSequence(String name) {
		if (this.forkedSequences == null) {
			return null;
		}
		return this.forkedSequences.get(name);
	}
}
