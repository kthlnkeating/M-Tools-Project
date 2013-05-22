/*
 * Created on Aug 25, 2005
 */
package gov.va.med.iss.meditor.utils;

/*
 * 080605 added Routine Name to Saved to: statement in console on saving or on error
 * 
 * 080315 added arguments to saveRoutine to permit copies to different servers, and
 *        to control updating the first line or not.
 *        Initial save to primary server, still uses old format for primary server and
 *        updating of first line.
 *        
 * 051006 modified the code to convert tabs (at least at the beginning or after
 *        a tag to spaces before transmitting to the server.
 */

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;
import gov.va.med.iss.meditor.preferences.MEditorPropertyPage1;
import gov.va.med.iss.meditor.preferences.MEditorPreferencesPage;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineLoad;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.dialogs.SecondaryServerSelectionDialogData;
import gov.va.med.iss.connection.dialogs.SecondaryServerSelectionDialogForm;
import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;
import gov.va.med.iss.connection.utilities.MPiece;
import gov.va.med.iss.connection.actions.ConnectionData;
/*
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
*/
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date; // JLI 100811
import java.util.HashMap;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
/*
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.swt.widgets.Table;
*/
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * @author vhaisfiveyj
 */
public class RoutineSave {
	
	static private String[] checkedServers = null;
	static private String fullDoc = "";
	static private String currentServerName = "";
	static private String currentServerAddress = "";
	static private String currentServerPort = "";
	static private String currentServerProject = "";
	static private boolean currentIsCopy = false;
	static private boolean wasSaved = false;
	static private boolean cancelledDueToProduction = false;
	static private boolean dontAskSecondaries = false;
	static private String currentRoutineName = "";
	// original code - location of a copy of the file before editing 
	// populated by MEditor.doSave
//	static public String originalCodeLocation = "";
//	static public String currentCodeLocation ="";
	// JLI 100810 - save original code to this string instead of as
	// a file, i.e., in originalCodeLocation, since creating a new
	// file in a directory under version control requests whether
	// the new file be added to version control 
	static public String previousCode = "";
	// JLI 100813
	static public String currentCode = "";
	static private boolean encounteredError = false;
	
