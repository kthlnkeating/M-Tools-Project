package gov.va.mumps.debug.core.model;

import gov.va.med.iss.mdebugger.vo.StackVO;
import gov.va.med.iss.mdebugger.vo.StepResultsVO;
import gov.va.mumps.debug.core.MDebugConstants;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;

public class MDebugTarget extends MDebugElement implements IDebugTarget {

	private ILaunch launch;
	private MDebugRpcProcess rpcDebugProcess;
	private boolean suspended;
	private MThread debugThread;
	private String name;
	
	//process stack
	private MStackFrame[] stack;
	
	public MDebugTarget(ILaunch launch, MDebugRpcProcess rpcProcess) {
		super(null);
		setDebugTarget(this);
		this.launch = launch;
		this.rpcDebugProcess = rpcProcess;
		
		debugThread = new MThread(this);		
		suspended = true; //false in tutorial, because it waits for the event to come back on the socket saying suspended
		
//		stack = new Stack<MStackFrame>(15); //the server can't handle more than 15 anyway.
		
//		eventDispatch = new EventDispatchJob();
//		eventDispatch.schedule();
		
		stack = new MStackFrame[0];
		
		handleResponse(rpcProcess.getResponseResults());
		
		//TODO: temp just for testing
		rpcProcess.addBreakPoint("STACK5+2^TSTBLAH2");
		
		fireCreationEvent(); //to register that the rpcProcess has been started, since it was started in the constructor
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this); //TODO: should this listener be removed if terminated?
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public boolean canTerminate() {
		return rpcDebugProcess.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return rpcDebugProcess.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		terminated();
	}

	@Override
	public boolean canResume() {
		//System.out.println("canResume()");
		return !isTerminated() && isSuspended();
	}

	@Override
	public boolean canSuspend() {
		//System.out.println("canSuspend()");
		return !isTerminated() && !isSuspended(); //maybe always return false since the suspend button doesn't do anything?
	}

	@Override
	public boolean isSuspended() {
		//TODO: what is this for? when and why is it called?
		//System.out.println("isSuspended()");
		return suspended;
	}

	@Override
	public void resume() throws DebugException {
		rpcDebugProcess.resume();
		suspended = false;
//		debugThread.fireResumeEvent(DebugEvent.RESUME); //TODO: what is clientRequest for?
		StepResultsVO results = rpcDebugProcess.getResponseResults();
		suspended = true;
		handleResponse(results);
		debugThread.fireResumeEvent(DebugEvent.RESUME);
	}

	@Override
	public void suspend() throws DebugException {
		//rpcProcess has no suspend command
		//suspended = true; //what would this do though?
	}

	@Override
	public void breakpointAdded(IBreakpoint arg0) {
		//TODO: call rpcProccess to add breakpoint

	}

	@Override
	public void breakpointChanged(IBreakpoint arg0, IMarkerDelta arg1) {
		//TODO: copy what PDADebugTarget does
	}

	@Override
	public void breakpointRemoved(IBreakpoint arg0, IMarkerDelta arg1) {
		//TODO: call rpcProccess to remove breakpoint
	}

	@Override
	public boolean canDisconnect() {
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
	}

	@Override
	public boolean isDisconnected() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long arg0, long arg1)
			throws DebugException {
		return null;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public String getName() throws DebugException {
		if (name == null) {
			name = "MUMPS Routine";
			try {
				name = getLaunch().getLaunchConfiguration().getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, "MUMPS Routine");
				name += " [DebugTarget]";
			} catch (CoreException e) {
			}
		}
		return name;
	}

	@Override
	public IProcess getProcess() {
		return rpcDebugProcess;
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return new IThread[] {debugThread};
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return true; //apparently a bug otherwise
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void stepOver() {
		fireResumeEvent(DebugEvent.STEP_OVER);
		rpcDebugProcess.stepOver();
		fireSuspendEvent(DebugEvent.STEP_OVER);
		suspended = true; //note that handleResponse can set this to false if it the debug is completed
		handleResponse(rpcDebugProcess.getResponseResults());
	}

	public void stepInto() {
		rpcDebugProcess.stepInto();
		handleResponse(rpcDebugProcess.getResponseResults());
		//TODO: fire event
	}

	public void stepOut() {
		rpcDebugProcess.stepOut();
		handleResponse(rpcDebugProcess.getResponseResults());
	}
	
	public MStackFrame[] getStackFrames() {
		//System.out.println("getStackFrame()");
		return stack;
	}
	
	private void handleResponse(StepResultsVO vo) {
		
		//invoke terminate event if DONE found + fireevents/setflags
		if (vo.isComplete()) {
			terminated();
			return;
		}
		
		//update cached stack
		Iterator<StackVO> stackItr = vo.getStack();
		List<StackVO> svoList = new LinkedList<StackVO>();
		while (stackItr.hasNext()) {
			 svoList.add(stackItr.next());
		}
		stack = new MStackFrame[svoList.size()];
		
		for (int i = 0; i < svoList.size(); i++) {
			StackVO svo = svoList.get(i);
			
			//TODO: actually, (1) use locationAsTag and (2) for stacks bellow the top stack, subsitute the callerName from the parent stack.
			System.out.println("adding: "+ svo.getStackName());
			if (i == svoList.size() - 1)
				stack[i] = new MStackFrame(debugThread, svo.getStackName(), svo.getCaller(), vo.getRoutineName(), vo.getLineLocation(), vo.getNextCommnd());
			else
				stack[i] = new MStackFrame(debugThread, svo.getStackName(), svo.getCaller(), null, -1, null);
		}
		
		

	}
	
	private void terminated() {
		rpcDebugProcess.terminate();
		suspended = false;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		fireTerminateEvent();
	}

}
