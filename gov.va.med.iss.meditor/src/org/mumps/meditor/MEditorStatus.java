package org.mumps.meditor;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;

public class MEditorStatus {
	private MEditorStatusSeverity severity = MEditorStatusSeverity.OK;	
	private String message;
	
	public MEditorStatus() {
		this.severity = MEditorStatusSeverity.OK; 
	}
	
	public MEditorStatus(MEditorStatusSeverity severity, String message) {
		this.severity = severity;
		this.message = message;		
	}
	
	public MEditorStatusSeverity getSeverity() {
		return this.severity;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public boolean hasMessage() {
		return (this.message != null) && (this.message.length() > 0);
	}
	
	@SuppressWarnings("unused")
	private static int getLogSeverity(MEditorStatusSeverity severity) {
		switch (severity) {
		case ERROR:
			return Status.ERROR;
		case WARNING:
			return Status.WARNING;
		default:
			return Status.INFO;
		}
	}
	
	private static int getMessageDialogSeverity(MEditorStatusSeverity severity) {
		switch (severity) {
		case ERROR:
			return MessageDialog.ERROR;
		case WARNING:
			return MessageDialog.WARNING;
		default:
			return MessageDialog.INFORMATION;
		}
	}

	public void showMessage() {
		if (this.hasMessage()) {
			int dialogSeverity = getMessageDialogSeverity(this.severity);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.open(dialogSeverity, shell, Messages.DEFAULT_MSG_TITLE, this.message, SWT.NONE);
		}
	}
	
	private static MEditorStatus getExceptionInstance(String message, Throwable t) {
		if (t != null) {
			MEditorPlugin.getDefault().logInfo(message, t);
		}
		MEditorStatus result = new MEditorStatus(MEditorStatusSeverity.ERROR, message);
		return result;
	}
	
	public static MEditorStatus getInstance(Throwable t) {
		if (t instanceof MEditorException) {			
			return getExceptionInstance(t.getMessage(), t.getCause());
		} else {
			String message = Messages.bind(Messages.UNEXPECTED_INTERNAL, t.getMessage());
			return getExceptionInstance(message, t);
		}
	}
}
