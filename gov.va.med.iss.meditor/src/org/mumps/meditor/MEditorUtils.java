package org.mumps.meditor;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.MPiece;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.command.resource.ResourceUtilsExtension;
import gov.va.med.iss.meditor.command.utils.MServerRoutine;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;
import gov.va.med.iss.meditor.utils.MEditorMessageConsole;
import gov.va.med.iss.meditor.utils.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;

public class MEditorUtils {
	/** Returns the handle for the backup file for a file in a project. Backup  
	 *  files store the M server versions whenever they are loaded to client. 
	 *  Backup files are stored in a preference determined project directory.  
	 *  The relative path of a specific backup file in this backup directory 
	 *  is identical to the relative path of the actual file in the project.
	 *  <p>
	 *  This method does not check existence or otherwise do any validations
	 *  on the handle.   
	 * 
	 * @see IFile
	 * @param file
	 * @return handle to the backup file.                                                              
	 */	
	public static IFile getBackupFile(IFile file) {
		IProject project = file.getProject();
		IPath projectPath = project.getFullPath();
		IPath filePath = file.getFullPath();
		IPath relativeFilePath = filePath.makeRelativeTo(projectPath);
		String backupFolderName = MEditorPrefs.getServerBackupFolderName();
		Path relativeBackupFolderPath = new Path(backupFolderName);
		IPath relativeBackupFilePath = relativeBackupFolderPath.append(relativeFilePath);
		IFile backupFile = project.getFile(relativeBackupFilePath);
		return backupFile;
	}
		
	/** Returns the routine name for an M file. Backup  
	 * 
	 * @param file
	 * @return routine name.
	 * @throws MEditorException when file is not an M file.                                                              
	 */	
	public static String getRoutineName(IFile file) throws InvalidFileException {
		String fileName = file.getName();
		if (! fileName.endsWith(".m")) {
			throw new InvalidFileException(Messages.UNEXPECTED_EDITOR_FILE_NOTM);
		}
		int length = fileName.length();
		String routineName = fileName.substring(0, length-2);
		return routineName;
	}

	/**
	 * Save the backup file. Create it if one doesn't exist. Also change the
	 * filename to end on todays date in the case a file already exists.
	 * 
	 * @param projectName
	 * @param routineName
	 * @param serverCode
	 * @throws CoreException
	 */
	public static void syncBackup(IFile file, String serverCode) throws CoreException, IOException {		
		IFile backupFile = getBackupFile(file);
		InputStream stream = new ByteArrayInputStream(serverCode.getBytes("UTF-8"));
		if (! backupFile.exists()) {
			ResourceUtilsExtension.prepareFolders((IFolder) backupFile.getParent());			
			backupFile.create(stream, true, null);
		} else {
			backupFile.setContents(stream, true, true, null);
		}
		stream.close();
	}
		
	/**
	 * Converts all tabs to space, removes all control characters, and removes
	 * all empty lines.  This method also makes sure that all the end of line
	 * characters are consistent. 
	 * does not Cleans M code from control characters inlcud
	 * filename to end on todays date in the case a file already exists.
	 * 
	 * @param source M code.
	 * @param target updated M code.
	 * @return if any updated needed.
	 * @throws BadLocationException
	 */
	public static boolean cleanMCode(IDocument source, IRoutineBuilder target) throws BadLocationException { 
        boolean result = false; 
		String eol = ((IDocumentExtension4) source).getDefaultLineDelimiter();
        int n = source.getNumberOfLines(); 
        for (int i=0; i<n; ++i) { 
        	IRegion lineInfo = source.getLineInformation(i); 
        	int lineLength = lineInfo.getLength();
        	boolean emptyLine = (lineLength == 0); 
        	result = result || (emptyLine && (source.getLineLength(i) > 0)); 
        	if (! emptyLine) { 
        		int offset = lineInfo.getOffset(); 
        		String lineText = source.get(offset, lineLength); 
        		if (lineText.indexOf('\t') >= 0) { 
        			lineText = lineText.replace('\t', ' '); 
        			result = true; 
        		}        		
        		lineText = lineText.replaceAll("\\p{Cntrl}", "");        		
        		result = result || (lineText.length() != lineLength);
        		if (lineText.trim().isEmpty()) {
        			result = true;
        			continue;
        		}
        		String eolLine = source.getLineDelimiter(i);
        		if (! eol.equals(eolLine)) {
        			result = true;
        		}
        		target.appendLine(lineText, eol);
        	} 
        } 
        return result;
 	} 
	
