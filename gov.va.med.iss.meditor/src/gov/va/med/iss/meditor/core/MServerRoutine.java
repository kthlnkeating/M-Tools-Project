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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

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
	private BackupSynchResult lastSynchResult;
	
	public MServerRoutine(String routineName, String content, IFile clientFileHandle, BackupSynchResult synchBackupResult) {
		this.routineName = routineName;
		this.content = content;
		this.clientFileHandle = clientFileHandle;
		this.lastSynchResult = synchBackupResult;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public IFile getFileHandle() {
		return this.clientFileHandle;
	}
	
	public BackupSynchResult getLastSynchResult() {
		return this.lastSynchResult;
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
	
	private static BackupSynchResult synchBackupFile(IFile file, String content) throws BackupSynchException {
		try {
			IFile backupFile = SaveRoutineEngine.getBackupFile(file);
			if (backupFile.exists()) {
				if (content != null) {
					boolean updated = updateFile(backupFile, content);
					return updated ? BackupSynchResult.UPDATED : BackupSynchResult.NO_CHANGE_IDENTICAL;
				} else {
					return BackupSynchResult.NO_CHANGE_SERVER_DELETED;
				}
			} else {
				if (content != null) {
					ResourceUtilsExtension.prepareFolders((IFolder) backupFile.getParent());			
					ResourceUtilsExtension.updateFile(backupFile, content);
					return BackupSynchResult.INITIATED;
				} else {
					return BackupSynchResult.NO_CHANGE_BOTH_ABSENT;
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
