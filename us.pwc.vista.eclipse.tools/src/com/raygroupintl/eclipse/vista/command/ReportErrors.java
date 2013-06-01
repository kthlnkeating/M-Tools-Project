package com.raygroupintl.eclipse.vista.command;

import java.util.List;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.tool.ParseTreeSupply;
import com.raygroupintl.m.tool.ToolResult;
import com.raygroupintl.m.tool.routine.MRoutineToolInput;
import com.raygroupintl.m.tool.routine.RoutineToolParams;
import com.raygroupintl.m.tool.routine.error.ErrorTool;

public class ReportErrors extends MToolsCommand {
	private ErrorTool getTool(ParseTreeSupply pts) {
		RoutineToolParams p = new RoutineToolParams(pts);
		ErrorTool tool = new ErrorTool(p);
		return tool;
	}
	
	@Override
	protected ToolResult getResult(ParseTreeSupply pts, List<String> selectedFileNames) {
		ErrorTool tool = this.getTool(pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(selectedFileNames);
		ToolResult result = tool.getResult(input);
		return result;
	}

	@Override
	protected ToolResult getResult(ParseTreeSupply pts, EntryId entryId) {
		ErrorTool tool = this.getTool(pts);
		ToolResult result = tool.getResult(entryId);
		return result;
	}
}
