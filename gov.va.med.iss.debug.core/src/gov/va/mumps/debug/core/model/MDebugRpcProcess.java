package gov.va.mumps.debug.core.model;

import gov.va.med.iss.mdebugger.MDebugger;
import gov.va.med.iss.mdebugger.vo.StepResultsVO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

public class MDebugRpcProcess extends PlatformObject implements IProcess {
	
	private MDebugger mDebugger;

	private ILaunch launch;
	private ProcessMonitorThread monitor;
	private IStreamsProxy streamsProxy;
	private String name;
	private boolean terminated;
	private Map<String,String> attributes;
	private boolean captureOutput = true;
	private StepResultsVO responseResults;
	
	public MDebugRpcProcess(String debugEntryTag, Map<String, String> attributes) {
		initializeAttributes(attributes);
		mDebugger = new MDebugger();
		responseResults = mDebugger.startDebug(debugEntryTag);
		name = debugEntryTag;
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
	public void terminate() {
		//responseResults = mDebugger.terminate();
		terminated = true;
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
	    if (!captureOutput) {
	        return null;
	    }
		return streamsProxy;
	}

	protected void fireChangeEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CHANGE));
	}
	
	protected void fireEvent(DebugEvent event) {
		DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[]{event});
		}
	}

	public void resume() {
		responseResults = mDebugger.resume();
	}

	public void stepOver() {
		responseResults = mDebugger.stepOver();
	}

	public void stepInto() {
		responseResults = mDebugger.stepInto();
	}

	public void stepOut() {
		responseResults = mDebugger.stepOut();
	}
	
	public void addBreakPoint(String breakPoint) {
		mDebugger.addBreakpoint(breakPoint);
	}
	
	public void removeBreakPoint(String breakPoint) {
		mDebugger.removeBreakpoint(breakPoint);
	}

	public StepResultsVO getResponseResults() {
		return responseResults;
	}

	void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
	
}
