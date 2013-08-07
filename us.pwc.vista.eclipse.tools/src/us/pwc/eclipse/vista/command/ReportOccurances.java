package us.pwc.eclipse.vista.command;

import java.util.List;

import org.eclipse.core.resources.IProject;

import us.pwc.eclipse.vista.propertyPage.OccuranceTypesHandler;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.ResultsByRoutine;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.m.tool.routine.MRoutineToolInput;
import com.pwc.us.rgi.m.tool.routine.occurance.Occurance;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceTool;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceToolParams;
import com.pwc.us.rgi.m.tool.routine.occurance.OccuranceType;

public abstract class ReportOccurances extends MToolsCommand {
	private OccuranceTool getTool(IProject project, ParseTreeSupply pts) {
		OccuranceToolParams p = new OccuranceToolParams(pts);
		String[] types = OccuranceTypesHandler.getTypesFromStore(project);
		if (types != null) {
			p.clearTypes();
			for (String rawType : types) {
				String type = rawType.toUpperCase();
				type = type.replace(' ', '_');
				OccuranceType ot = OccuranceType.valueOf(type);
				p.addType(ot);
			}
		}		
		OccuranceTool tool = new OccuranceTool(p);
		return tool;
	}
	
	@Override
	public ResultsByRoutine<Occurance, List<Occurance>> getResult(IProject project, ParseTreeSupply pts, List<String> selectedFileNames) {
		OccuranceTool tool = this.getTool(project, pts);
		MRoutineToolInput input = new MRoutineToolInput();
		input.addRoutines(selectedFileNames);
		ResultsByRoutine<Occurance, List<Occurance>> result = tool.getResult(input);
		return result;
	}

	@Override
	public ToolResult getResult(IProject project, ParseTreeSupply pts, EntryId entryId) {
		OccuranceTool tool = this.getTool(project, pts);
		ToolResult result = tool.getResult(entryId);
		return result;
	}
}
