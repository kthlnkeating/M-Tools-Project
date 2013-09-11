package gov.va.mumps.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

//This class is mutable for variables to be synced, since they can be scoped back to multiple parent stacks.
//However for everything else, a stack is created whenever a debug result comes back from the server and
//it is not updated.


/*
 * Update: this will not work. It would only work with stepping, because it would garauntee
 * that the previous stacks are the same. However with breakpoints, the stacks in positions
 * 1 and 2 could be anything by the time it resumes. My only option then really is to
 * just recreate the stack entirely.
 */

/*
 * Update2: with regards to the first update, it won't be able to remember what stack is which,
 * so previous stacks will not have the correct line numbers left in them, since they could be
 * changed by now, even if they have the same name.
 * 
 */

/*
 * 1: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 2: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 
 * result, Stack1 is created, and replaces that last Stack1 (1 deleted)
 * 
 * after:
 * 
 * 1: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 2: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 3: Stack2(lineLocation, cmdCountOnLine, nextCommand)
 * 
 * result, Stack2 is created, and placed ontop of (1:) Stack1 (nothing deleted)
 * 
 * after:
 * 
 * 1: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 2: Stack1(lineLocation, cmdCountOnLine, nextCommand)
 * 
 * result, Stack1 is created, and placed on top of (1:) Stack1 (2 stacks deleted)
 * 
 * note: 1 stack is always created. This is much better than trying to keep 
 * the original stacks, remap them and resync them. HOWEVER, variables need to
 * be synced.
 */

/*
 * About variables. The debugger returns all variables back. 
 * 
 * In order to map variables to a stack, it may be possible (update: its not, not currently)
 * to separate variables by taking all the first variables that come in from 
 * START, place them in a separate custom view. Any new variables can be added
 * to a stack by diffing all the currently defined variables. But new variables
 * would also have to be given to all children stacks.
 * 
 * Deleted variables would simply not be returned on the current stack, BUT
 * they would have to be deleted if they were placed on a parent stack or
 * if they are in the custom view.
 * 
 * Changed variables are similar to deleted variables, the value must be changed
 * across all parent stacks where it is scoped.
 * 
 */

public class MStackFrame extends MDebugElement implements IStackFrame {

	private IThread debugThread;
	private String stackName;
	@SuppressWarnings("unused")
	private String callerTag;
	private String routineName;
	private int lineLocation;
	@SuppressWarnings("unused")
	private String nextCommand;
	
	public MStackFrame(IThread debugThread, String stackName, String callerTag, 
			String routineName, int lineLocation, String nextCommand) {
		super((IMDebugTarget) debugThread.getDebugTarget());
		
		this.debugThread = debugThread;
		this.stackName = stackName;
		this.callerTag = callerTag;
		this.routineName = routineName;
		this.lineLocation = lineLocation;
		this.nextCommand = nextCommand;
	}

	/* TODO it may be better to implement logic to determine if this stack is the top stack.
	 * That way it can return false for stepInto, since Java may be robust
	 * enough to do specific stack stepping
	 */
	
	@Override
	public boolean canStepInto() { //TODO: disable all of these for these for children stacks
		return getThread().canStepInto();
	}

	@Override
	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	@Override
	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	@Override
	public boolean isStepping() {
		return getThread().isStepping();
	}

	@Override
	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	@Override
	public boolean canResume() {
		return getThread().canResume();
	}

	@Override
	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return getThread().isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		getThread().resume();
	}

	@Override
	public void suspend() throws DebugException {
	}

	@Override
	public boolean canTerminate() {
		return getThread().canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return getThread().isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		getThread().terminate();
	}

	@Override
	public int getCharEnd() throws DebugException {
		return -1; 
	}

	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}

	@Override
	public int getLineNumber() throws DebugException {
		//return (int)(25 * Math.random());
		return lineLocation;
	}

	@Override
	public String getName() throws DebugException {
		return stackName;
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}
	
	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	@Override
	public IThread getThread() {
		return debugThread;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return ((IMDebugTarget)debugThread.getDebugTarget()).getVariables();
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return ((IMDebugTarget)debugThread.getDebugTarget()).getVariables().length != 0;
	}

	public String getRoutineName() {
		return routineName;
	}

}