	/**
	 * 
	 */
	public static void saveRoutine(String routineFileName) {
		VistaLinkConnection myConnection;
		currentRoutineName = routineFileName;
		RoutineLoad.newPage = false;
		encounteredError = false;
		// 080329 JLI modified to handle saving to multiple servers
		IEditorPart part = MEditorUtilities.getIWorkbenchPage().getActiveEditor();
		String contentDescription = ((MEditor) part).getContentDescription();
		String registeredName = ((MEditor) part).getEditorSite().getRegisteredName();
		String partName = ((MEditor) part).getPartName();
		String editorTitle = ((MEditor) part).getTitle();
		String titleToolTip = ((MEditor) part).getTitleToolTip();
		int topIndex = ((MEditor) part).getTopIndex();
		String routinePrimaryServer = ((MEditor) part).getRoutineServer();
		if (routinePrimaryServer.compareTo("") == 0) {
			routinePrimaryServer = VistaConnection.getPrimaryServerID();
		}
		if (routinePrimaryServer.compareTo(VistaConnection.getPrimaryServerID()) != 0) {
			SecondaryServerSelectionDialogData.addCheckedServer(routinePrimaryServer);
			routinePrimaryServer = VistaConnection.getPrimaryServerID();
		}
		ArrayList serverList = ConnectionPreferencePage.getServerList();
		String server1 = (String)serverList.get(0);
		String server2 = VistaConnection.getPrimaryServerID();
		String name = MPiece.getPiece(server1,";");
		if (! (MPiece.getPiece(server2,";").compareTo("") == 0)) {
			if (! (name.compareTo(MPiece.getPiece(server2,";")) == 0)) {
				VistaConnection.clearPrimary();
				ConnectionData cData = VistaConnection.getMatchingConnection(
						MPiece.getPiece(server1,";",3),
						MPiece.getPiece(server1,";",2));
				if (! (cData == null) ) {
					VistaConnection.setPrimaryServer(cData);
				}
			}
		}
		if (serverList.size() > 1) {
			if (!dontAskSecondaries) {
				SecondaryServerSelectionDialogData.setArrayList(serverList);
				String[] servers = SecondaryServerSelectionDialogData.getServerList();
				// JLI 101027 modified for multiple servers with same address and port
				if (servers[0] != null) {
					SecondaryServerSelectionDialogForm secondary = new SecondaryServerSelectionDialogForm(MEditorUtilities.getIWorkbenchWindow().getShell(),SWT.NONE);
					secondary.open("SAVE",servers);
					dontAskSecondaries = SecondaryServerSelectionDialogData.getDontAsk();
				}
			}
			checkedServers = SecondaryServerSelectionDialogData.getSavedCheckedList();
//			// JLI 101027 modified for multiple servers with same address and port
			if (checkedServers != null) {
				int numChecked = checkedServers.length;
				if (numChecked > 0) {
					String value = checkedServers[0];
					value = value + " ";
				}
			}
		}
		// 080329 JLI end of modification
		// get primary connection
		
		String currentServer = VistaConnection.getCurrentServer();
		currentServerName = MPiece.getPiece(currentServer,";",1);
		currentServerAddress = MPiece.getPiece(currentServer,";",2);
		currentServerPort = MPiece.getPiece(currentServer,";",3);
		currentServerProject = MPiece.getPiece(currentServer,";",4);
		myConnection = VistaConnection.getConnection(currentServerName, currentServerAddress, currentServerPort, currentServerProject);
		fullDoc = "";
		if (! (myConnection == null)) {  // primary Server
			currentIsCopy = false;
			if (isSkipProductionAccount(myConnection)) {
				cancelledDueToProduction = true;
				setErrorDocHeader("Cancelled");
			}
			else {
			// JLI 101110 CHECK ON WHETHER PRODUCTION AND SKIP IF NECESSARY
// JLI 100810			if (!routineWasEdited(originalCodeLocation, currentCodeLocation)) {
// JLI 100813			if (!routineWasEdited(currentCodeLocation)) {
				if (!routineWasEdited()) {
					MessageDialog.openInformation(
							MEditorUtilities.getIWorkbenchWindow().getShell(),
							"Meditor Plug-in: Routine Save",
							"Routine saved is identical to the what is already deployed onto the server.");
					currentIsCopy = true;

				}
				saveRoutine(routineFileName, myConnection, currentIsCopy);
				if (!cancelledDueToProduction) {
					wasSaved = true;
				}
			}
		}
		else {
			setErrorDocHeader("Failed");
		}
		if (! (myConnection == null)) { // now to other servers
			currentIsCopy = true;
			encounteredError = false;
			// if prior save to primary server was cancelled due to Production account 
			// set up to update time on next save
			if (cancelledDueToProduction) {
				cancelledDueToProduction = false;
				if (! wasSaved) {
					currentIsCopy = false;
				}
			}
			// JLI 101027 modified for multiple servers with same address and port
			if (checkedServers != null) {
				for (int i=0; i<checkedServers.length; i++) {  // if any
					if (checkedServers[i].compareTo(routinePrimaryServer) != 0) {
						setServerInfo(checkedServers[i]);
						myConnection = VistaConnection.getConnection(currentServerName,currentServerAddress,currentServerPort, currentServerProject);
						if (isSkipProductionAccount(myConnection)) {
							cancelledDueToProduction = true;
							setErrorDocHeader("Cancelled");
						}
						else if (! (myConnection == null) ) {
							saveRoutine(routineFileName,myConnection,currentIsCopy);
							currentIsCopy = true;
						}
						else {
							setErrorDocHeader("Failed");
							MessageDialog.openWarning(
									MEditorUtilities.getIWorkbenchWindow().getShell(),
									"Meditor Plug-in Routine Save",
									"Unable to connect to "+currentServerName+" ("+currentServerAddress+", "+currentServerPort+")");
						}
					}
				}
			}
			writeDocToConsole(fullDoc);
		}
		currentIsCopy = false; // reset this before leaving
		wasSaved = false;
		cancelledDueToProduction = false;
		myConnection = VistaConnection.getConnection(); // reset to primary server
	}
	
// JLI 100810	private static boolean routineWasEdited(String previousCodeLocation, String currentCodeLocation) {
// JLI 100813	private static boolean routineWasEdited(String currentCodeLocation) {
	private static boolean routineWasEdited() {
		boolean result = false;
// JLI 100810 removed next line, added previousCode as a static public variable, now set in MEditor.doSave
//		String previousCode = MEditorUtilities.fileToString(previousCodeLocation);
// JLI 100813		String currentCode = MEditorUtilities.fileToString(currentCodeLocation);
		currentCode = cleanSource(currentCode);
		previousCode = cleanSource(previousCode);
		if (currentCode.compareTo(previousCode) != 0) {
			result = true;
		}
		return result;
	}

