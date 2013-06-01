package us.pwc.eclipse.vista.command;

import java.util.List;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.tool.CommonToolParams;
import com.raygroupintl.m.tool.ParseTreeSupply;
import com.raygroupintl.m.tool.entry.MEntryToolResult;
import com.raygroupintl.m.tool.entry.RecursionDepth;
import com.raygroupintl.m.tool.entry.quittype.QuitType;
import com.raygroupintl.m.tool.entry.quittype.QuitTypeTool;

public class ValidateQuitTypes extends MToolsCommand {
	private QuitTypeTool getTool(ParseTreeSupply pts) {
		CommonToolParams params = new CommonToolParams(pts);
		params.getRecursionSpecification().setDepth(RecursionDepth.ALL);
		return new QuitTypeTool(params);
	}
	
	@Override
	protected MEntryToolResult<QuitType> getResult(ParseTreeSupply pts, List<String> selectedFileNames) {
		QuitTypeTool tool = this.getTool(pts);
		MEntryToolResult<QuitType> result = tool.getResultForRoutines(selectedFileNames);
		return result;
	}

	@Override
	protected QuitType getResult(ParseTreeSupply pts, EntryId entryId) {
		QuitTypeTool tool = this.getTool(pts);
		QuitType result = tool.getResult(entryId);
		return result;
	}
}
