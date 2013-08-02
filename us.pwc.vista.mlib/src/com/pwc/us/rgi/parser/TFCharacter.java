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

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pwc.us.rgi.charlib.Predicate;
import com.pwc.us.rgi.parsergen.ObjectSupply;

public class TFCharacter<T extends Token> extends TokenFactory<T> {
	private Predicate predicate;
	private Constructor<? extends T> constructor;
	
	public TFCharacter(String name, Predicate predicate) {
		super(name);
		this.predicate = predicate;
	}
	
	@Override
	public T tokenize(Text text, ObjectSupply<T> objectSupply) {
		TextPiece p = text.extractChar(this.predicate);
		return this.convertString(p, objectSupply);
	}

	public void setStringTargetType(Constructor<? extends T> constructor) {
		this.constructor = constructor;		
	}

	public T convertString(TextPiece p, ObjectSupply<T> objectSupply) {
		if (p == null) {
			return null;
		} else if (this.constructor == null) {
			return objectSupply.newString(p);
		} else {
			try {
				return this.constructor.newInstance(p);						
			} catch (Throwable t) {
				String clsName =  this.getClass().getName();
				Logger.getLogger(clsName).log(Level.SEVERE, "Unable to instantiate " + clsName + ".", t);			
			}
			return null;
		}
	}
}
