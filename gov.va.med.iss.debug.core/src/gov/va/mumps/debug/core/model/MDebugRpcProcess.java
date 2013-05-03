package gov.va.mumps.debug.core.model;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.mumps.debug.xtdebug.XtdebugHandler;
import gov.va.mumps.debug.xtdebug.vo.StepResultsVO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

import com.sun.corba.se.impl.activation.ProcessMonitorThread;

//TODO: This is really supposed to be wrapper for a system process. Since there is no system process running, I could/should move all this logic elsewhere
public class MDebugRpcProcess extends PlatformObject implements IProcess {
	
	//private MDebugger mDebugger; //TODO: this will no longer be a wrapper for mDebugger (one of many threads, but more of a wrapper for the RPCHandler) instance, it just uses 1 of 2 mDebuggers to talk to it.

	//TODO: what are concernsof this class... probably just the implementing methods of the IProcess interface and that is it.
	/*
	 * terminate does nothing, as is the same with resume, since a process
	 * cannot be suspended. additionally stream proxy may also not be used since
	 * a new custom debug console may be needed.... therefore making this an
	 * uneeded man-in-the-middle class?
	 */
	
	private ILaunch launch;
	private ProcessMonitorThread monitor;
	//private IStreamsProxy streamsProxy = new MConsoleStreamProxy();
	private String name;
	private boolean terminated;
	private Map<String,String> attributes;
	private StepResultsVO responseResults;
	private XtdebugHandler xtdebugHandler;
	
	//optimization
	private static final Pattern semiPat = Pattern.compile(";");
	
	public MDebugRpcProcess(ILaunch launch, String debugEntryTag, Map<String, String> attributes) {
		initializeAttributes(attributes);
		xtdebugHandler = new XtdebugHandler(VistaConnection.getConnection());
		responseResults = xtdebugHandler.startDebug(debugEntryTag);
		
		//name = debugEntryTag;
		if (VistaConnection.getCurrentServer() != null && !VistaConnection.getCurrentServer().isEmpty()) { //dislike globals like this, want to refactor this to OOP using factories and explicit dependencies in contructors
			String connStr = VistaConnection.getCurrentServer();
			String[] pieces = semiPat.split(connStr);
			name = "VistA Connection: " +pieces[0]+ ", " +pieces[1]+ ":" +pieces[2];
		} else
			name = "Error: Not connected to VistA";
		this.launch = launch;
		launch.addProcess(this);
		fireCreationEvent();
	}
	
	private void initializeAttributes(Map<String,String> attributes) {
		if (attributes != null) {
			Iterator<String> keys = attributes.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String)keys.next();
				setAttribute(key, (String)attributes.get(key));
			}	
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IProcess.class)) {
			return this;
		}
		if (adapter.equals(IDebugTarget.class)) {
			ILaunch launch = getLaunch();
			IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (this.equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		}
		if (adapter.equals(ILaunch.class)) {
			return getLaunch();
		}
		//CONTEXTLAUNCHING
		if(adapter.equals(ILaunchConfiguration.class)) {
			return getLaunch().getLaunchConfiguration();
		}
		return super.getAdapter(adapter);
	}

	@Override
	public boolean canTerminate() {
		return !terminated;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public void terminate() { //terminating is rather simple, just stop calling the API
		//responseResults = mDebugger.terminate();
		if (isTerminated())
			return;
		
		terminated = true;
		fireTerminateEvent();
	}

	@Override
	public String getAttribute(String key) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(key);
	}
	
	@Override
	public void setAttribute(String key, String value) {
		if (attributes == null) {
			attributes = new HashMap<String,String>(5);
		}
		Object origVal = attributes.get(key);
		if (origVal != null && origVal.equals(value)) {
			return; //nothing changed.
		}
		
		attributes.put(key, value);
		fireChangeEvent();
	}

	@Override
	public int getExitValue() throws DebugException {
		return 0;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		//return streamsProxy;
		return null;
	}

	private void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}
	
	private void fireEvent(DebugEvent event) {
		DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[]{event});
		}
	}
	
	private void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}

	private void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	//TODO: why go through the debug wrapper for these? Can I just invoke mDebugger directly from either the console or debugger ui now that there are 2 copies of it?
	public void resume() {
		responseResults = xtdebugHandler.resume();
	}

	public void stepOver() {
		//responseResults = mDebugger.stepOver();
	}

	public void stepInto() {
		responseResults = xtdebugHandler.stepInto();
	}

	public void stepOut() {
		//responseResults = mDebugger.stepOut();
	}
	
	public void addBreakPoint(String breakPoint) {
		xtdebugHandler.addBreakpoint(breakPoint);
	}
	
	public void removeBreakPoint(String breakPoint) {
		xtdebugHandler.removeBreakpoint(breakPoint);
	}
	
	public void addWatchPoint(String watchPoint) {
		xtdebugHandler.addWatchpoint(watchPoint);
	}
	
	public void removeWatchPoint(String watchPoint) {
		xtdebugHandler.removeWatchpoint(watchPoint);
	}

	public StepResultsVO getResponseResults() {
		return responseResults;
	}

	public void sendReadInput(String input) {
		responseResults = xtdebugHandler.sendReadInput(input);
	}	
}
