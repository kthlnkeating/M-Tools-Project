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

package us.pwc.vista.eclipse.core.helper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public class MessageDialogHelper {
	public static boolean question(String title, String msgKey, String... bindings) {
		String message = (bindings.length == 0) ? msgKey : NLS.bind(msgKey, bindings);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		boolean result = MessageDialog.openQuestion(shell, title, message);
		return result;
	}
		
	public static void showMulti(String title, MultiStatus statuses) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ErrorDialog.openError(shell, title, null, statuses);
	}
	
	public static void showError(String title, String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.open(MessageDialog.ERROR, shell, title, message, SWT.NONE);
	}
	
	public static void showWarning(String title, String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.open(MessageDialog.WARNING, shell, title, message, SWT.NONE);
	}
	
	private static int getDialogSeverity(int severity) {
		switch (severity) {
		case IStatus.ERROR:
			return MessageDialog.ERROR;
		case IStatus.WARNING:
			return MessageDialog.WARNING;
		case IStatus.INFO:
			return MessageDialog.INFORMATION;
		default:
			return MessageDialog.NONE;
		}
	}
	
	public static void logAndShow(String title, IStatus status) {
		int severity = status.getSeverity();
		if (severity != IStatus.OK) {
			String message = status.getMessage();
			StatusManager.getManager().handle(status, StatusManager.LOG);
			int dialogSeverity = getDialogSeverity(status.getSeverity());
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.open(dialogSeverity, shell, title, message, SWT.NONE);
		}
	}
	
	public static void logAndShow(String pluginId, String message, Throwable t) {
		IStatus status = new Status(IStatus.ERROR, pluginId, message, t);
		StatusManager.getManager().handle(status, StatusManager.SHOW);
	}
	
	public static void logAndShow(String pluginId, Throwable t) {
		IStatus status = new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
		StatusManager.getManager().handle(status, StatusManager.SHOW);
	}
}
