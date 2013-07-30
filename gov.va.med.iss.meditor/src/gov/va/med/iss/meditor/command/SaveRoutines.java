package gov.va.med.iss.meditor.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.core.SaveRoutineEngine;
import gov.va.med.iss.meditor.core.StatusHelper;
import gov.va.med.iss.meditor.dialog.InputDialogHelper;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

public class SaveRoutines extends AbstractHandler {
	protected String getTopMessage(int overallSeverity) {
		if (overallSeverity == IStatus.ERROR) {
			return "Some file could not be saved due to errors.";
		} else if (overallSeverity == IStatus.WARNING) {
			return "All files are saved but some with warnings.";			
		} else {
			return "All files are saved successfully.";
		}
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		String parameter = event.getParameter("gov.va.med.iss.meditor.command.saveRoutines.specifyNamespace");
		boolean specifyNameSpace = (parameter != null) && parameter.equals("true");

		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return null;
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		List<IFile> selectedFiles = CommandCommon.getSelectedMFiles(event, projectName);
		if (selectedFiles == null) {
			return null;
		}
		
		if (specifyNameSpace) {
			String title = Messages.bind2(Messages.SAVE_M_RTNS_DLG_TITLE, ConnectionUtilities.getServer(), ConnectionUtilities.getPort(), ConnectionUtilities.getProject());
			String namespace = InputDialogHelper.getRoutineNamespace(title);
			if (namespace == null) {
				return null;
			}

			List<IFile> updatedFiles = new ArrayList<IFile>();
			for (IFile file : selectedFiles) {
				String name = file.getName();
				if (name.startsWith(namespace)) {
					updatedFiles.add(file);
				}
			}			
			if (updatedFiles.size() == 0) {
				String message = Messages.bind(Messages.NO_FILES_IN_NAMESPACE, namespace);
				MessageDialogHelper.showError(message);
				return null;
			}			
			selectedFiles = updatedFiles;
		}
				
		int overallSeverity = IStatus.OK;
		List<IStatus> statuses = new ArrayList<>();
		for (IFile file : selectedFiles) {
			IStatus status = SaveRoutineEngine.save(connection, file);
			String prefixForFile = file.getFullPath().toString() + " -- ";
			overallSeverity = StatusHelper.updateStatuses(status, prefixForFile, overallSeverity, statuses);
		}
		
		CommandCommon.showMultiStatus(overallSeverity, this.getTopMessage(overallSeverity), statuses);
		return null;		
	}
}