	public static void writeDocToConsole(String fullDoc){
		try {
			MEditorMessageConsole.writeToConsole(fullDoc);
		} catch (Exception e) {
			MessageDialog.openWarning(
					MEditorUtilities.getIWorkbenchWindow().getShell(),
					"Meditor Plug-in Routine Save",
					"Unable to send message to Console: "+e.getLocalizedMessage());
		}
	}
	
	public static void saveRoutine(String routineFileName, 
			VistaLinkConnection myConnection, boolean isCopy) {
		RoutineLoad.newPage = false;
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		String routineName;
		String doc = "";
		
		if (routineFileName.compareTo("") == 0)
			return;
		routineName = routineFileName.substring(0,routineFileName.length()-2);
		while (routineName.indexOf('/') > 0) {
			routineName = routineName.substring(routineName.indexOf('/')+1,routineName.length());
		}
		if ( ! (routineName.compareTo("") == 0)) {
			if (getReadOnlyState(routineName)) {
				MessageDialog.openWarning(
						MEditorUtilities.getIWorkbenchWindow().getShell(),
						"Meditor Plug-in Routine Save",
						"The routine "+routineName+" was loaded as READ-ONLY\n"
						+"\nChanges may have been saved locally, but they are\n"
						+"NOT being propagated to the server");
				return;
			}
/*
			// get code from when we last loaded into Eclipse
			String location = "";
			try {
				location = RoutineLoad.getFullFileLocation(routineName);
			} catch (Exception e) {

			}
			String fileName = RoutineLoad.getLastLoadFileName(routineName, location);
			String lastCodeLoaded = MEditorUtilities.fileToString(fileName);
			if (! checkForServerChange(routineName, lastCodeLoaded, true, false))
*/
			if (! doCheckForServerChange(routineName)) {
				return;
			}
			if (! (myConnection == null)) {
				try {
					String strval = MEditor.getTextFromActiveEditor();
					boolean hasText = doSaveRoutine(routineName, strval, myConnection, isCopy);
					// now get the saved copy back from the server and use that.
					// but don't update the backup copy on disk.
					
					if (! encounteredError) {
						RoutineLoad rl = new RoutineLoad();
						rl.loadRoutine(routineName, false);

						if (!hasText) {
							fullDoc = fullDoc + routineName + " saved.\n";
							MessageDialog.openConfirm(
								MEditorUtilities.getIWorkbenchWindow().getShell(),
								"Meditor Plug-in Routine Save",
								"Saved "+routineName+" to "+currentServerName+" ("+currentServerAddress+")\n***  No Problems encountered.  ***");

						}
					}
//					if (doc.length() > 0) {
//					fullDoc = fullDoc+doc;
//						MEditorMessageConsole.writeToConsole(doc);
//					}
				} catch (Exception e) {
					MessageDialog.openInformation(
							win.getShell(),
							"M-Editor Plug-in",
							"Error encountered while executing Routine Save "+e.getMessage());
					setErrorDocHeader("Failed");
				}
			}
		}
	}
	
