package gov.va.mumps.debug.xtdebug.vo;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class StepResultsVO {
	
	private ResultReasonType resultReason;
	private boolean complete;
	private LinkedHashSet<VariableVO> variables;
	//location
	private String routineName; //TODO: in order to tidy up, consider consolidating these into a LocationVO
	private int lineLocation;
	private String locationAsTag;
	private String nextCommand;
	private String lastCommand;
	//stack
	private LinkedList<StackVO> stack;
	//write
	private String writeLine;
	//watch variables
	private LinkedList<WatchVO> watchedVars;
	//read
	private ReadResultsVO readresults;
	
	public StepResultsVO(ResultReasonType resultReason, boolean complete,
			LinkedHashSet<VariableVO> variables, String routineName,
			int lineLocation, String locationAsTag, String nextCommand,
			String lastCommand, LinkedList<StackVO> stack, String writeLine, LinkedList<WatchVO> watchedVars, ReadResultsVO readResults) {
		super();
		this.resultReason = resultReason;
		this.complete = complete;
		this.variables = variables;
		this.routineName = routineName;
		this.lineLocation = lineLocation;
		this.locationAsTag = locationAsTag;
		this.nextCommand = nextCommand;
		this.lastCommand = lastCommand;
		this.stack = stack;
		this.writeLine = writeLine;
		this.watchedVars = watchedVars;
		this.readresults = readResults;
	}
	
	public ResultReasonType getResultReason() {
		return resultReason;
	}

	public boolean isComplete() {
		return complete;
	}

	public Iterator<VariableVO> getVariables() {
		return variables.iterator();
	}

	public String getRoutineName() {
		return routineName;
	}

	public int getLineLocation() {
		return lineLocation;
	}
	
	public String getLocationAsTag() {
		return locationAsTag;
	}
	
	public String getNextCommnd() {
		return nextCommand;
	}
	
	public String getLastCommand() {
		return lastCommand;
	}

	public Iterator<StackVO> getStack() {
		return stack.iterator();
	}
	
	public String getWriteLine() {
		return writeLine;
	}
	
	public Iterator<WatchVO> getWatchedVars() {
		return watchedVars.iterator();
	}
	
	public ReadResultsVO getReadResults() {
		return readresults;
	}
	
	public enum ResultReasonType {
		START, STEP, BREAKPOINT, WATCHPOINT, WRITE, READ;
	}

}
