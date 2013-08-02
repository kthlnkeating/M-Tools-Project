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

public interface Tokens<T extends Token> {
	void addToken(T token);
	void setToken(int index, T token);
	
	T getToken(int index);
	T getToken(int index0, int index1);	
	T getToken(int index0, int index1, int index2);	
	
	Tokens<T> getTokens(int index);
	Tokens<T> getTokens(int index0, int index1);

	boolean hasToken();
		
	TextPiece toValue();
	
	Iterable<T> toLogicalIterable();
	Iterable<T> toIterable();
	
	int size();
}
