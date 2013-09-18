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

package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.xtdebug.vo.VariableVO;

import java.util.List;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;

public interface IMDebugTarget extends IDebugTarget {
	MStackFrame[] getStackFrames();
	
	void stepOver();
	void stepInto();
	void stepReturn();
	
	IVariable[] getVariables();
	public List<VariableVO> getAllVariables();
	
	MDebugPreference getPreferenceImplemented();
	
	boolean canStepOver();
	boolean canStepReturn();
}
