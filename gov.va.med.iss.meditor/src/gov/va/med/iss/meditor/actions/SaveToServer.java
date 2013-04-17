package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.RoutineSave;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SaveToServer implements IObjectActionDelegate {
	private ISelectionProvider selectionProvider;
	private Shell shell;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.selectionProvider = targetPart.getSite().getSelectionProvider();
		this.shell = targetPart.getSite().getShell();
	}

	private List<IFile> getSelectedFiles() {
		if (this.selectionProvider != null) {
			ISelection selection = this.selectionProvider.getSelection();
			if ((selection != null) && (selection instanceof ITreeSelection)) {
				ITreeSelection treeSelection = (ITreeSelection) selection;
				TreePath[] paths = treeSelection.getPaths();
				if ((paths != null) && (paths.length > 0)) {
					List<IFile> result = new ArrayList<>(paths.length);
					for (TreePath p : paths) {
						Object lastSegment = p.getLastSegment();
						if (lastSegment instanceof IFile) {
							IFile file = (IFile) lastSegment;
							result.add(file);
						}
					}
					return result;
				}
			}
		}
		return Collections.emptyList();
	}
	
	@Override
	public void run(IAction action) {
		List<IFile> files = this.getSelectedFiles();
		if (files.size() < 1) {
			MessageDialog.openInformation(this.shell, "Vista", "No file is selected.");
			return;
		}
		VistaLinkConnection c = VistaConnection.getConnection();
		if (c == null) {
			MessageDialog.openInformation(this.shell, "Vista", "No connection.");
			return;			
		}
		ITextFileBufferManager mgr = FileBuffers.getTextFileBufferManager();
		if (mgr == null) {
			MessageDialog.openInformation(this.shell, "Vista", "No buffer manager.");
			return;									
		}
		try {
			for (IFile file : files) {
				String name = file.getName().split("\\.m")[0];
				InputStream stream = file.getContents();
				Scanner scanner = new Scanner(stream).useDelimiter("\\A");
				String content = scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				RoutineSave.doSaveRoutine(name, content, c, false);
			}
		} catch (Exception t) {
			String msg = "Unexpected error.";
			MessageDialog.openInformation(this.shell, "Vista", msg);
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
