package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class MThread extends MDebugElement implements IThread { //in the future perhaps this can be used to represent a JOB on the server
	private IBreakpoint[] breakpoints;
	private String name;
	private boolean debug;

	public MThread(IMDebugTarget target) {
		super(target);
		this.debug = target.getLaunch().getLaunchMode().equals(ILaunchManager.DEBUG_MODE);
		fireCreationEvent();
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
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		getDebugTarget().resume();
	}

	@Override
	public void suspend() throws DebugException {
	}

	@Override
	public boolean canStepInto() {
		return this.canStep();
	}

	@Override
	public boolean canStepOver() {
		boolean b = this.getDebugTarget().canStepOver();
		if (b) {
			return this.canStep();
		} else {
			return false;
		}
	}

	@Override
	public boolean canStepReturn() {
		boolean b = this.getDebugTarget().canStepReturn();
		if (b) {
			return this.canStep();
		} else {
			return false;
		}
	}
	
	private boolean canStep() {
		return this.debug && (! this.isTerminated()) && (! this.isStepping());
	}

	@Override
	public boolean isStepping() {
		//return false;
		return !isSuspended();
	}

	@Override
	public void stepInto() throws DebugException {
		fireResumeEvent(DebugEvent.STEP_INTO);
		this.getDebugTarget().stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		fireResumeEvent(DebugEvent.STEP_OVER);
		this.getDebugTarget().stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		fireResumeEvent(DebugEvent.STEP_RETURN);
		this.getDebugTarget().stepReturn();
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
		fireTerminateEvent();
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
			return getDebugTarget().getStackFrames();
		} else {
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
