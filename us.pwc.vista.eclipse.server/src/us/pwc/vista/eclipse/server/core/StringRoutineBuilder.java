package us.pwc.vista.eclipse.server.core;

import us.pwc.vista.eclipse.server.resource.IRoutineBuilder;


public class StringRoutineBuilder implements IRoutineBuilder {
	private StringBuilder builder = new StringBuilder();
	
	@Override
	public void appendLine(String line, String endOfLine) {
		this.builder.append(line);
		this.builder.append(endOfLine);
	}
	
	public String getRoutine() {
		return this.builder.toString();
	}
	
	public static StringRoutineBuilder getInstance(String[] lines, String eol) {
		StringRoutineBuilder result = new StringRoutineBuilder();
		for (int i=0; i<lines.length; ++i) {
			result.appendLine(lines[i], eol);
		}		
		return result;
	}
}
