package gov.va.med.iss.meditor.dialog;

import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.core.StatusHelper;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class MessageDialogHelper {
	private static void show(int severity, String msgKey, String[] bindings, Throwable t, int dialogSeverity) {
		String message = (bindings.length == 0) ? msgKey : NLS.bind(msgKey, bindings);
		if (t != null) MEditorPlugin.getDefault().log(severity, message, t);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.open(dialogSeverity, shell, Messages.DEFAULT_MSG_TITLE, message, SWT.NONE);
	}
	
	public static void showWarning(Throwable t, String msgKey, String... bindings) {
		show( Status.WARNING, msgKey, bindings,t, MessageDialog.WARNING);
	}

	public static void showError(Throwable t, String msgKey, String... bindings) {
		show(Status.ERROR, msgKey, bindings, t, MessageDialog.ERROR);
	}
	
	public static void showInformation(List<String> msgKeyList, List<String[]> bindingsList) {
		int n = msgKeyList.size();
		if (n > 0) {
			if (n == 1) {
				showInformation(msgKeyList.get(0), bindingsList.get(0));
			} else {
				String fullMessage = "";
				for (int i=0; i<n; ++i) {
					String msgKey = msgKeyList.get(i);
					String[] bindings = bindingsList.get(i);					
					String message = (bindings.length == 0) ? msgKey : NLS.bind(msgKey, bindings);
					if (! fullMessage.isEmpty()) {
						fullMessage += "\n";
					}
					fullMessage += message;
				}
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageDialog.open(MessageDialog.INFORMATION, shell, Messages.DEFAULT_MSG_TITLE, fullMessage, SWT.NONE);				
			}
		}
	}
	
	public static void showMulti(MultiStatus statuses) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ErrorDialog.openError(shell, Messages.DEFAULT_MSG_TITLE, null, statuses);
	}
	
	public static void showError(String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.open(MessageDialog.ERROR, shell, Messages.DEFAULT_MSG_TITLE, message, SWT.NONE);
	}
	
	public static void showWarning(String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.open(MessageDialog.WARNING, shell, Messages.DEFAULT_MSG_TITLE, message, SWT.NONE);
	}
	
	public static void showInformation(String msgKey, String... bindings) {
		show(Status.INFO, msgKey, bindings, null, MessageDialog.INFORMATION);
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
	
	public static void logAndShow(IStatus status) {
		int severity = status.getSeverity();
		if (severity != IStatus.OK) {
			Throwable t = status.getException();
			String message = status.getMessage();
			if (t != null) {
				MEditorPlugin.getDefault().log(severity, message, t);
			}
			int dialogSeverity = getDialogSeverity(status.getSeverity());
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.open(dialogSeverity, shell, Messages.DEFAULT_MSG_TITLE, message, SWT.NONE);
		}
	}
	
	public static void logAndShow(String message, Throwable t) {
		IStatus status = StatusHelper.getStatus(message, t);
		logAndShow(status);
	}
	
	public static void logAndShow(Throwable t) {
		IStatus status = StatusHelper.getStatus(t);
		logAndShow(status);
	}
	
	public static void logAndShowUnexpected(Throwable t) {
		String message = Messages.bind(Messages.UNEXPECTED_INTERNAL, t.getMessage());
		IStatus status = StatusHelper.getStatus(message, t);
		logAndShow(status);
	}
}
