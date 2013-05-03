package gov.va.mumps.debug.ui;

import gov.va.mumps.debug.core.model.MDebugTarget;
import gov.va.mumps.debug.ui.console.MDevConsole;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class MDebugUIPlugin extends AbstractUIPlugin implements
		ILaunchListener {

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);	
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
		} finally {
			super.stop(context);
		}
	}

	@Override
	public void launchAdded(final ILaunch launch) {
	}

	@Override
	public void launchChanged(ILaunch launch) {
		
		if (launch == null || launch.getDebugTarget() == null)
			return;
		
		//TODO: set a variable on synced method to MDebugTarget to incidate that the console was created to prevent it from creating multiple console each time this event files.
		
		MDevConsole mDevConsole = new MDevConsole("hi I'm a new test console", null, null, true);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { mDevConsole });
		
		System.out.println("found MDebugTarget: " +((MDebugTarget)launch.getDebugTarget()).getAllVariables());
	}

	@Override
	public void launchRemoved(ILaunch launch) {
		//TODO: remove the listeners that were added to the custom console
	}
}
