package us.pwc.vista.eclipse.server.core;


import java.util.ArrayList;
import java.util.List;

import us.pwc.vista.eclipse.core.resource.IRoutineBuilder;

public class ListRoutineBuilder implements IRoutineBuilder {
	private List<String> lines = new ArrayList<String>();
	private String eol;
	private boolean mixedEOLs;
	
	@Override
	public void appendLine(String line, String endOfLine) {
		if (this.eol == null) {
			this.eol = endOfLine;
		} else {
			this.mixedEOLs = (! this.eol.equals(endOfLine));
		}
		this.lines.add(line);
	}
	
	public List<String> getRoutineLines() {
		return this.lines;
	}
	
	public String getRoutine() {
		StringBuilder sb = new StringBuilder();
		for (String line : this.lines) {
			sb.append(line);
			sb.append(this.eol);
		}
		return sb.toString();
	}
	
	public String getEOL() {
		return this.eol;
	}
	
	public boolean isIdentical(String[] compareLines) {
		if (this.mixedEOLs) {
			return false;
		}
		int n = compareLines.length;
		if (n != this.lines.size()) {
			return false;
		}
		int i=0;
		for (String line : this.lines) {
			String compareLine = compareLines[i];
			if (! line.equals(compareLine)) {
				return false;
			}
			++i;
		}
		return true;
	}
}
