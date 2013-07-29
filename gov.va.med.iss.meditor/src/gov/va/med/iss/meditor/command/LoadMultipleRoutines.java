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

import java.util.List;

import gov.va.med.iss.meditor.command.utils.StatusHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

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
	
	protected void showFinalMessage(int overallSeverity, List<IStatus> statuses) {
		String message = this.getTopMessage(overallSeverity);
		MultiStatus multiStatus = StatusHelper.getMultiStatus(overallSeverity, message, statuses);
		MessageDialogHelper.showMulti(multiStatus);		
	}
	
	protected int updateStatuses(IStatus status, String prefixForFile, int overallSeverity, List<IStatus> statuses) {
		if (status.getSeverity() == IStatus.OK) {
			IStatus newStatus = StatusHelper.getStatus(IStatus.INFO, prefixForFile + "no issues");
			statuses.add(newStatus);
			return IStatus.INFO;
		} else {
			IStatus newStatus = StatusHelper.getStatus(status, prefixForFile + status.getMessage() + "\n");
			statuses.add(newStatus);
			int severity = status.getSeverity();
			return StatusHelper.updateOverallSeverity(overallSeverity, severity);				
		}	
	}
}
