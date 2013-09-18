package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.IMInterpreter;
import gov.va.mumps.debug.core.IMInterpreterConsumer;
import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.xtdebug.vo.VariableVO;
import gov.va.mumps.launching.InputReadyListener;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import us.pwc.vista.eclipse.core.resource.FileSearchVisitor;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class MCacheTelnetDebugTarget extends MDebugElement implements IMDebugTarget, InputReadyListener, IMInterpreterConsumer {
	private static String BREAK_IDENTIFIER = "<BREAK>";

	//private static enum TargetState {
	//	NOT_CONNECTED,
	//	INITIALIZING,
	//	ENDING_BREAKPOINT,
	//	DEBUGGING,
	//	IN_BREAK,
	//	IN_BREAK_STACK_VARS,
	//	IN_BREAK_SUSPEND;
	//}
	
	
	private ILaunch launch;
	private MCacheTelnetProcess rpcDebugProcess;
	private boolean suspended;
	private MThread debugThread;
	private String name;
	
	//All the currently defined variables
	private List<VariableVO> allVariables;
	//Only variables created during the debug process
	private MVariable[] variables;
	
	//process stack
	private MStackFrame[] stack;
	
	//mode
	private boolean debug;
	
	private IMInterpreter interpreter;
	
	private IProject project;
	private String mcode;
	
	public MCacheTelnetDebugTarget(IProject project, String mcode, ILaunch launch, MCacheTelnetProcess rpcProcess) {
		super(null);
		this.project = project;
		this.mcode = mcode;
		setDebugTarget(this);
		this.launch = launch;
		this.debug = launch.getLaunchMode().equals(ILaunchManager.DEBUG_MODE);
		this.rpcDebugProcess = rpcProcess;
		
		debugThread = new MThread(this);		
		suspended = true;		
		stack = new MStackFrame[0];
		
		//this.setupBreakPoints();
		
		fireCreationEvent();
	}
	
	/**
	 * Removes all active breakpoints currently registered with the breakpoint
	 * manager.
	 */
	private void uninstallActiveBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MDebugConstants.M_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointRemoved(breakpoints[i], null);
		}
	}

	@Override
	public IMDebugTarget getDebugTarget() {
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
		return !isTerminated() && isSuspended();
	}

	@Override
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}

	@Override
	public boolean isSuspended() {
		return suspended;
	}

	@Override
	public void resume() {
		debugThread.fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		rpcDebugProcess.resume();
		suspended = false;
		this.interpreter.resume();
	}

	@Override
	public void suspend() {
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (!supportsBreakpoint(breakpoint) || isTerminated())
			return;
		
		try {
			if (!breakpoint.isEnabled())
				return;

			if (breakpoint instanceof AbstractMBreakpoint)
				rpcDebugProcess
						.addBreakPoint(((AbstractMBreakpoint) breakpoint)
								.getBreakpointAsTag());
			else if (breakpoint instanceof MWatchpoint)
				rpcDebugProcess.addWatchPoint(((MWatchpoint) breakpoint)
						.getWatchpointVariable());
		} catch (CoreException e) {
		}
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta marker) {
		if (!supportsBreakpoint(breakpoint) || isTerminated())
			return;
		
		try {
			// this code is from the custom debugger tutorial. it assumes that
			// breakpoints only property being changed is the enable/disable
			// toggle. Which is not quite true, what if a line number changes?
			// if so, use the IMarkerDelta parm to compute this change and
			// remove the old value from the RPC api... although I think that
			// would only be benefecial for editing a file while debugging it
			// at the same time, which I am not a big fan of supporting.
			if (breakpoint.isEnabled()) {
				breakpointAdded(breakpoint);
			} else {
				breakpointRemoved(breakpoint, null);
			}
		} catch (CoreException e) {
		}
		
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta marker) {
		if (!supportsBreakpoint(breakpoint) || isTerminated())
			return;
		
		if (breakpoint instanceof AbstractMBreakpoint)
			rpcDebugProcess.removeBreakPoint(((AbstractMBreakpoint)breakpoint).getBreakpointAsTag());
		else if (breakpoint instanceof MWatchpoint)
			rpcDebugProcess.removeWatchPoint(((MWatchpoint)breakpoint).getWatchpointVariable());
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
			name = "MUMPS Code";
			try {
				name = getLaunch().getLaunchConfiguration().getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, "MUMPS Code");
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
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(MDebugConstants.M_DEBUG_MODEL) &&
				(breakpoint instanceof AbstractMBreakpoint || breakpoint instanceof MWatchpoint)) {
			return true;
			//the previous example only supports breakpoints on a per file basis.
			//many languages, if not all common languages, will allow you to jump
			//from one code file to another. this debug target should support all
			//M_DEBUG_MODEL breakpoints in the workspace.
//			try {
//				String program = getLaunch().getLaunchConfiguration().getAttribute(MDebugConstants.ATTR_PDA_PROGRAM, (String)null);
//				if (program != null) {
//					IMarker marker = breakpoint.getMarker();
//					if (marker != null) {
//						IPath p = new Path(program);
//						return marker.getResource().getFullPath().equals(p);
//					}
//				}
//			} catch (CoreException e) {
//			}			
		}
		return false;
	}

	@Override
	public void stepOver() {
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_OVER);
		rpcDebugProcess.stepOver();
		this.interpreter.stepOver();		
	}

	@Override
	public void stepInto() {
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_INTO);
		rpcDebugProcess.stepInto();
		this.interpreter.stepInto();		
	}

	public void stepOut() {
		suspended = false;
		fireResumeEvent(DebugEvent.STEP_RETURN);
		rpcDebugProcess.stepOut();
		this.interpreter.stepReturn();		
	}
	
	@Override
	public MStackFrame[] getStackFrames() {
		return stack;
	}
	
	@Override
	public MVariable[] getVariables() {
		return variables;
	}
	
	public List<VariableVO> getAllVariables() {
		return allVariables;
	}
	
	@Override
	public MDebugPreference getPreferenceImplemented() {
		return MDebugPreference.CACHE_TELNET;
	}
	
	/**
	 * Notification a breakpoint was encountered. Determine which breakpoint was
	 * hit and fire a suspend event.
	 * 
	 * @param breakpointName
	 *            name of the breakpoint, either a tag or a variable name for watch
	 *            points
	 */
	@SuppressWarnings("unused")
	private void breakpointHit(String breakpointName) {
		// determine which breakpoint was hit, and set the thread's breakpoint
		
		if (breakpointName == null)
			return; //guard against bad data from the backend api

		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MDebugConstants.M_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint)) {
				if (breakpoint instanceof AbstractMBreakpoint) {
					AbstractMBreakpoint Mbreakpoint = (AbstractMBreakpoint) breakpoint;
					if (breakpoint.equals(Mbreakpoint.getBreakpointAsTag()))
						debugThread.setBreakpoints(new IBreakpoint[]{breakpoint});
						break;
				} else if (breakpoint instanceof MWatchpoint) {
					
				}
			}
		}

		suspended(DebugEvent.BREAKPOINT);
	}
		
	private void suspended(int detail) {
		suspended = true;
		debugThread.fireSuspendEvent(detail);
	}
	
	private void terminated() {
		if (this.debug) {
			uninstallActiveBreakpoints();
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		}
		rpcDebugProcess.terminate();
		suspended = false;
		fireTerminateEvent(); //this fires the event to indicate that the debugtarget has terminated.
	}

	@Override
	public void handleInput(final String input) {
		if (isTerminated())
			return;
		
		//input must be handled because it causes the program to continue (resume) running, and it completes the read command which may update variable values
		rpcDebugProcess.sendReadInput(input);

	}
	
	@Override
	public void handleConnected(IMInterpreter interpreter) {
		this.interpreter = interpreter;
		String command = this.getInitialBreakPointCommand();
		this.interpreter.sendInfoCommand(command + "\n");
	}
	
	@Override 
	public void handleCommandExecuted(String info) {
		this.interpreter.sendRunCommand(this.mcode);
	}
	
	@Override
	public void handleBreak(String info) {
		String[] pieces = info.split("\r\n");		
		int breakLineIndex = 0;
		for (String piece : pieces) {
			if (piece.startsWith(BREAK_IDENTIFIER)) break;
			++breakLineIndex;
		}
		if (breakLineIndex >= pieces.length) {
			throw new RuntimeException("Internal Error");
		}
		
		String entryInfo = pieces[breakLineIndex].substring("<BREAK>".length());
		MCodeLocation entryTag = MCodeLocation.getInstance(entryInfo);
		int lineNumber = this.getLineNumber(entryTag);
		this.stack = new MStackFrame[1];
		this.stack[0] = new MStackFrame(this.debugThread, entryInfo, entryTag.getTag(), entryTag.getRoutine(), lineNumber);
		
		this.variables = new MVariable[breakLineIndex-3];
		for (int i=1; i<breakLineIndex-2; ++i) {
			String nv = pieces[i];
			int equalLocation = nv.indexOf('=');
			if (equalLocation >= 0) {
				String name = nv.substring(0, equalLocation);
				String value = nv.substring(equalLocation+1);
				this.variables[i-1] = new MVariable(this.stack[0], name); 
				MValue mValue = new MValue(this.variables[i-1], value);
				this.variables[i-1].setValue(mValue);
			}				
		}

		suspended(DebugEvent.BREAKPOINT);			
	}

	@Override
	public void handleEnd() {		
		this.terminated();
	}

	@Override
	public void handleError(Throwable throwable) {	
		throw new RuntimeException(throwable);
	}

	private int getLineNumber(MCodeLocation entryTag) {
		String routineName = entryTag.getRoutine();
		FileSearchVisitor fsv = new FileSearchVisitor(routineName + ".m");
		try {
			fsv.run(this.project);
			IFile file = fsv.getFile();
			int lineNumber = this.getLineNumber(file, entryTag.getTag());
			return lineNumber + entryTag.getOffset();
		} catch (Throwable t) {			
			throw new RuntimeException("Invalid file");
		}
	}

	private int getLineNumber(IFile file, String tag) throws CoreException, BadLocationException {
		IDocument fileDocument = ResourceUtilExtension.getDocument(file);
		int n = fileDocument.getNumberOfLines();
		for (int i=0; i<n; ++i) {
			IRegion lineInfo = fileDocument.getLineInformation(i);
			int offset = lineInfo.getOffset();
			int length = lineInfo.getLength();
			String fileLine = fileDocument.get(offset, length);
			if (fileLine.startsWith(tag)) {
				return i+1;
			}
		}
		return -1;
	}
	
	private String getInitialBreakPointCommand() {
		String command = "";
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MDebugConstants.M_DEBUG_MODEL);
		for (IBreakpoint breakPoint : breakpoints) {
			try {
				if (breakPoint.isEnabled()) {
					if (breakPoint instanceof AbstractMBreakpoint) {
						AbstractMBreakpoint b = (AbstractMBreakpoint) breakPoint;
						String codeLocation = b.getAsDollarTextInput();						
						if (! command.isEmpty()) {
							command += ' ';
						}
						command += "ZBREAK " + codeLocation + ":\"B\":\"1\":\"W $C(0,0,0) ZWRITE\"";
					} else if (breakPoint instanceof MWatchpoint) {
						MWatchpoint b = (MWatchpoint) breakPoint;
						String variable = b.getWatchpointVariable();
						if (! command.isEmpty()) {
							command += ' ';
						}
						command += "ZBREAK *" + variable + ":\"B\":\"1\":\"W $C(0,0,0) ZWRITE\"";						
					}
				}
			} catch (CoreException coreExcepion) {			
			}
		}
		return command;
	}
}
