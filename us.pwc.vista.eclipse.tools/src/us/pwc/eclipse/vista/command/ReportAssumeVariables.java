package us.pwc.eclipse.vista.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import us.pwc.eclipse.vista.propertyPage.ExpectedAssumedVariablesHandler;
import us.pwc.eclipse.vista.propertyPage.NameFilter;
import us.pwc.eclipse.vista.propertyPage.NameFilterType;
import us.pwc.eclipse.vista.propertyPage.RecursionSpecificationHandler;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.NamespaceFilter;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.entry.MEntryToolResult;
import com.pwc.us.rgi.m.tool.entry.RecursionDepth;
import com.pwc.us.rgi.m.tool.entry.RecursionSpecification;
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
	
	private RecursionDepth getRecursionDepth(IProject project) {
		int rd = RecursionSpecificationHandler.getRecursionDepthFromStore(project, RecursionSpecificationHandler.AV_PREFIX);
		switch (rd) {
		case 1:
			return RecursionDepth.ENTRY;
		case 2:
			return RecursionDepth.ROUTINE;
		case 3:
			return RecursionDepth.ALL;
		default:
			return RecursionDepth.LABEL;
		}
	}
	
	private NamespaceFilter getNameSpaceFilter(IProject project) {
		List<NameFilter> filters = RecursionSpecificationHandler.getFiltersFromStore(project, RecursionSpecificationHandler.AV_PREFIX);
		List<String> included = new ArrayList<String>();
		List<String> excluded = new ArrayList<String>();
		List<String> exception = new ArrayList<String>();
		if (filters != null) for (NameFilter filter : filters) {
			NameFilterType type = filter.getType();
			String value = filter.getValue();
			switch (type) {
			case INCLUDE:
				included.add(value);
				break;
			case EXCLUDE:
				included.add(value);
				break;
			case EXCEPTION:
				exception.add(value);
				break;
			}
		}
		NamespaceFilter result = new NamespaceFilter();
		result.addIncludedNamespaces(included);
		result.addExcludedNamespaces(excluded);
		result.addExcludedExceptionNamespaces(exception);
		return result;
	}
	
	private AssumedVariablesTool getTool(IProject project, ParseTreeSupply pts) {
		AssumedVariablesToolParams params = new AssumedVariablesToolParams(pts);
		String[] expectedVars = ExpectedAssumedVariablesHandler.getVarsFromStore(project);
		for (String var : expectedVars) {
			params.addExpected(var);
		}
		RecursionDepth rd = this.getRecursionDepth(project);
		RecursionSpecification spec = new RecursionSpecification();
		spec.setDepth(rd);
		if (rd == RecursionDepth.ALL) {
			NamespaceFilter nsFilter = this.getNameSpaceFilter(project);
			spec.setNamespaceFilter(nsFilter);
		}
		params.setRecursionSpecification(spec);		
		return new AssumedVariablesTool(params);		
	}
	
	@Override
	protected MEntryToolResult<AssumedVariables> getResult(IProject project, ParseTreeSupply pts, List<String> selectedFileNames) {
		AssumedVariablesTool tool = this.getTool(project, pts);
		MEntryToolResult<AssumedVariables> result = tool.getResultForRoutines(selectedFileNames);
		return result;
	}

	@Override
	protected AssumedVariables getResult(IProject project, ParseTreeSupply pts, EntryId entryId) {
		AssumedVariablesTool tool = this.getTool(project, pts);
		AssumedVariables result = tool.getResult(entryId);
		return result;
	}
}
