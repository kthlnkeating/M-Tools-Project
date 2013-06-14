package us.pwc.eclipse.vista.command;

import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariables;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesTool;
import com.pwc.us.rgi.m.tool.entry.assumedvariables.AssumedVariablesToolParams;

public class ReportAssumeVariables extends MToolsCommand {
	@Override
	protected OutputFlags getOutputFlags() {
		OutputFlags fs = new OutputFlags();
		fs.setSkipEmpty(true);
		fs.setShowDetail(true);
		return fs;
	}
	
	private AssumedVariablesTool getTool(ParseTreeSupply pts) {
		AssumedVariablesToolParams params = new AssumedVariablesToolParams(pts);
		params.addExpected("U");
		params.addExpected("DT");
		params.addExpected("DUZ");
		params.getRecursionSpecification().setDepth(RecursionDepth.LABEL);
		return new AssumedVariablesTool(params);		
	}
	
	@Override
	protected MEntryToolResult<AssumedVariables> getResult(ParseTreeSupply pts, List<String> selectedFileNames) {
		AssumedVariablesTool tool = this.getTool(pts);
		MEntryToolResult<AssumedVariables> result = tool.getResultForRoutines(selectedFileNames);
		return result;
	}

	@Override
	protected AssumedVariables getResult(ParseTreeSupply pts, EntryId entryId) {
		AssumedVariablesTool tool = this.getTool(pts);
		AssumedVariables result = tool.getResult(entryId);
		return result;
	}
}
