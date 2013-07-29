package gov.va.med.iss.meditor.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.StatusHelper;
import gov.va.med.iss.meditor.command.utils.MServerRoutine;
import gov.va.med.iss.meditor.editors.MEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class LoadMRoutine extends AbstractHandler {
	private static class RoutineNameValidator implements IInputValidator {
		@Override
		public String isValid(String newText) {
			if ((newText == null) || newText.isEmpty()) {
				return "Routine name is required.";
			}
			if (! newText.matches("[%A-Z][A-Z0-9]{0,7}")) {
				return "Invalid routine name.";
			}
			return null;			
		}
	}
	
	private String getRoutineName() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		String title = "Load Routine (from " + ConnectionUtilities.getServer() + ";" + ConnectionUtilities.getPort() + " to " + ConnectionUtilities.getProject() + ")";
		String msg  = "Please enter routine name:";
		final InputDialog dialog = new InputDialog(shell, title, msg, "", new RoutineNameValidator());
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialog.open();
			}
		});
		if (dialog.getReturnCode() == Window.OK) {
			String routineName = dialog.getValue();
			return routineName;
		} else {
			return  null;
		}
	}
	
	private IProject getProject(String projectName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			if (! project.exists()) {
				project.create(new NullProgressMonitor());
			}
			if (! project.isOpen()) {
				project.open(new NullProgressMonitor());
			}
		} catch (CoreException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String msg = "Cannot open project " + projectName;
			MessageDialog.openError(shell, "MEditor", msg);
			return null;
		}
		return project;
	}
	
	private void openEditor(IFile file) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(file), MEditor.M_EDITOR_ID);		
		} catch (PartInitException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String msg = "Unable to open editor for file " + file.getName();
			MessageDialog.openError(shell, "MEditor", msg);			
		}
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		String routineName = this.getRoutineName();
		if (routineName == null) {
			return null;
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		IProject project = this.getProject(projectName);
		if (project == null) {
			return null;
		}
		
		CommandResult<MServerRoutine> result = CommandEngine.loadRoutine(connection, project, routineName);
		IStatus status = result.getStatus();		
		if (status.getSeverity() != IStatus.OK) {
			StatusHelper.logAndShow(status);			
		}
		if (status.getSeverity() != IStatus.ERROR) {
			IFile file = result.getResultObject().getFileHandle();
			this.openEditor(file);
		}
		return null;
	}
}