	public static boolean doSaveRoutine(String routineName, String strval, 
			VistaLinkConnection myConnection, boolean isCopy) {
		RoutineLoad.newPage = false;
		String doc = "";
		currentRoutineName = routineName;
		try {
			int nlines = 0;
			HashMap hm = new HashMap();
			// remove spaces and tabs at end of line, replace first tab with space,
			// remove any non-tab control characters
			strval = cleanSource(strval); // JLI 100227 extracted code to cleanSource
			// now convert to array by line
			String stra= "";
			while (!(strval.compareTo("") == 0)) {
				int fromIndex1 = strval.indexOf('\n');
				if ( (fromIndex1 == -1))
					fromIndex1 = strval.length();
				if (fromIndex1 > -1) {
					stra = strval.substring(0,fromIndex1+1);
					if (strval.length() > fromIndex1)
						strval = strval.substring(fromIndex1+1);
					else
						strval = "";
					// remove line terminator
					stra = stra.replaceAll("\r\n","");
					// convert initial tab character to a space
					int loc = stra.indexOf("\t");
					if (loc > -1) {
						stra = stra.substring(0,loc)+" "+stra.substring(loc+1);
					}
					// skip blank lines
					if (! (stra.compareTo("") == 0)) {
						nlines = nlines + 1;
						hm.put(Integer.toString(nlines),stra);
					}
				}
			}
			
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
//			if (this.xmlRadioButton.isSelected()) {
//				vReq.setUseProprietaryMessageFormat(false);
//			} else {
				vReq.setUseProprietaryMessageFormat(true);
//			}
			vReq.getParams().setParam(1, "string", "RS");  // RD  RL  GD  GL  RS
			vReq.getParams().setParam(2, "array", hm);
			vReq.getParams().setParam(3, "string", routineName);
			String updateEntryInRoutineFile = MEditorPrefs.getPrefs(MEditorPlugin.P_DEFAULT_UPDATE);
			String unitTestName = getUnitTestName(routineName+".m");
			String updateFirstLine = isCopy ? "0" : "1";
			updateEntryInRoutineFile = (updateEntryInRoutineFile=="true") ? "1" : "0";
			vReq.getParams().setParam(4, "string",updateEntryInRoutineFile +"^"+unitTestName+"^"+updateFirstLine);
			RpcResponse vResp = myConnection.executeRPC(vReq);
			setFullDocHeader();

			int index1 = vResp.getResults().indexOf('\n');
			if (index1 > -1) {
				String line1 = vResp.getResults().substring(0,index1); //vResp.getResults().indexOf('\n'));
				if (line1.indexOf("-1") == 0) {
					MessageDialog.openWarning(
							MEditorUtilities.getIWorkbenchWindow().getShell(),
							"Meditor Plug-in Routine Save",
							MPiece.getPiece(line1,"^",2));
				}
				doc = vResp.getResults().substring(vResp.getResults().indexOf('\n'));
			}
			else
				doc = "";
			boolean isErrorsOrWarnings = false;
			int n = 0;
			if (doc.contains("no tags with variables to list")) {
				doc = doc.replace("Variables which are neither NEWed or arguments","");
				doc = doc.replace("no tags with variables to list","");
			}
			while (doc.contains("\n\n")) {
				doc = doc.replaceAll("\n\n","\n");
			}
			while (n < doc.length()) {
				int n1 = doc.indexOf('\n',n);
				String str = doc.substring(n,n1);
				int nbase = n;
				n = n1+1;
				if (str.indexOf("Compiled list of Errors and Warnings") == 0) {
					n1 = doc.indexOf('\n',n);
					String str1 = doc.substring(n,n1);
					if (str1.compareTo("No errors or warnings to report") == 0) {
						String str2 = "";
						if (nbase > 0) {
							str2 = doc.substring(0,nbase);
						}
						doc = str2 + doc.substring(n1+1,doc.length());
						n = nbase;
					}
					else
						isErrorsOrWarnings = true;
				}
				if (str.compareTo("Variables which are neither NEWed or arguments") == 0) {
					n = n + 1;  // skip blank line
					n1 = doc.indexOf('\n',n);
					String str1 = doc.substring(n,n1);
					if (str1.compareTo("no tags with variables to list") == 0) {
						String str2 = "";
						if (nbase > 0) {
							str2 = doc.substring(0,nbase);
						}
						doc = str2 + doc.substring(n1,doc.length());
						n = nbase;
					}
				}
			}
			while (doc.indexOf('\n') == 0) {
				if (doc.length() > 1)
					doc = doc.substring(1);
				else
					doc = "";
			}
			if (isErrorsOrWarnings) {
				MessageDialog.openWarning(
					MEditorUtilities.getIWorkbenchWindow().getShell(),
					"Meditor Plug-in Routine Save",
					"Routine saved, but XINDEX has reported errors or warnings in the M code. Refer to the Console for details.");
			}
		} catch (Exception e) {
				MessageDialog.openError(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"M-Editor Plug-in",
						"Error encountered while executing Routine Save: "+e.getMessage());
				setErrorDocHeader("Failed");
		}
		fullDoc = fullDoc + doc;
		return doc.length()>0;
	}
	
