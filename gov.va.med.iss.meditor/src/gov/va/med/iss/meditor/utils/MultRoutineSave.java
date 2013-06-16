package gov.va.med.iss.meditor.utils;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;

import java.io.File;
import java.nio.file.FileSystems;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.mumps.meditor.MEditorRPC;
import org.mumps.meditor.MEditorUtils;

public class MultRoutineSave {
	
	private static final String SEP = FileSystems.getDefault().getSeparator();
	
	public static void saveMultipleRoutines(String loadFromDirectory, String routine) {
		
		VistaLinkConnection connection = VistaConnection.getConnection();
		MEditorRPC rpc = new MEditorRPC(connection);
		
		String routines = expandNames(loadFromDirectory, routine);
		routines = routines.substring(0,routines.length()-1);
		if (!loadFromDirectory.endsWith(SEP)) {
			loadFromDirectory = loadFromDirectory + SEP;
		}
		if (routines.compareTo("") == 0) {
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"M-Editor Plug-in",
					"There were no matching routines found to save");
			return;
		}
		String saveResults = "";
		while (routines != "") {
			int loc = routines.indexOf(",");
			String rouname;
			if (loc > -1) {
				rouname = routines.substring(0,loc);
				routines = routines.substring(loc+1);
			}
			else {
				rouname = routines;
				routines = "";
			}
			String filename = loadFromDirectory+rouname;
			rouname = rouname.substring(0,rouname.indexOf("."));
			try {
//				String codeFromServer = RoutineSave.getCodeFromServer(rouname);
				// get code from when we last loaded into Eclipse
				String codeFromDisk = MEditorUtilities.fileToString(filename);
				boolean isOK = true; //RoutineSave.checkForServerChange(rouname, codeFromDisk, true, true);
				if (isOK) {
					saveResults += rpc.saveRoutineToServer(rouname, codeFromDisk, false);
					//RoutineSave.doSaveRoutine(rouname, codeFromDisk, myConnection, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
				saveResults += "Unable to save routine " +rouname+ " to server"+SEP;
			}
		}
		try {
			MEditorMessageConsole.writeToConsole(saveResults);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * expandNames - takes a complete directory and a comma separated list
	 *               of routine names (the names may contain * to indicate
	 *               ambiguous parts) and returns a comma separated list of
	 *               all the matching routines in the specified directory.
	 */
	static String expandNames(String directory, String routine) {
		String results = "";
		routine = routine + ",";
		while (routine.indexOf(",") > -1) {
			int loc = routine.indexOf(",");
			String name = routine.substring(0,loc);
			if (name.compareTo("") != 0) {
				routine = routine.substring(loc+1);
				if (name.indexOf("*") > -1) {
					results = results + getFileList(directory,name+".m");
				}
				else if (!(getFileList(directory,name+".m").compareTo("") == 0)) {
					results = results + name+".m" + ",";
				}
			}
		}
		return results;
	}
	
	public static void main(String args[]) {
		//String test = getFileList("c:\\exe","*.pl");
		//test = getFileList("c:\\exe","del*.pl");
		//String test = expandNames("c:\\exe","html2text.java,fil*.pl,del*.pl");
		saveMultipleRoutines("c:\\exe","JLI*.m");
	}
	
	static String getFileList(String directory, String fileName) {
		String result = ""; 
		File dir = new File(directory);
		String[] files = dir.list();
		String[] parts = new String[20];
		int partsCount = 0;
		while (fileName.indexOf("*") > -1) {
			int loc = fileName.indexOf("*");
			String part = "";
			if (loc > 0) {
				part = fileName.substring(0,loc);
			}
			if (!part.matches("")) {
				parts[partsCount++] = part.toUpperCase();
			}
			fileName = fileName.substring(loc+1);
		}
		for (int i=0; i<files.length; i++) {
			boolean match = true;
			int base = 0;
			String file = files[i].toUpperCase();
			String matchFile = "";
			for (int j=0; j<parts.length; j++) {
				if (parts[j] == null) {
					break;
				}
				String part = parts[j];
				if (file.indexOf(part,base) > -1) {
					int loc = file.indexOf(part,base);
					matchFile = matchFile + file.substring(base,loc)+part;
					base = loc + part.length();
				}
				else {
					match = false;
					break;
				}
			}
			if (match) {
				String matchSubString = file.substring(0,matchFile.length());
				String ext = Character.toString(file.charAt(file.length()-2));
				ext = ext + Character.toString(file.charAt(file.length()-1));
				if (ext.compareTo(".M") == 0) {
					if (matchFile.compareTo(matchSubString) == 0) {
						result = result + file+",";
					}
				}
			}
		}
		return result;
	}

}
