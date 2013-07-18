package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.meditor.editors.MEditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.mumps.meditor.MEditorRPC;
import org.mumps.meditor.MEditorUtils;
import org.mumps.meditor.RoutineNotFoundException;
import org.mumps.meditor.dialogs.RoutineDiffersDialog;
import org.mumps.pathstructure.vista.RoutinePathResolver;
import org.mumps.pathstructure.vista.RoutinePathResolverFactory;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class RoutineEditAction implements IWorkbenchWindowActionDelegate {
	
	private static final String SEP = FileSystems.getDefault().getSeparator();

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
	
	private static class RoutineVisitor implements IResourceProxyVisitor {
		private IFile file;
		private String fileName;
		
		public RoutineVisitor(String routineName) {
			this.fileName = routineName + ".m";
		}
		
		@Override
		public boolean visit (IResourceProxy proxy) { 
			if (this.file != null) {
				return false;
			}
			String name = proxy.getName();
			if (! name.equals(this.fileName)) {
				return true;
			}
			if (proxy.getType() != IResource.FILE) {
				return true;
			}
			this.file = (IFile) proxy.requestResource();
			return false;
        } 
		
		public IFile getFile() {
			return this.file;
		}
	}
		
	public RoutineEditAction() {
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
	
	private String getRoutineCodeFromServer(VistaLinkConnection connection, String routineName) {
		MEditorRPC rpc = new MEditorRPC(connection);
		try {
			String serverCode = rpc.getRoutineFromServer(routineName);
			return serverCode;
		} catch (RoutineNotFoundException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String msg = "Routine " + routineName + " does not exist on the server.";
			MessageDialog.openInformation(shell, "MEditor", msg);
			return null;
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
	
	private IFile getExistingRoutine(IProject project, String routineName) {
		RoutineVisitor visitor = new RoutineVisitor(routineName);
		try {
			project.accept(visitor, 0);
		} catch (CoreException e) {
			return null;
		}
		return visitor.getFile();
	}
	
	private Boolean compareStreams(InputStream clientStream, InputStream serverStream) {
		try {
			while (true) {
				int c = clientStream.read();
				int s = serverStream.read();

				if (c != s) {
					return false;
				}
		    
				if (s == -1) {
					return true;
				}
		    } 
		} catch (IOException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String msg = "IO Error during camparing server and client versions of the routine.";
			MessageDialog.openError(shell, "MEditor", msg);
			return null;
		}
	}
	
	private void runForExistingFile(IFile clientRoutine, String serverCode) {
		try {
			InputStream clientStream = clientRoutine.getContents();
			InputStream serverStream = new ByteArrayInputStream(serverCode.getBytes());		
			Boolean areTheSame = this.compareStreams(clientStream, serverStream);
			clientStream.close();
			serverStream.close();
			if (areTheSame != null) {
				if (! areTheSame.booleanValue()) {
					InputStream source = new ByteArrayInputStream(serverCode.getBytes());		
					clientRoutine.setContents(source, true, true, null);
				}
				this.openEditor(clientRoutine);
				if (! areTheSame.booleanValue()) {
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					String msg = "Project file " + clientRoutine.getName() + " is updated.";
					msg += "\n" + "You can use local history to see the changes."; 
					MessageDialog.openError(shell, "MEditor", msg);
				}
			}		
		} catch (CoreException | IOException e) {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			String msg = "Unable to open existing file " + clientRoutine.getName() + " does not exist on the server";
			MessageDialog.openError(shell, "MEditor", msg);
		}
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
	
	public void run(IAction action) {
		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return;
		}
		
		String routineName = this.getRoutineName();
		if (routineName == null) {
			return;
		}
		
		String serverCode = this.getRoutineCodeFromServer(connection, routineName);
		if (serverCode == null) {
			return;
		}
		
		String projectName = VistaConnection.getPrimaryProject();
		IProject project = this.getProject(projectName);
		if (project == null) {
			return;
		}
		
		IFile existingRoutine = this.getExistingRoutine(project, routineName);
		if (existingRoutine != null) {
			runForExistingFile(existingRoutine, serverCode);
			return;
		}
				
		String relRoutinePath;
		RoutinePathResolver routinePathResolver = RoutinePathResolverFactory
					.getInstance()
					.getRoutinePathResolver(
							project.getLocation().toFile());
		relRoutinePath = routinePathResolver.getRelativePath(routineName);
			
		//make sure that this relative path exists
		createFolders(relRoutinePath, project);
		
		//check to see if a routine is already loaded here. Are we syncing or are we loading it in new?
		IFile routineFile = project.getFile(relRoutinePath +SEP+ routineName + ".m");
		if (routineFile.exists()) {
			String fileCode;
			try {
				fileCode = MEditorUtils.readFile(routineFile);
			} catch (CoreException | IOException e) {
				e.printStackTrace();
				//show error
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "MEditor",
						"Failed to load routine. Error occured while loading the local code for compairson to the server: " +e.getMessage());
				return;
			}
						
			//if cannot figure out how to load changes into an editor but not save them, show a warning ask what to do with a diff option
			if (!MEditorUtils.compareRoutines(serverCode, fileCode)) {
				//replace the local contents with latest from server but do not save the actual file
				RoutineDiffersDialog dialog = new RoutineDiffersDialog(Display
						.getDefault().getActiveShell(),
						"Routine " +routineName+ " found on server and locally in the project " +projectName+". Would you like to overwrite the local version with the server version?",
						" the project version (" +routineFile.getFullPath().toOSString()+ ")",
						routineName, MEditorUtils.cleanSource(fileCode),
						MEditorUtils.cleanSource(serverCode));
				if (dialog.open() != Dialog.OK)
					return;
			}
		}

		//simply save the file
		try {
			MEditorUtils.createOrReplace(routineFile, serverCode);
		} catch (UnsupportedEncodingException | CoreException e) {
			e.printStackTrace();
			//show error
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"Failed to load routine. Error occured while writing the local file: " +e.getMessage());
			return;
		}
		
		this.openEditor(routineFile);
		
		//Save backup file(s)
		try {
			MEditorUtils.syncBackup(projectName, routineName, serverCode);
		} catch (CoreException e) {
			// show warning only
			e.printStackTrace();
			e.printStackTrace();
			MessageDialog.openWarning(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"Routine Saved on server, but error occured while syncing the local backup file: " +e.getMessage());	
		}
	}

	private void createFolders(String relPath, IProject project) {
		
		if (relPath.equals("") || project.getFolder(relPath).exists())
			return;

		String[] paths;
		if (relPath.indexOf("/") == -1)
			paths = new String[] {relPath};
		else
			paths = relPath.split("/");
		String createPath = "";
		for (int i = 0; i < paths.length; i++) {
			createPath += paths[i] + "/";
			if (project.getFolder(createPath).exists())
				continue;

			try {
				project.getFolder(createPath).create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}