	/*
	 *   cleanSource - remove spaces and/or tabs at end of line, 
	 *                 and all other control characters
	 *                 
	 *   @param  strval   the input string
	 *   
	 */
	private static String cleanSource(String strval) {
		String result = "";
		String stra = "";
		while (!(strval.compareTo("") == 0)) {
			int fromIndex1 = strval.indexOf('\n');
			if ( (fromIndex1 == -1))
				fromIndex1 = strval.length()-1; //JLI 101102 fix string index out of range
			if (fromIndex1 > -1) {
				stra = strval.substring(0,fromIndex1+1);
				if (strval.length() > fromIndex1)
					strval = strval.substring(fromIndex1+1);
				else
					strval = "";

				// trim white space from end of lines
				while (stra.contains(" \r\n")) {
					stra = stra.replaceAll(" \r\n","\r\n");
				}
				while (stra.contains("\t\r\n")) {
					stra = stra.replaceAll("\t\r\n","\r\n");
				}
				// check and remove control characters other than tab
				if (stra.length() > 0) {
					int iCounter = 0;
					while (iCounter < stra.length()) {
						if (!(stra.charAt(iCounter) == '\t')) {
							if (stra.charAt(iCounter) < ' ') {
								stra = stra.substring(0,iCounter) + stra.substring(iCounter+1);
								iCounter--;

							}
						}
						iCounter++;
					}
				}
				// skip blank lines
				if (! (stra.compareTo("") == 0)) {
					result = result + stra + "\r\n";
				}
			}
		}
		return result;
	}
	
	private static boolean getReadOnlyState(String routineName) {
		boolean value = false;
		// modified next line, since it was ending up giving bad directories
//		routineName = RoutineLoad.getRelativeFileName(routineName);

		try {
			IContainer container = getContainer();
			if (! (container == null)) {
				IFile iFile = null;
				MEditorPropertyPage1 mepp = getMEditorPropertyPage1(routineName, container, iFile);
				value = mepp.getReadOnlyPropertyValue((IResource) iFile);
			}
		} catch (Exception e) {
			return false;
		}
		return value;
	}
	
	private static String getUnitTestName(String routineName){
		String unitTestName = "";
		String relRoutineName = "";
		if (VistaConnection.getCurrentProject().length() == 0) {
			// 101109 Changed from routineName to relRoutineName
			relRoutineName = RoutineLoad.getRelativeFileName(routineName);
		}
		try {
			IContainer container = getContainer();
			if (! (container == null)) {
				IFile iFile = null;
				iFile = container.getFile(new Path(relRoutineName));
				MEditorPropertyPage1 mepp = getMEditorPropertyPage1(routineName, container, iFile);
				unitTestName = mepp.getUnitTestNamePropertyValue((IResource) iFile);
			}
		} catch (Exception e) {
			return "";
		}
		if (unitTestName.contains("does not exist.")) {
			return ""; 
		}
		return unitTestName;
	}
	
	public static IFile getIFile(String routineName) {
		IFile iFile = null;
		routineName = RoutineLoad.getRelativeFileName(routineName);
		try {
			IContainer container = getContainer();
			if (! (container == null)) {
				iFile = container.getFile(new Path(routineName+".m"));
			}
		} catch (Exception e) {
		}
		return iFile;
	}
	
	private static MEditorPropertyPage1 getMEditorPropertyPage1(String routineName, IContainer container, IFile iFile) {
		routineName = RoutineLoad.getRelativeFileName(routineName);
		iFile = container.getFile(new Path(routineName));
		return new MEditorPropertyPage1();
	}
	
	public static IContainer getContainer() {
		IContainer container;
		try {
//			IResource resource = MEditorUtilities.getProject(MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME)); //"mcode");
			String project = MEditorPreferencesPage.getProjectName();
			IResource resource = MEditorUtilities.getProject(project);
			container = (IContainer)resource;
			container.refreshLocal(
					IResource.DEPTH_INFINITE, null);
		} catch (Exception e) {
			container = null;
		}
        return container;
	}
	
