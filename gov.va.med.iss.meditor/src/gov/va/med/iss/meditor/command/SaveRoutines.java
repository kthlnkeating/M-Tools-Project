package gov.va.med.iss.meditor.command;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.mumps.meditor.MEditorStatus;
import org.mumps.meditor.MEditorStatusSeverity;
import org.mumps.meditor.MEditorUtils;

public class SaveRoutines extends AbstractHandler {
	private static class FileAndStatus {
		public String filePath;
		public MEditorStatus status;
		
		public FileAndStatus(String filePath, MEditorStatus status) {
			super();
			this.filePath = filePath;
			this.status = status;
		}		
	}
	
	private boolean fillSelectedFiles(TreePath[] selections, List<IFile> validFiles) {
		for (TreePath path : selections) {
			Object lastSegment = path.getLastSegment();
			if (lastSegment instanceof IFile) {
				IFile selected = (IFile) lastSegment;
				String name = selected.getName();
				if (name.endsWith(".m")) {
					validFiles.add(selected);
				} else return false;
			} else return false;
		}
		return true;
	}
	
	private List<IFile> getFiles(TreeSelection treeSelection) {
		List<IFile> files = new ArrayList<IFile>();
		TreePath[] selections = treeSelection.getPaths();
		boolean invalidExists = ! this.fillSelectedFiles(selections, files);
		if (invalidExists) {
			MessageDialogHelper.showError(Messages.NOT_SUPPORTED_SELECTION);
			return null;
		}
		if (files.size() == 0) {
			MessageDialogHelper.showError(Messages.NO_FILES);
			return null;			
		}
		return files;
	}
	
	private static String getErrorDialogMessage(List<FileAndStatus> statusList, int numOfFileToWrite) {
		if (statusList.size() == 0) {
			return "";
		} else {
			StringBuilder fileBindings = new StringBuilder();
			int numWritten = 0;
			String eol = System.lineSeparator();
			for (FileAndStatus fns : statusList) {
				if (numWritten > numOfFileToWrite) break;
				fileBindings.append(eol);
				fileBindings.append(fns.filePath);
				++numWritten;
			}
			if (numWritten > numOfFileToWrite) {
				fileBindings.append(eol);
				fileBindings.append("...");
			}
			String message = Messages.bind(Messages.ERRORS_FOUND_FOR_FILES, fileBindings);
			return message;
		}
	}
	
	private static List<IStatus> getDetail(FileAndStatus fns, String pid) {
		List<IStatus> statuses = new ArrayList<IStatus>();
		boolean isError = fns.status.getSeverity() == MEditorStatusSeverity.ERROR;		
		int severity = isError ? IStatus.ERROR : IStatus.WARNING;
		if (isError) {
			String fileInfo = Messages.bind(Messages.FILE_NOT_SAVED, fns.filePath);
			statuses.add(new Status(severity, pid, 1, fileInfo, null));
		} else {
			String fileInfo = Messages.bind(Messages.FILE_SAVED, fns.filePath);
			statuses.add(new Status(severity, pid, 1, fileInfo, null));
		}
		String[] detailMessages = fns.status.getMessage().split("\n");
		for (String detailMessage : detailMessages) {
			statuses.add(new Status(severity, pid, 1, detailMessage, null));
		}
		statuses.add(new Status(severity, pid, 1, "\n", null));
		return statuses;
	}
		
	private static IStatus[] getDetails(List<FileAndStatus> statusList, String pid) {
		List<IStatus> statuses = new ArrayList<IStatus>();
		for (FileAndStatus fns : statusList) {
			List<IStatus> statusesForOne = getDetail(fns, pid);
			statuses.addAll(statusesForOne);
		}
		return statuses.toArray(new IStatus[0]);		
	}

	private static void showError(List<FileAndStatus> statusList) {
		String topMessage = getErrorDialogMessage(statusList, 2);
		String pid = MEditorPlugin.getDefault().getPluginId();
		IStatus[] details = getDetails(statusList, pid);
		MultiStatus info = new MultiStatus(pid, 1, details, topMessage, null);
		MessageDialogHelper.showMulti(info);		
	}
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		//String parameter = event.getParameter("gov.va.med.iss.meditor.command.saveRoutines.specifyNamespace");
		//boolean specifyNameSpace = (parameter != null) && parameter.equals("true");
		
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if ((selection == null) || (! (selection instanceof TreeSelection))) {
			MessageDialogHelper.showError(Messages.NOT_SUPPORTED_SELECTION_LIST);
			return null;
		}
		List<IFile> files = this.getFiles((TreeSelection) selection);
		if (files == null) {
			return null;
		}
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection != null) { 
			List<FileAndStatus> statusList = new ArrayList<FileAndStatus>();
			for (IFile file : files) {
				MEditorStatus status = MEditorUtils.save(connection, file);
				if (status.getSeverity() != MEditorStatusSeverity.OK) {
					String filePath = file.getFullPath().toString();
					FileAndStatus fns = new FileAndStatus(filePath, status);
					statusList.add(fns);
				}			
			}
			if (statusList.size() > 0) {
				showError(statusList);
			}
		}
		return null;
	}
}
