/**
 * created 03/15/08
 */
package gov.va.med.iss.meditor.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import java.io.FileWriter;
import java.io.File;

/**
 * @author vhaisfiveyj
 *
 */
public class MultRoutineLoad {

	/**
	 *   This class takes a routine specifier, which may be ambiguous, and loads the routines
	 *   from the current primary server into a specified location on the client workstation.
	 */
	public MultRoutineLoad() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void loadMultipleRoutines(String routineName, String saveToDirectory) {
		// get a list of routine using RoutineDirectory
		String routineList = RoutineDirectory.getRoutineList(routineName);
		String errorType = "";
		boolean isError = false;
		int nFiles = 0;
		int newFiles = 0;
		int noChangeFiles = 0;
		int editedFiles = 0;
		String routinesLoaded = "";
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
					String codeFromServer = RoutineSave.getCodeFromServer(routine);
					if ( codeFromServer.compareTo("") == 0 ) {
						MessageDialog.openInformation(
								MEditorUtilities.getIWorkbenchWindow().getShell(),
								"M-Editor Plug-in",
								"Could not find routine: "+routine);
					}
					else {
						errorType = "saving";
						char c = saveToDirectory.charAt(saveToDirectory.length()-1);
						if (! (c == '\\')) {
							saveToDirectory = saveToDirectory + "\\";
						}
						checkDirectory(saveToDirectory);
						String fileName = saveToDirectory+routine+".m";
						File outFile = new File(fileName);
						boolean isOK = true;
						nFiles++;
						if (outFile.exists()) {
							String oldSourceCode = MEditorUtilities.fileToString(fileName);
							isOK = RoutineSave.checkForServerChange(routine, oldSourceCode, false, true);
							if (! (codeFromServer.compareTo(oldSourceCode) == 0)) {
								editedFiles++;
								RoutineCompare.compareRoutines(oldSourceCode,codeFromServer,"the version being loaded from the server",routine,false);
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
	
	private static void checkDirectory(String directoryName) {
		if (directoryName.charAt(directoryName.length()-1) == '\\') {
			directoryName = directoryName.substring(0,directoryName.length()-1);
		}
		if (! (new File(directoryName)).exists()) {
			int index = directoryName.lastIndexOf("\\");
			String previous = directoryName.substring(0,index);
			checkDirectory(previous);
//			boolean success = (new File(directoryName)).mkdir(); 
			(new File(directoryName)).mkdir(); 
		}
	}

}
