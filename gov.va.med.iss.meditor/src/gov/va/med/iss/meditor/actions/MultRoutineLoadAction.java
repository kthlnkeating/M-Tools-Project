/**
 * created 03/15/08
 */
package gov.va.med.iss.meditor.actions;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineCompare;
import gov.va.med.iss.meditor.utils.RoutineDirectory;
import gov.va.med.iss.meditor.utils.RoutineNameDialogData;
import gov.va.med.iss.meditor.utils.RoutineNameDialogForm;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.FileSystems;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.mumps.meditor.MEditorRPC;

/**
 * @author vhaisfiveyj
 *
 */
public class MultRoutineLoadAction implements IWorkbenchWindowActionDelegate {
	
	public static final String SEP = FileSystems.getDefault().getSeparator();

	public MultRoutineLoadAction() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		VistaLinkConnection connection = VistaConnection.getConnection();
		MEditorRPC rpc = new MEditorRPC(connection);
		
		RoutineNameDialogForm dialogForm = new RoutineNameDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell());
		RoutineNameDialogData data = dialogForm.openMultiple();
		if (data.getButtonResponse()) {
			String routineName = data.getTextResponse();
			String saveToDirectory = data.getDirectory();
			if (data.getUpperCase()) {
				routineName = routineName.toUpperCase();
			}
			if (! (routineName.compareTo("") == 0)) {
				// get a list of routine using RoutineDirectory
				String routineList = RoutineDirectory.getRoutineList(routineName);
				String errorType = "";
				boolean isError = false;
				int nFiles = 0;
				int newFiles = 0;
				int noChangeFiles = 0;
				int editedFiles = 0;
				String noChangeFileNames = "";
				String editedFileNames = "";
				String newFileNames = "";
				while ( routineList.compareTo("") > 0 ) {
					int loc = routineList.indexOf("\n");
					String routine = routineList.substring(0,loc);
					if (routineList.length() > loc) {
						routineList = routineList.substring(loc+1);
					}
					else {
						routineList = "";
					}
					if ( (routine.compareTo("") > 0) && (!isError) ) {
						try {
							errorType = "retrieving";
							String codeFromServer;
							try {
								codeFromServer = rpc.getRoutineFromServer(routine);
							} catch (Exception e) {
								continue; //jspivey-- for some reason some routines do not show up in cache studio or from the RPC but are listed in the directory
							} // RoutineSave.getCodeFromServer(routine);
							if ( codeFromServer.compareTo("") == 0 ) {
								MessageDialog.openInformation(
										MEditorUtilities.getIWorkbenchWindow().getShell(),
										"M-Editor Plug-in",
										"Could not find routine: "+routine);
							}
							else {
								errorType = "saving";
								if (!saveToDirectory.endsWith(SEP)) {
									saveToDirectory = saveToDirectory + SEP;
								}
								checkDirectory(saveToDirectory);
								String fileName = saveToDirectory+routine+".m";
								File outFile = new File(fileName);
								boolean isOK = true;
								nFiles++;
								if (outFile.exists()) {
									String oldSourceCode = MEditorUtilities.fileToString(fileName);
									isOK = true; //RoutineSave.checkForServerChange(routine, oldSourceCode, false, true); //jspivey-- this was always returning true anyway
									if (! (codeFromServer.compareTo(oldSourceCode) == 0)) {
										editedFiles++;
										RoutineCompare.compareRoutines(oldSourceCode,codeFromServer,"the version being loaded from the server",routine);
										editedFileNames = editedFileNames + ((editedFileNames.length() == 0)?"":", ")+routine;
									}
									else {
										noChangeFiles++;
										isOK = false;
										noChangeFileNames = noChangeFileNames + ((noChangeFileNames.length() == 0)?"":", ")+routine;
									}
								}
								else {
									newFiles++;
									newFileNames = newFileNames + ((newFileNames.length() == 0)?"":", ")+routine;
								}
								if (isOK) {
									FileWriter out = new FileWriter(outFile);
									out.write(codeFromServer);
									out.close();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openInformation(
									MEditorUtilities.getIWorkbenchWindow().getShell(),
									"M-Editor Plug-in",
									"Error encountered while "+errorType+" routine "+routine+": "+e.getMessage());
							isError = true;
						}
					}
				}
				if (! isError ) {
					String str = "Completed processing "+nFiles+" routines:\n\n";
					if (noChangeFiles > 0)
						str = str + noChangeFiles+" unchanged files - not reloaded: "+noChangeFileNames+"\n\n";
					if (editedFiles > 0)
						str = str + editedFiles+" edited files: "+editedFileNames+"\n\n";
					if (newFiles > 0)
						str = str + newFiles+" new files: "+newFileNames + "\n\n";
					newFiles = newFiles + editedFiles;
					if (newFiles > 0)
						str = str + newFiles + " routinesLoaded to "+saveToDirectory+"\n\n";
					MessageDialog.openInformation(
							MEditorUtilities.getIWorkbenchWindow().getShell(),
							"M-Editor Plug-in",
							str);
				}
			}
		}

	}
	
	private static void checkDirectory(String directoryName) {
		if (directoryName.endsWith(SEP)) {
			directoryName = directoryName.substring(0,directoryName.length()-1);
		}
		if (! (new File(directoryName)).exists()) {
			int index = directoryName.lastIndexOf(SEP);
			String previous = directoryName.substring(0,index);
			checkDirectory(previous);
//			boolean success = (new File(directoryName)).mkdir(); 
			(new File(directoryName)).mkdir(); 
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}
}
