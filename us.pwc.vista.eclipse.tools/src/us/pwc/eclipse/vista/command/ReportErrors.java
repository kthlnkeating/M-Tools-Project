package us.pwc.eclipse.vista.command;

import java.util.List;

import org.eclipse.core.resources.IProject;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.RoutineToolParams;
import com.pwc.us.rgi.m.tool.routine.error.ErrorTool;

public class ReportErrors extends MToolsCommand {
	private ErrorTool getTool(ParseTreeSupply pts) {
		RoutineToolParams p = new RoutineToolParams(pts);
		ErrorTool tool = new ErrorTool(p);
		return tool;
	}
	
	@Override
	protected ToolResult getResult(IProject project, ParseTreeSupply pts, List<String> selectedFileNames) {
		ErrorTool tool = this.getTool(pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(selectedFileNames);
		ToolResult result = tool.getResult(input);
		return result;
	}

	@Override
	protected ToolResult getResult(IProject project, ParseTreeSupply pts, EntryId entryId) {
		ErrorTool tool = this.getTool(pts);
		ToolResult result = tool.getResult(entryId);
		return result;
	}
}
