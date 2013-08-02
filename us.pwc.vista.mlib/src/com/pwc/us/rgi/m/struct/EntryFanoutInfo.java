package com.pwc.us.rgi.m.struct;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.parsetree.data.FanoutType;
import com.pwc.us.rgi.m.tool.entry.quittype.QuitTypeState;

public class EntryFanoutInfo {
	
	//private int lineNumber; //TODO: not currently set, need to enhance framework to capture this
	//private int startCmdPos;
	//private int endCmdPos;
	
	private boolean valid;
	private LineLocation lineLocation;
	private FanoutType fanoutType;
	private EntryId fanoutTo;
	private QuitTypeState returnType;
	private boolean fanoutExists;
	
	public EntryFanoutInfo(LineLocation lineLocation,
			FanoutType fanoutType, EntryId fanoutTo) {
		super();
		this.lineLocation = lineLocation;
		this.fanoutType = fanoutType;
		this.fanoutTo = fanoutTo;
	}
	
	public LineLocation getLineLocation() {
		return lineLocation;
	}

	public void setLineLocation(LineLocation lineLocation) {
		this.lineLocation = lineLocation;
	}

	public FanoutType getFanoutType() {
		return fanoutType;
	}

	public void setFanoutType(FanoutType fanoutType) {
		this.fanoutType = fanoutType;
	}

	public EntryId getFanoutTo() {
		return fanoutTo;
	}

	public void setFanoutTo(EntryId fanoutTo) {
		this.fanoutTo = fanoutTo;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public QuitTypeState getReturnType() {
		return returnType;
	}

	public void setReturnType(QuitTypeState returnType) {
		this.returnType = returnType;
	}

	public boolean isFanoutExists() {
		return fanoutExists;
	}

	public void setFanoutExists(boolean fanoutExists) {
		this.fanoutExists = fanoutExists;
	}

}
