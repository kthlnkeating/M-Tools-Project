package gov.va.med.iss.mdebugger.vo;


import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class StepResultsVO {
	
	private boolean complete;
	private LinkedHashSet<VariableVO> variables;
	//location
	private String routineName;
	private int lineLocation;
	private String locationAsTag;
	private String nextCommand;
	//stack
	private LinkedList<StackVO> stack;
	
	public StepResultsVO(boolean complete,
			LinkedHashSet<VariableVO> variables, String routineName,
			int lineLocation, String locationAsTag, String nextCommand,
			LinkedList<StackVO> stack) {
		super();
		this.complete = complete;
		this.variables = variables;
		this.routineName = routineName;
		this.lineLocation = lineLocation;
		this.locationAsTag = locationAsTag;
		this.nextCommand = nextCommand;
		this.stack = stack;
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

	public Iterator<StackVO> getStack() {
		return stack.iterator();
	}

}