	private static ListRoutineBuilder getListRoutineBuilder(IFile file) throws InvalidFileException, CoreException, BadLocationException {		
		IDocument document = ResourceUtilsExtension.getDocument(file);
		ListRoutineBuilder target = new ListRoutineBuilder();
		boolean updated = cleanMCode(document, target);
		if (updated) {
			String message = Messages.bind(Messages.NOT_SUPPORTED_MFILE_CONTENT, file.getName());
			throw new InvalidFileException(message);
		}
		return target;
	}
	
	private static MEditorStatus saveRoutineToServer(VistaLinkConnection connection, String routineName, ListRoutineBuilder builder, IFile backupFile, StringBuilder consoleMessage) {
		String warningMessage = "";
		try {
			List<String> contents = builder.getRoutineLines();
			MEditorStatus result = saveRoutineToServer(connection, routineName, contents, consoleMessage);
			if (result.getSeverity() == MEditorStatusSeverity.ERROR) {
				return result;
			}
			if (result.hasMessage()) {
				String message = result.getMessage();
				warningMessage += "\n" + message;
			}
			
			MEditorMessageConsole.writeToConsole(consoleMessage.toString());
			
			String routine = builder.getRoutine();
			synchBackupFile(backupFile, routine);
		} catch (BackupSynchException bse) {
			String message = bse.getMessage();
			MEditorPlugin.getDefault().logError(message, bse);
			warningMessage += "\n" + message;
		} catch (Throwable t) {
			String message = Messages.bind(Messages.UNABLE_RTN_SAVE, routineName);
			MEditorPlugin.getDefault().logError(message, t);
			return new MEditorStatus(MEditorStatusSeverity.ERROR, message);
		}		

		if (warningMessage.length() > 0) {
			warningMessage = Messages.bind(Messages.ROUTINE_SAVED_W_WARNINGS, routineName) + warningMessage;
			return new MEditorStatus(MEditorStatusSeverity.WARNING, warningMessage); 
		} else {
			return new MEditorStatus();
		}
	}
	
	private static StringBuilder startConsoleMessage(String routineName, BackupSynchResult synchResult, IFile backupFile) {
		StringBuilder result = new StringBuilder();
		String currentServer = VistaConnection.getCurrentServer();
		String currentServerName = MPiece.getPiece(currentServer,";");
		String currentServerAddress = MPiece.getPiece(currentServer,";",2);
		String currentServerPort = MPiece.getPiece(currentServer,";",3);
		String header = routineName + " saved to: " + currentServerName + " ("+currentServerAddress+", "+currentServerPort+")\n";
		result.append(header);
		if (synchResult == BackupSynchResult.INITIATED) {
			String message = Messages.bind(Messages.SERVER_FIRST_SAVE, backupFile.getFullPath().toString());
			result.append(message);
			result.append("\n");
		}
		if (synchResult == BackupSynchResult.NO_CHANGE_SERVER_DELETED) {
			result.append(Messages.SERVER_DELETED);				
			result.append("\n");
		}
		result.append("\n");
		return result;
	}

	private static MEditorStatus synchBackupFile(IFile backupFile, String routine) throws BackupSynchException {
		try {
			InputStream stream = StringUtil.stringToStream(routine); 
			if (backupFile.exists()) {
				backupFile.setContents(stream, true, true, null);
			} else {
				ResourceUtilsExtension.prepareFolders((IFolder) backupFile.getParent());			
				backupFile.create(stream, true, null);
			}
			stream.close();
			return new MEditorStatus();
		} catch (Throwable t) {
			String message = Messages.bind(Messages.SAVE_BACKUP_SYNCH_ERROR, backupFile.getName());
			throw new BackupSynchException(message, t);
		} 
	}
	
