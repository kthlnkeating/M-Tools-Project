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

package gov.va.med.iss.meditor.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.runtime.IStatus;

abstract class LoadMultipleRoutines extends AbstractHandler {
	protected String getTopMessage(int overallSeverity) {
		if (overallSeverity == IStatus.ERROR) {
			return "Some file could not be loaded due to errors.";
		} else if (overallSeverity == IStatus.WARNING) {
			return "All files are loaded but some with warnings";			
		} else {
			return "All files are loaded successfully.";
		}
	}
}