	/**
	 * Method checkForServerChange
	 * 
	 * @param String routineName - the name of the routine to check for changes
	 * @return boolean - false if new changes are present on the server and are rejected
	 */
	public static boolean checkForServerChange(String routineName, String lastCodeLoaded, boolean isSave, boolean isMult) {
		if (lastCodeLoaded.compareTo("") == 0) // nothing to check
			return true;
		try {
			// convert newlines only to return/newline
			if (lastCodeLoaded.indexOf("\r\n") == -1)
				lastCodeLoaded = lastCodeLoaded.replaceAll("\n","\r\n");
			// add terminating return/newline if not present
			if (lastCodeLoaded.charAt(lastCodeLoaded.length()-1) != '\n'
				&& lastCodeLoaded.charAt(lastCodeLoaded.length()-2) != '\r') {
				lastCodeLoaded = lastCodeLoaded + "\r\n";
			}
// moved to RoutineCompare			lastCodeLoaded = markBadChars(lastCodeLoaded);
			// get code from server
			String codeFromServer = getCodeFromServer(routineName);
// moved to RoutineCompare			codeFromServer = markBadChars(codeFromServer);
			if ((codeFromServer.compareTo(lastCodeLoaded) == 0) || (codeFromServer.compareTo("") == 0))
				return true;
			else if (!isSave && isMult) {
				return true;
			}
			else {
				RoutineChangedDialog dialog = new RoutineChangedDialog(MEditorUtilities.getIWorkbenchWindow().getShell());
				RoutineChangedDialogData value = dialog.open(routineName, lastCodeLoaded, codeFromServer, isSave, isMult);
				if (value.getReturnValue() && value.getSaveServerRoutine()) {
					String location1 = RoutineLoad.getFullFileLocation(routineName);
					// JLI 110127 changed date string from hhmm to HHmm to generate 24 hour values
					location1 = RoutineLoad.saveOldCopy(routineName, codeFromServer ,"yyMMdd_HHmm", location1);
					MessageDialog.openInformation(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Meditor Plug-in",
							"The version from the server has been saved as the file:\n\n"+location1);
				}
				return value.getReturnValue();
			}
		} catch (Exception e) {
			MessageDialog.openInformation(
					MEditorUtilities.getIWorkbenchWindow().getShell(),
					"M-Editor Plug-in",
					"Error encountered while checking last loaded version vs. the version on the server "+e.getMessage());
			return false;
		}
	}
	
	public static String markBadChars(String codeToCheck) {
		String result = "";
		int loc = 0;
		while (codeToCheck.length()>0) {
			int loc1 = codeToCheck.indexOf("\r\n",loc);
			String str1 = codeToCheck.substring(loc,loc1+2);
			if (codeToCheck.length() > loc1+2)
				codeToCheck = codeToCheck.substring(loc1+2);
			else
				codeToCheck = "";
			result = result + markBadCharsOnLine(str1);
		}
		return result;
	}
	
	private static String markBadCharsOnLine(String inputLine) {
		// show spaces and/or tabs from end of lines
		int loc=inputLine.length()-3;
		int count=0;
		char charVal = inputLine.charAt(loc);
		while (charVal <= ' ') {
			count++;
			loc--;
			if (loc < 0)
				break;
			charVal = inputLine.charAt(loc);
		}
		if (count > 0) {
			String endStr = inputLine.substring(loc+1);
			inputLine = inputLine.substring(0,loc+1);
			for (int i=0; i<count; i++) {
				charVal = endStr.charAt(i);
				if (charVal == ' ') {
					inputLine += "<span class=\"unexpected\" alt=\"unexpected character\">&lt;space&gt;</span>";
				}
				else if (charVal == '\t') {
					inputLine += "<span class=\"unexpected\" alt=\"unexpected character\">&lt;tab&gt;</span>";
				}
				else if (charVal == 0) {
					inputLine += "<span class=\"unexpected\" alt=\"unexpected character\">&lt;cntrl-Space&gt;</span>";
				}
				else {
					char controlChar = (char)(charVal+64);
					inputLine = inputLine+"<span class=\"unexpected\" alt=\"unexpected character\">&lt;cntrl-"+controlChar+"&gt;</span>";
				}
			}
			inputLine = inputLine + "\r\n";
		}
		boolean tabFound = false;
		String outputLine = "";
		for (int i=0; i<inputLine.length()-2; i++) {
			charVal = inputLine.charAt(i);
			if (charVal < ' ') {
				if (charVal == '\t') {
					if (! tabFound) {
						tabFound = true;
						outputLine += "\t";
					}
					else {
						outputLine += "&lt;tab&gt;";
					}
				}
				else if (charVal == 0) {
					outputLine += "&lt;cntrl-Space&gt;";
				}
				else {
					outputLine += "&lt;cntrl-"+((char)(charVal+64))+"&gt;";
				}
			}
			else {
				outputLine = outputLine + charVal;
			}
		}
		return outputLine + "\r\n";
	}
	
