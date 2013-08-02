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

package com.pwc.us.rgi.m.tool.routine.occurance;

public enum OccuranceType {
	WRITE,
	READ,
	INDIRECTION,
	INTRINSIC_TEXT,
	NAKED_GLOBAL,
	EXECUTE,
	EXCLUSIVE_KILL,
	EXTERNAL_DO,
	ATOMIC_DO,
	ATOMIC_GOTO,
	DO,
	GOTO,
	EXTRINSIC,
	DO_BLOCK;
}


