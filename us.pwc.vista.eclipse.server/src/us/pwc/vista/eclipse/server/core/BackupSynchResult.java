package us.pwc.vista.eclipse.server.core;

import org.eclipse.core.resources.IFile;

public class BackupSynchResult {
	private BackupSynchStatus status;
	private IFile file;
	
	public BackupSynchResult(BackupSynchStatus status) {
		this.status = status;
	}
	
	public BackupSynchResult(BackupSynchStatus status, IFile file) {
		this.status = status;
		this.file = file;
	}
	
	public BackupSynchStatus getStatus() {
		return this.status;
	}
	
	public IFile getFile() {
		return this.file;
	}
}
