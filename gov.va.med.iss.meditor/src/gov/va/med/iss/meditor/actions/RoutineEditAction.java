package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineChangedDialog;
import gov.va.med.iss.meditor.utils.RoutineChangedDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.mumps.meditor.MEditorRPC;
import org.mumps.meditor.RoutineNotFoundException;
import org.mumps.pathstructure.generic.PathFileSearchVisitor;
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

	/**
	 * The constructor.
	 */
	public RoutineEditAction() {
	}
		
	public void run(IAction action) {
		//Resolve object dependencies (calculate dependencies at the entry point up front)
		VistaLinkConnection connection = VistaConnection.getConnection();
		String projectName = VistaConnection.getPrimaryProject();
		MEditorRPC rpc = new MEditorRPC(connection);
		RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
		
		//Collect input
		RoutineNameDialogData dialogInput = dialogForm.open(true);
		String routineName = dialogInput.getTextResponse().trim().toUpperCase();
		//Validations on input
		validateInput(dialogInput, routineName);
		
		String serverCode;
		try { //attempt Load routine into a string, if not found show error
			serverCode = rpc.getRoutineFromServer(routineName);
		} catch (RoutineNotFoundException e) {
			//TODO: show error message about routine not existing on server
			return;
		}
		//Collect additional input from user (note: this should be moved into a new, redesign routine load dialog which calculates all the input needed up front
		String relRoutinePath;
		Path foundPath = searchForFile(Paths.get(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocationURI()), routineName+ ".m");
		if (foundPath != null) {
			relRoutinePath = foundPath.toString().substring(
					ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getLocation().toOSString().length() + 1,
					foundPath.toString().length() - routineName.length() - 2);
		} else {
			RoutinePathResolver routinePathResolver = RoutinePathResolverFactory
					.getInstance()
					.getRoutinePathResolver(
							ResourcesPlugin.getWorkspace().getRoot()
									.getProject(projectName).getLocation().toFile());
			relRoutinePath = routinePathResolver.getRelativePath(routineName);
		}
		

		


		//check to see if a routine is already loaded here. Are we syncing or are we loading it in new?
		IFile routineFile = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFile(relRoutinePath +SEP+ routineName + ".m");
		if (routineFile.exists()) {
			String fileCode;
			try {
				fileCode = readFile(routineFile);
			} catch (CoreException | IOException e) {
				e.printStackTrace();
				//TODO: show warning
				return;
			}
						
			//TODO: if cannot figure out how to load changes into an editor but not save them, show a warning ask what to do with a diff option
			if (serverCode.equals(fileCode)) {
				//TODO: just show a warning stating they are the same and do nothing
			} else {
				//replace the local contents with latest from server but do not save the actual file
				//FileEditorInput editorInput = new FileEditorInput(routineFile);
				RoutineChangedDialog dialog = new RoutineChangedDialog(Display.getDefault().getActiveShell());
				RoutineChangedDialogData userInput = dialog.open(routineName, serverCode, fileCode, false, false);
				if (!userInput.getReturnValue())
					return;
			}
		}

		//simply save the file
		try {
			createOrReplace(routineFile, serverCode);
		} catch (UnsupportedEncodingException | CoreException e) {
			e.printStackTrace();
			//TODO: show error
			return;
		}
		
		//Open the file in Eclipse
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput(routineFile), MEditor.M_EDITOR_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		//Save backup file(s)
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//		if (!backupDir.toFile().exists())
//			backupDir.toFile().mkdir();
		try {
			IFolder folder = iProject.getFolder("backups");
			if (!folder.exists())
				folder.create(true, true, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		IFile backupFile = iProject.getFile(
						"backups" +SEP+routineName+ " " +new SimpleDateFormat("yyMMdd").format(new Date())+ ".m");
		//note: backup routines should be in a static folder, not configured else that will move them around dynamically and cause bugs
		try {
			createOrReplace(backupFile, serverCode);
		} catch (UnsupportedEncodingException | CoreException e) {
			// TODO display error
			e.printStackTrace();
		}
		
		
		
		
//		VistaConnection.setBadConnection(false);
//		// JLI 110127 check that at least one server defined before showing dialog
//		//if (VistaConnection.getPrimaryServer().compareTo(";;;") != 0) {
//		if (VistaConnection.getPrimaryServer()) {
//			VistaLinkConnection currentConnection = VistaConnection.getCurrentConnection();
//			RoutineLoad.newPage = true;
//			RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
//			RoutineNameDialogData data = dialogForm.open(true);
//			if (data.getButtonResponse()) {
//				String routineName = data.getTextResponse();
//				boolean isReadOnly = data.getReadOnly();
//				if (data.getUpperCase()) {
//					routineName = routineName.toUpperCase();
//				}
//				if (routineName.length() > MEditorPlugin.P_SACC_MAX_LABEL_LENGTH) {  // SACC standard Maximum Label Length
//					MessageDialog.openInformation(
//							RoutineLoad.getWindow().getShell(),
//							"Meditor Plug-in",
//							// JLI 090918 - change Long Name problem to a warning
//							//"Routine Name Exceeds Maximum Length of "+
//							"Warning: Routine Name Exceeds Maximum Length of "+
//							   MEditorPlugin.P_SACC_MAX_LABEL_LENGTH + 
//							   " characters.\n\nIt will not be put in the "+
//							   "Routine file");
//				}
//				//  JLI 090918 Don't Force failure if routine name is longer than SAC standard
//				//  else if (! (routineName.compareTo("") == 0)) {
//				if (! (routineName.compareTo("") == 0)) {
//					//VistaConnection.getPrimaryServer(); //091029 - moved from RoutineLoad.routineLoad - to make check for change in servers
//					if (VistaConnection.getPrimaryServer()) {
//						
//						//jspivey-- display a project folder popup to get where to load the routine. moved to here so it only displays once, since it is invoked at the entry point
//						String projectName = MPiece.getPiece(VistaConnection.getCurrentServer(), ";", 4);
//						projectName = projectName.trim().equals("") ? "mcode" : projectName;
//						String userSelDirectory = null;
//						
//						//check for whether to display this prompt or not
//						if (containsFolder(projectName)) {
//							//display the prompt
//							CustElementTreeSelectionDialog dialog = new CustElementTreeSelectionDialog(Display.getDefault().getActiveShell(),
//									new WorkbenchLabelProvider(),
//								    new WorkbenchContentProvider());
//							dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
//							dialog.setAllowMultiple(false);
//							dialog.setMessage("Select a directory to load the routine into.");
//							dialog.setTitle("Routine Import");
//							ViewerFilter filter = new ProjectDirViewerFilter(projectName);
//							dialog.addFilter(filter);
//							
//							if (dialog.open() == Window.OK) {
//								IResource resource = (IResource) dialog.getFirstResult();
//								String path = resource.getLocation().toOSString();
//								userSelDirectory = path;
//							} else {
//								throw new RuntimeException("User cancelled file selection");
//							}
//						}
//						
//						RoutineLoad rl = new RoutineLoad();
//						rl.loadRoutine(routineName, true, isReadOnly, userSelDirectory);
//					}
//				}
//			}
//		}
	}

	private Path searchForFile(Path path, final String fileName) {
		
		if (fileName == null)
			return null;
		
		PathFileSearchVisitor fv = new PathFileSearchVisitor(fileName);
	      try {
	        Files.walkFileTree(path, fv);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
		
//		for (Path file : directory.) {
//			if (file.isDirectory())
//				return searchForFile(directory, fileName);
//			else {
//				if (file.getName().equals(fileName))
//					return Paths.get(file.toURI());
//			}
//		}
		
		return fv.getResult();		
	}

	private void createOrReplace(IFile routineFile, String serverCode)
			throws UnsupportedEncodingException, CoreException {
		
		InputStream stream = new ByteArrayInputStream(serverCode.getBytes("UTF-8"));
		if (!routineFile.exists())
			routineFile.create(stream, true, null);
		else
			routineFile.setContents(stream, true, true, null);
	}

	private String readFile(IFile routineFile) throws CoreException, IOException {
		//sync file
		//compare it, if equal do nothing and just show a warning
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(routineFile.getContents()));
		char[] buffer = new char[512];
		StringBuilder sb = new StringBuilder();
		int read;

		while ((read = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, read);
			buffer = new char[512];
		}

		reader.close();

		return sb.toString();
	}
	
	private boolean validateInput(RoutineNameDialogData dialogInput, String routineName) {
		if (!dialogInput.getButtonResponse())
			return false;
		
		if (!routineName.matches("%?[A-Z][A-Z0-9]+")) {
			//TODO: put validations into (redesigned) dialog
			return false;
		} else if (routineName.length() > 8) {
			//TODO: put validations into (redesigned) dialog
			return false;
		}
		
		return true;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}