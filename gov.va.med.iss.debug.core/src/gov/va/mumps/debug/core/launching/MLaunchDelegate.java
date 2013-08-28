package gov.va.mumps.debug.core.launching;

import gov.va.med.iss.connection.ConnectionData;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.model.MDebugRpcProcess;
import gov.va.mumps.debug.core.model.MDebugTarget;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import us.pwc.vista.eclipse.core.VistACorePrefs;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class MLaunchDelegate extends LaunchConfigurationDelegate {
	private String getAttribute(ILaunchConfiguration configuration, String attrKey, String attrName) throws CoreException {
		String value = configuration.getAttribute(attrKey, (String) null);
		if (value == null) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "Required " + attrName + " parameter is not defined.");
			throw new CoreException(status);
		}		
		return value;
	}
		
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String projectName = this.getAttribute(configuration, MDebugConstants.ATTR_M_PROJECT_NAME, "'project name'");
		String filePath = this.getAttribute(configuration, MDebugConstants.ATTR_M_FILE_PATH, "'file path'");
		String entryTag = this.getAttribute(configuration, MDebugConstants.ATTR_M_ENTRY_TAG, "'entry tag'");

		IProject project = ResourceUtilExtension.getProject(projectName);
		if ((project == null) || (! project.exists())) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "Project " + projectName + " does not exists.");
			throw new CoreException(status);			
		}
		String serverName = VistACorePrefs.getServerName(project);
		if ((serverName == null) || serverName.isEmpty()) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "No server is specified for project " + projectName + ".");
			throw new CoreException(status);						
		}
		ConnectionData cd = VLConnectionPlugin.getConnectionManager().findConnection(serverName);
		if (cd == null) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "No connection has been established for server " + serverName + ".");
			throw new CoreException(status);						
		}
		IPath path = Path.fromOSString(filePath);
		String fileName = path.lastSegment();
		String routineName = fileName.substring(0, fileName.length()-2);
		String debugEntryTag = "D " + entryTag + "^" + routineName;
		
		MDebugRpcProcess rpcProcess = new MDebugRpcProcess(launch, cd, debugEntryTag, null);

		IDebugTarget target = new MDebugTarget(launch, rpcProcess);
		launch.addDebugTarget(target);
	}
}
