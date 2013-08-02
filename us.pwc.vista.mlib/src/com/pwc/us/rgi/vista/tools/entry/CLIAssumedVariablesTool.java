package com.pwc.us.rgi.vista.tools.entry;

import com.pwc.us.rgi.m.tool.entry.MEntryToolInput;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariables;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesTool;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;
import com.pwc.us.rgi.vista.tools.CLIParams;

public class CLIAssumedVariablesTool extends CLIEntryTool<AssumedVariables> {	
	public CLIAssumedVariablesTool(CLIParams params) {
		super(params);
	}
	
	@Override
	protected MEntryToolResult<AssumedVariables> getResult(MEntryToolInput input) {
		AssumedVariablesToolParams params = CLIETParamsAdapter.toAssumedVariablesToolParams(this.params);
		AssumedVariablesTool a = new AssumedVariablesTool(params);
		return a.getResult(input);			
	}
}
