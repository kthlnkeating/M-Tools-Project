package us.pwc.eclipse.vista.command;

import java.util.List;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByRoutine;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.occurance.Occurance;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceTool;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceToolParams;

public class ReportOccurances extends MToolsCommand {
	private OccuranceTool getTool(ParseTreeSupply pts) {
		OccuranceToolParams p = new OccuranceToolParams(pts);	
		OccuranceTool tool = new OccuranceTool(p);
		return tool;
	}
	
	@Override
	protected ResultsByRoutine<Occurance, List<Occurance>> getResult(ParseTreeSupply pts, List<String> selectedFileNames) {
		OccuranceTool tool = this.getTool(pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(selectedFileNames);
		ResultsByRoutine<Occurance, List<Occurance>> result = tool.getResult(input);
		return result;
	}

	@Override
	protected ToolResult getResult(ParseTreeSupply pts, EntryId entryId) {
		OccuranceTool tool = this.getTool(pts);
		ToolResult result = tool.getResult(entryId);
		return result;
	}
}
