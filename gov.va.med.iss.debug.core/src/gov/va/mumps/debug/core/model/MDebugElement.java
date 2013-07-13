package gov.va.mumps.debug.core.model;

import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.MDebugCorePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

public abstract class MDebugElement extends PlatformObject implements IDebugElement {

	// containing target 
	private MDebugTarget target;
	
	/**
	 * Constructs a new debug element contained in the given
	 * debug target.
	 * 
	 * @param target debug target (PDA VM)
	 */
	public MDebugElement(MDebugTarget target) {
		this.target = target;
	}
	
	@Override
	public String getModelIdentifier() {
		return MDebugConstants.M_DEBUG_MODEL;
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return target;
	}
	
	protected void setDebugTarget(MDebugTarget target) {
		this.target = target;
	}
	
	@Override
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	protected void abort(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, MDebugCorePlugin.getInstance().getPluginId(), 
				DebugPlugin.INTERNAL_ERROR, message, e));
	}
	
	/**
	 * Fires a debug event
	 * 
	 * @param event the event to be fired
	 */
	private void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] {event});
	}
	
	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}	
	
	/**
	 * Fires a <code>RESUME</code> event for this element with
	 * the given detail.
	 * 
	 * @param detail event detail code
	 */
	protected void fireResumeEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a <code>SUSPEND</code> event for this element with
	 * the given detail.
	 * 
	 * @param detail event detail code
	 */
	protected void fireSuspendEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}
	
	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
}