	private static boolean updateConsoleMessage(String doc, StringBuilder consoleMessage) {
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
		consoleMessage.append(doc);
		return isErrorsOrWarnings;
	}
	
	public static MEditorStatus saveRoutineToServer(VistaLinkConnection connection, String routineName, List<String> contents, StringBuilder consoleMessage) throws FoundationsException {
		RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
		vReq.setUseProprietaryMessageFormat(true);
		vReq.getParams().setParam(1, "string", "RS");  // RD  RL  GD  GL  RS
		vReq.getParams().setParam(2, "array", contents);
		vReq.getParams().setParam(3, "string", routineName);
		String updateEntryInRoutineFile = MEditorPrefs.getPrefs(MEditorPlugin.P_DEFAULT_UPDATE);
		//String unitTestName = getUnitTestName(routineName+".m"); //--jspivey not supported beause it is loading this value into the eclipse persistence store when the routine is loaded in. This won't work for routines imported from the filesystem
		String unitTestName = "";
		String updateFirstLine = "0";  //isCopy ? "1" : "0";
		//String updateFirstLine = "0"; //fixed because it makes syncing and comparing files difficult when it changes on the server but not locally --jspivey
		updateEntryInRoutineFile = "0";   //(updateEntryInRoutineFile=="true") ? "1" : "0";
		vReq.getParams().setParam(4, "string",updateEntryInRoutineFile +"^"+unitTestName+"^"+updateFirstLine);
		RpcResponse vResp = connection.executeRPC(vReq);

		int index = vResp.getResults().indexOf('\n');
		if (index > -1) {
			String line1 = vResp.getResults().substring(0, index);
			if (line1.indexOf("-1") == 0) {
				String message = MPiece.getPiece(line1,"^",2);
				MEditorStatus r = new MEditorStatus(MEditorStatusSeverity.ERROR, message);
				return r;
			}
			String doc = vResp.getResults().substring(vResp.getResults().indexOf('\n'));
			boolean isErrorsOrWarnings = updateConsoleMessage(doc, consoleMessage);
			if (isErrorsOrWarnings) {
				MEditorStatus r = new MEditorStatus(MEditorStatusSeverity.WARNING, Messages.XINDEX_IN_CONSOLE);
				return r;
			}			
		}
		return new MEditorStatus();
	}
	
	public static MEditorStatus save(VistaLinkConnection connection, IFile file) {
		try {
			String projectName = VistaConnection.getPrimaryProject();
			if (! file.getProject().getName().equals(projectName)) {
				String message = Messages.bind(Messages.EDITOR_FILE_WRONG_PROJECT, file.getName(), projectName);
				return new MEditorStatus(MEditorStatusSeverity.ERROR, message);
			}

			ListRoutineBuilder routineContent = getListRoutineBuilder(file);

			MServerRoutine serverRoutine = MServerRoutine.load(connection, file);
			String routineName = serverRoutine.getRoutineName();
			BackupSynchResult synchResult = serverRoutine.getLastSynchResult();
			if (synchResult == BackupSynchResult.UPDATED) {
				String message = Messages.bind(Messages.SERVER_BACKUP_CONFLICT, getBackupFile(file).getFullPath().toString());
				return new MEditorStatus(MEditorStatusSeverity.ERROR, message);				
			}
			
			if (serverRoutine.isLoaded() && serverRoutine.compareTo(file)) {
				MEditorStatus r = new MEditorStatus(MEditorStatusSeverity.ERROR, Messages.SERVER_CLIENT_EQUAL);				
				return r;
			}
			
			IFile backupFile = getBackupFile(file);
			StringBuilder consoleMessage = startConsoleMessage(routineName, synchResult, backupFile);			
			return saveRoutineToServer(connection, routineName, routineContent, backupFile, consoleMessage);
		} catch (Throwable t) {
			return MEditorStatus.getInstance(t);
		}
	}	
}
