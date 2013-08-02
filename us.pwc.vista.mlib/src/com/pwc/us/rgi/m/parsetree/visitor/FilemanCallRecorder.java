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

package com.pwc.us.rgi.m.parsetree.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pwc.us.rgi.m.parsetree.AtomicDo;
import com.pwc.us.rgi.m.parsetree.Extrinsic;
import com.pwc.us.rgi.m.parsetree.InnerEntryList;
import com.pwc.us.rgi.m.parsetree.Local;
import com.pwc.us.rgi.m.parsetree.Node;
import com.pwc.us.rgi.m.parsetree.Routine;
import com.pwc.us.rgi.m.parsetree.data.CallArgument;
import com.pwc.us.rgi.m.parsetree.data.CallArgumentType;
import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.VistaPackage;

public class FilemanCallRecorder extends LocationMarker {
	private RepositoryInfo repositoryInfo;
	private String currentRoutineName;
	private Set<String> filemanGlobals = new HashSet<String>();
	private Set<String> filemanCalls = new HashSet<String>();
	private InnerEntryList lastInnerEntryList;
	
	public FilemanCallRecorder(RepositoryInfo ri) {
		this.repositoryInfo = ri;
	}
		
	private static String removeDoubleQuote(String input) {
		if (input.charAt(0) != '"') {
			return input;
		}
		return input.substring(1, input.length()-1);
	}
	
	private static boolean validate(String input) {
		int dotCount = 0;
		for (int i=0; i<input.length(); ++i) {
			char ch = input.charAt(i);
			if (ch == '.') {
				++dotCount;
				if (dotCount > 1) return false;
			} else if (! Character.isDigit(ch)) {
				return false;
			}
		}
		if ((dotCount == 1) && (input.length() ==1)) return false;
		return true;
	}
	
	private boolean inFilemanRoutine(String routineName, boolean kernalToo) {
		VistaPackage pkg = this.repositoryInfo == null ? null : this.repositoryInfo.getPackageFromRoutineName(routineName);
		if (pkg == null) {			
			char ch0 = routineName.charAt(0);
			if (ch0 == 'D') {
				char ch1 = routineName.charAt(1);
				if ((ch1 == 'I') || (ch1 == 'M') || (ch1 == 'D')) {
					return true;
				}
			}
			return false;
		} else {
			String name = pkg.getPackageName();
			return name.equalsIgnoreCase("VA FILEMAN") || (kernalToo && name.equalsIgnoreCase("KERNEL"));
		}
	}
	
	@Override
	protected void setLocal(Local local, Node rhs) {
		if ((rhs != null) && ! inFilemanRoutine(this.currentRoutineName, true)) {
			String rhsAsConst = rhs.getAsConstExpr();
			if (rhsAsConst != null) {
				String name = local.getName().toString();
				if (name.startsWith("DI") && (name.length() == 3)) {
					char ch = name.charAt(2);
					if ((ch == 'E') || (ch == 'K') || (ch == 'C')) {
						rhsAsConst = removeDoubleQuote(rhsAsConst);
						if ((rhsAsConst.length() > 0) && (rhsAsConst.charAt(0) == '^')) {
							String[] namePieces = rhsAsConst.split("\\(");
							if ((namePieces[0].length() > 0)) {
								String result = namePieces[0] + "(";
								if ((namePieces.length > 1) && (namePieces[1] != null) && (namePieces[1].length() > 0)) {
									String[] subscripts = namePieces[1].split("\\,");
									if ((subscripts.length > 0) && (subscripts[0].length() > 0) && validate(subscripts[0])) {
										result += subscripts[0];									
									}
								}
								this.filemanGlobals.add(result);
							}
						}
					}
				}
			}
		}
	}
	
	protected void updateFanout(EntryId fanoutId, CallArgument[] callArguments) {
		if (fanoutId != null) {
			if ((callArguments != null) && (callArguments.length > 0) && ! inFilemanRoutine(this.currentRoutineName, true)) {
				CallArgument ca = callArguments[0];
				if (ca != null) {
					CallArgumentType caType = ca.getType();
					if ((caType == CallArgumentType.STRING_LITERAL) || (caType == CallArgumentType.NUMBER_LITERAL)) {
						String routineName = fanoutId.getRoutineName();						
						if ((routineName != null) && (routineName.length() > 1) && inFilemanRoutine(routineName, false)) {
							Node caNode = ca.getNode();
							String cleanValue = removeDoubleQuote(caNode.getAsConstExpr());
							if (cleanValue.length() > 0 && validate(cleanValue)) {
								String value = fanoutId.toString() + "(" + cleanValue;
								this.filemanCalls.add(value);
							}
						}
					}
				}
			}
		} 
	}
	
	@Override
	protected void visitAtomicDo(AtomicDo atomicDo) {
		super.visitAtomicDo(atomicDo);		
		this.updateFanout(atomicDo.getFanoutId(), atomicDo.getCallArguments());
	}
	
	@Override
	protected void visitExtrinsic(Extrinsic extrinsic) {
		super.visitExtrinsic(extrinsic);
		this.updateFanout(extrinsic.getFanoutId(), extrinsic.getCallArguments());
	}
	
	@Override
	protected void visitInnerEntryList(InnerEntryList entryList) {
		if (entryList != this.lastInnerEntryList) {
			this.lastInnerEntryList = entryList;
			super.visitInnerEntryList(entryList);
		}
	}
		
	public List<String> getFilemanGlobals() {
		List<String> result = new ArrayList<String>(this.filemanGlobals);
		Collections.sort(result);
		return result;
	}
	
	public List<String> getFilemanCalls() {
		List<String> result = new ArrayList<String>(this.filemanCalls);
		Collections.sort(result);
		return result;
	}
	
	@Override
	protected void visitRoutine(Routine routine) {
		this.filemanGlobals = new HashSet<String>();
		this.filemanCalls = new HashSet<String>();
		this.currentRoutineName = routine.getName();
		super.visitRoutine(routine);
	}
}