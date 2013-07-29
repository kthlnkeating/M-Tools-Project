package org.mumps.meditor;

import gov.va.med.iss.meditor.command.utils.MServerRoutine;

public class LoadRoutineResult {
	private String routineName;
	private MServerRoutine content;
	private BackupSynchResult synchResult;
	
	public LoadRoutineResult(String routineName, MServerRoutine content, BackupSynchResult synchResult) {
		super();
		this.routineName = routineName;
		this.content = content;
		this.synchResult = synchResult;
	}
	
	public String getRoutineName() {
		return this.routineName;
	}

	public MServerRoutine getContent() {
		return content;
	}
	
	public BackupSynchResult getSynchResult() {
		return synchResult;
	}
}
