package gov.va.mumps.debug.core.launching;

import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.model.MDebugRpcProcess;
import gov.va.mumps.debug.core.model.MDebugTarget;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class MLaunchDelegate extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		// routine name
		String debugEntryTag = configuration.getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, (String)null);
		if (debugEntryTag == null) {
			abort("M Entry tag not specified in launch configuration.", null);
		}
		
		MDebugRpcProcess rpcProcess = new MDebugRpcProcess(launch, debugEntryTag, null);
		
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			IDebugTarget target = new MDebugTarget(launch, rpcProcess);
			launch.addDebugTarget(target);
		}
	}
	
	/**
	 * Throws an exception with a new status containing the given
	 * message and optional exception.
	 * 
	 * @param message error message
	 * @param e underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException {
		// TODO: the plug-in code should be the example plug-in, not Perl debug model id
		throw new CoreException(new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, 0, message, e));
	}

}
