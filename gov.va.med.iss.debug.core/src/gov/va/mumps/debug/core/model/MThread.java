package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.examples.core.pda.model.PDADebugTarget;

public class MThread extends MDebugElement implements IThread {

	private IBreakpoint[] breakpoints;
	private boolean stepping = false; //TODO: move to constructor?
	private String name;
	
	public MThread(MDebugTarget target) {
		super(target);
	}

	@Override
	public boolean canResume() {
		return getDebugTarget().canResume();
	}

	@Override
	public boolean canSuspend() {
		return getDebugTarget().canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return getDebugTarget().isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		getDebugTarget().resume();
	}

	@Override
	public void suspend() throws DebugException {
		getDebugTarget().suspend();
	}

	@Override
	public boolean canStepInto() {
		return true; //TODO: this will take some lexical analysis of the nextCommand
	}

	@Override
	public boolean canStepOver() {
		return true; //I think any command can be stepped over
	}

	@Override
	public boolean canStepReturn() {
		return true; //TODO: check if there is anything on the stack?
	}

	@Override
	public boolean isStepping() {
		//return false;
		return isSuspended();
	}

	@Override
	public void stepInto() throws DebugException {
		((MDebugTarget)getDebugTarget()).stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		((MDebugTarget)getDebugTarget()).stepOver();

	}

	@Override
	public void stepReturn() throws DebugException {
		((MDebugTarget)getDebugTarget()).stepOut();

	}

	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}

	@Override
	public boolean isTerminated() {
		return getDebugTarget().isTerminated(); //1 debug target per "thread"
	}

	@Override
	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		if (breakpoints == null) {
			return new IBreakpoint[0];
		}
		return breakpoints;
	}
	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code>
	 * if none.
	 * 
	 * @param breakpoints the breakpoints this thread is suspended at, or <code>null</code>
	 * if none
	 */
	protected void setBreakpoints(IBreakpoint[] breakpoints) {
		this.breakpoints = breakpoints;
	}

	@Override
	public String getName() throws DebugException {
		if (name == null) {
			name = "MUMPS Routine";
			try {
				name = getLaunch().getLaunchConfiguration().getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, "MUMPS Routine");
				name += " [Thread]";
			} catch (CoreException e) {
			}
		}
		return name;
	}

	@Override
	public int getPriority() throws DebugException {
		return 0;
	}

	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			return ((MDebugTarget)getDebugTarget()).getStackFrames();
		} else {
			System.out.println("returning dummy stackframes");
			return new IStackFrame[0];
		}
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0) {
			return frames[0];
		}
		return null;
	}

	@Override
	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}

}
