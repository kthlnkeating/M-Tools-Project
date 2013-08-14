//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package gov.va.med.iss.meditor.core;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.foundations.utilities.FoundationsException;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.error.BackupSynchException;
import gov.va.med.iss.meditor.error.InvalidFileException;
import gov.va.med.iss.meditor.error.LoadRoutineException;
import gov.va.med.iss.meditor.resource.ResourceUtilsExtension;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import us.pwc.vista.eclipse.core.VistACorePrefs;

/**
 * This class represents an M routine that is loaded from server.  Due to 
 * details in the loading process <code>MServerRoutine</code> cannot have an 
 * empty line. It is assumed that each line ends with an end of line character
 * and that end of line character is the one Eclipse configured to use when 
 * creating a new file. 
 */
public class MServerRoutine {
	private static final String EOL = "\n";
	
	private String routineName;
	private String content;
	private IFile clientFileHandle;
	private BackupSynchResult synchResult;
	
	private MServerRoutine(String routineName, String content, IFile clientFileHandle, BackupSynchResult synchBackupResult) {
		this.routineName = routineName;
		this.content = content;
		this.clientFileHandle = clientFileHandle;
		this.synchResult = synchBackupResult;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public IFile getFileHandle() {
		return this.clientFileHandle;
	}
	
	public BackupSynchResult getSynchResult() {
		return this.synchResult;
	}
	
	public String getRoutineName() {
		return this.routineName;
	}
	
	public boolean isLoaded() {
		return this.content != null;
	}
	
	private static void copyTo(IFile file, String content) throws CoreException, UnsupportedEncodingException {
		IProject project = file.getProject();
		String eolToBeUsed = ResourceUtilsExtension.getLineSeperator(project);
		if (! eolToBeUsed.equals(EOL)) {
			content = content.replaceAll(EOL, eolToBeUsed);
		}
		ResourceUtilsExtension.updateFile(file, content);						
	}
	
	public void copyTo(IFile file) throws CoreException, UnsupportedEncodingException {
		copyTo(file, this.content);
	}

	public boolean compareTo(IFile file) throws CoreException, BadLocationException {
		return compareTo(file, this.content);		
	}
	
	public static boolean compareTo(IFile file, String content) throws CoreException, BadLocationException {
		IDocument fileDocument = ResourceUtilsExtension.getDocument(file);
		StringTokenizer tokenizer = new StringTokenizer(content, EOL);
		int n = fileDocument.getNumberOfLines();
		int fileLineIndex = 0;
		String requiredEol = ResourceUtilsExtension.getLineSeperator(file.getProject());
		while (tokenizer.hasMoreTokens()) {
			if (fileLineIndex == n) {
				return false;
			}
			if (! requiredEol.equals(fileDocument.getLineDelimiter(fileLineIndex))) {
				return false;
			}			
			IRegion lineInfo = fileDocument.getLineInformation(fileLineIndex);
			int offset = lineInfo.getOffset();
			int length = lineInfo.getLength();
			String fileLine = fileDocument.get(offset, length);
			String contentLine = tokenizer.nextToken();
			if (! fileLine.equals(contentLine)) {
				return false;
			}
			++fileLineIndex;
		}
		for (int i=fileLineIndex; i<n; ++i) {
			if (fileDocument.getLineLength(i) > 0) {
				return false;
			}
		}
		return true;
	}
	
	public UpdateFileResult updateClient() throws CoreException, BadLocationException, UnsupportedEncodingException {
		if (this.clientFileHandle.exists()) {
			if (this.compareTo(this.clientFileHandle)) {
				return UpdateFileResult.IDENTICAL;
			} else {
				this.copyTo(this.clientFileHandle);
				return UpdateFileResult.UPDATED;
			}
		} else {
			this.copyTo(this.clientFileHandle);
			return UpdateFileResult.CREATED;
		}
	}
	
	public static boolean updateFile(IFile file, String content) throws CoreException, BadLocationException, UnsupportedEncodingException {
		if (compareTo(file, content)) {
			return false;
		} else {
			copyTo(file, content);
			return true;
		}
	}

	private static String load(VistaLinkConnection connection, String routineName) throws LoadRoutineException {
		try {
			RpcRequest vReq = RpcRequestFactory.getRpcRequest("", "XT ECLIPSE M EDITOR");
			vReq.setUseProprietaryMessageFormat(false);
			vReq.getParams().setParam(1, "string", "RL"); // RD RL GD GL RS
			vReq.getParams().setParam(2, "string", "notused");
			vReq.getParams().setParam(3, "string", routineName);
			RpcResponse vResp = connection.executeRPC(vReq);
			String result = vResp.getResults();
			if (result.startsWith("-1^Error Processing load request")) {
				return null;
			} else {
				return result.substring(result.indexOf('\n')+1);
			}
		} catch (FoundationsException e) {
			String message = Messages.bind(Messages.UNABLE_RTN_LOAD, routineName, e.getMessage());
			throw new LoadRoutineException(message, e);
		}
	}
	
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
	 * @param backupFolderName
	 * @param file
	 * @return handle to the backup file.                                                              
	 */	
	public static IFile getBackupFile(IFile file) throws CoreException {
		String backupFolderName = VistACorePrefs.getServerBackupDirectory(file.getProject());
		if (backupFolderName.isEmpty()) return null;
		IProject project = file.getProject();
		IPath projectPath = project.getFullPath();
		IPath filePath = file.getFullPath();
		filePath = filePath.removeFileExtension().addFileExtension("mbk");		
		IPath relativeFilePath = filePath.makeRelativeTo(projectPath);
		Path relativeBackupFolderPath = new Path(backupFolderName);
		IPath relativeBackupFilePath = relativeBackupFolderPath.append(relativeFilePath);
		IFile backupFile = project.getFile(relativeBackupFilePath);
		return backupFile;
	}

	private static BackupSynchResult synchBackupFile(IFile file, String content) throws BackupSynchException {
		try {
			IFile backupFile = getBackupFile(file);
			if (backupFile == null) {
				return new BackupSynchResult(BackupSynchStatus.OFF_BY_CONFIGURATION);
			}
			if (backupFile.exists()) {
				if (content != null) {
					boolean updated = updateFile(backupFile, content);
					BackupSynchStatus status = updated ? BackupSynchStatus.UPDATED : BackupSynchStatus.NO_CHANGE_IDENTICAL;
					return new BackupSynchResult(status, backupFile);
				} else {
					return new BackupSynchResult(BackupSynchStatus.NO_CHANGE_SERVER_DELETED, backupFile);
				}
			} else {
				if (content != null) {
					ResourceUtilsExtension.prepareFolders((IFolder) backupFile.getParent());			
					ResourceUtilsExtension.updateFile(backupFile, content);
					return new BackupSynchResult(BackupSynchStatus.INITIATED, backupFile);
				} else {
					return new BackupSynchResult(BackupSynchStatus.NO_CHANGE_BOTH_ABSENT, backupFile);
				}
			}
		} catch (CoreException t) {
			throw new BackupSynchException(t);
		}catch (UnsupportedEncodingException t) {
			throw new BackupSynchException(t);
		}catch (BadLocationException t) {
			throw new BackupSynchException(t);
		}
	}
	
	private static MServerRoutine load(VistaLinkConnection connection, IFile file, String routineName) throws LoadRoutineException, BackupSynchException {
		String content = load(connection, routineName);
		BackupSynchResult synchBackupResult = synchBackupFile(file, content);
		MServerRoutine result = new MServerRoutine(routineName, content, file, synchBackupResult);
		return result;
	}	

	public static MServerRoutine load(VistaLinkConnection connection, IFile file) throws InvalidFileException, LoadRoutineException, BackupSynchException {
		String routineName = SaveRoutineEngine.getRoutineName(file);
		return load(connection, file, routineName);
	}
}
