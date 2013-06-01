package us.pwc.eclipse.vista.command;

import java.util.List;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.tool.ParseTreeSupply;
import com.raygroupintl.m.tool.ResultsByRoutine;
import com.raygroupintl.m.tool.ToolResult;
import com.raygroupintl.m.tool.routine.MRoutineToolInput;
import com.raygroupintl.m.tool.routine.occurance.Occurance;
import com.raygroupintl.m.tool.routine.occurance.OccuranceTool;
import com.raygroupintl.m.tool.routine.occurance.OccuranceToolParams;

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
