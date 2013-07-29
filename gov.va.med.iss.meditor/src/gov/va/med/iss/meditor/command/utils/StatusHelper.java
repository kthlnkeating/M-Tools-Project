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

package gov.va.med.iss.meditor.command.utils;

import java.util.List;

import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

public class StatusHelper {
	public static int CODE_PROJECT_FILE_UPDATED = 1000;
	public static int CODE_PROJECT_FILE_CREATED = 1001;
	public static int CODE_PROJECT_FILE_IDENTICAL = 1002;

	public static IStatus getStatus(int code, int severity, String msgKey, String... msgBindings) {
		String message = Messages.bind(msgKey, msgBindings);
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus status = new Status(severity, pid, code, message, null);
		return status;
	}

	public static IStatus getStatus(int severity, String msgKey, String... msgBindings) {
		String message = Messages.bind(msgKey, msgBindings);
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus status = new Status(severity, pid, message);
		return status;
	}

	public static IStatus getStatus(Throwable t) {
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus status = new Status(IStatus.ERROR, pid, t.getMessage(), t);
		return status;
	}

	public static IStatus getStatus(String message, Throwable t) {
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus status = new Status(IStatus.ERROR, pid, message, t);
		return status;
	}

	public static IStatus getOKStatus() {
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus status = new Status(IStatus.OK, pid, "");
		return status;
	}
	
	public static IStatus getStatus(IStatus status, String newMessage) {
		return new Status (status.getSeverity(), status.getPlugin(), status.getCode(), newMessage, status.getException());
	}
	
	public static int updateOverallSeverity(int overallSeverity, int newSeverity) {
		int[] severities = new int[]{IStatus.ERROR, IStatus.WARNING, IStatus.INFO};
		for (int severity : severities) {
			if (newSeverity == severity) return severity;
			if (overallSeverity == severity) return severity;			
		}
		return IStatus.OK;	
	}
	
	public static MultiStatus getMultiStatus(int severity, String message, List<IStatus> statuses) {
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus[] statusesAsArray = statuses.toArray(new IStatus[0]);
		MultiStatus result = new MultiStatus(pid, IStatus.OK, statusesAsArray, message, null);
		return result;
	}
}