	public static String getCodeFromServer(String routineName) throws Exception {
		String codeFromServer = RoutineLoad.getRoutineFromServer(routineName);
		codeFromServer = codeFromServer.substring(codeFromServer.indexOf("\n")+1);
		if (codeFromServer.indexOf("\r\n") == -1)
			codeFromServer = codeFromServer.replaceAll("\n","\r\n");
		return codeFromServer;
	}
	
	static private void setServerInfo(String serverData) {
		currentServerName = MPiece.getPiece(serverData,";",1);
		currentServerAddress = MPiece.getPiece(serverData,";",2);
		currentServerPort = MPiece.getPiece(serverData,";",3);
		currentServerProject = MPiece.getPiece(serverData,";",4);
		VistaConnection.setCurrentServer(currentServerName,currentServerAddress, currentServerPort,currentServerProject);
	}
	
	static private void setFullDocHeader() {
		setDocHeaderSpacing();
		String currentServer = VistaConnection.getCurrentServer();
		currentServerName = MPiece.getPiece(currentServer,";");
		currentServerAddress = MPiece.getPiece(currentServer,";",2);
		currentServerPort = MPiece.getPiece(currentServer,";",3);
		currentServerProject = MPiece.getPiece(currentServer,";",4);
		fullDoc = fullDoc + currentRoutineName +" saved to: "
			+ currentServerName + " ("+currentServerAddress+", "+currentServerPort+")\n\n";
	}
	
	static void setErrorDocHeader(String type) {
		setDocHeaderSpacing();
		fullDoc = fullDoc + currentRoutineName +
			" **"+type+"** on save to: "+ currentServerName + " ("+currentServerAddress+", "+currentServerPort+")\n";
		encounteredError = true;
	}
	
	static void setDocHeaderSpacing() {
		if (! (fullDoc.compareTo("") == 0)) {
			fullDoc = fullDoc + "\n\n";
		}
	}
	
	static public void clearFullDoc() {
		fullDoc = "";
	}
	
	static public String getFullDoc() {
		return fullDoc;
	}
	
	static public boolean isCopy() {
		return currentIsCopy;
	}
	
	static private boolean isSkipProductionAccount(VistaLinkConnection myConnection) {
		boolean response = false;
		// check for production account
		try {
		if (VistaConnection.checkForProductionAccount(myConnection)) {
			// and if production check on skipping it
			// user responds YES to skip
			response = MessageDialog.openQuestion(
					MEditorUtilities.getIWorkbenchWindow().getShell(),
					"Meditor Plug-in Routine Save - PRODUCTION account",
					"** WARNING **\n\n"+
					"This is a PRODUCTION account ("+VistaConnection.getCurrentServer()+").\n" +
					"The M-Editor discourages saving a routine into a PRODUCTION account \n" +
					"and wants to cancel the save to this account (save to other accounts, \n" +
					"if any, would be continued).  A YES RESPONSE WILL CANCEL THE SAVE.\n\n" +
					"If saving into this account is absolutely necessary, press the NO button, \n" +
					"but **YOU** will be responsible for any problems arising from that action.");
		}
		} catch (Exception e) {
			
		}
		return response;
	}
	
	static boolean doCheckForServerChange(String routineName) {
		// get code from when we last loaded into Eclipse
		String location = "";
		try {
			location = RoutineLoad.getFullFileLocation(routineName);
		} catch (Exception e) {

		}
		String fileName = RoutineLoad.getLastLoadFileName(routineName, location);
		String lastCodeLoaded = MEditorUtilities.fileToString(fileName);
		return checkForServerChange(routineName, lastCodeLoaded, true, false);
	}
	
}
