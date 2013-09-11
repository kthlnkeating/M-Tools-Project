package gov.va.mumps.debug.core.launching;

import java.util.List;

import gov.va.med.iss.connection.VistAConnection;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.MDebugSettings;
import gov.va.mumps.debug.core.model.MCacheTelnetDebugTarget;
import gov.va.mumps.debug.core.model.MCacheTelnetProcess;
import gov.va.mumps.debug.core.model.MDebugPreference;
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
		@SuppressWarnings("unchecked")
		List<String> params = configuration.getAttribute(MDebugConstants.ATTR_M_PARAMS, (List<String>) null);
		boolean extrinsic = configuration.getAttribute(MDebugConstants.ATTR_M_IS_EXTRINSIC, false);
		
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
		VistAConnection vc = VLConnectionPlugin.getConnectionManager().findConnection(serverName);
		if (vc == null) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "No connection has been established for server " + serverName + ".");
			throw new CoreException(status);						
		}
		IPath path = Path.fromOSString(filePath);
		String fileName = path.lastSegment();
		String routineName = fileName.substring(0, fileName.length()-2);
		StringBuilder sb = new StringBuilder();
		if (extrinsic) {
			sb.append("N % S %=$$");
		} else {
			sb.append("D ");
		}
		sb.append(entryTag);
		sb.append('^');
		sb.append(routineName);
		if (params != null) {
			sb.append('(');
			boolean first = true;
			for (String param : params) {
				if (! first) {
					sb.append(',');
				}
				first = false;
				sb.append(param);
			}
			sb.append(')');
		} 
		String mCode = sb.toString();
		
		if (MDebugSettings.getDebugPreference() == MDebugPreference.CACHE_TELNET) {
			MCacheTelnetProcess rpcProcess = new MCacheTelnetProcess(launch, vc, mCode, null);
			IDebugTarget target = new MCacheTelnetDebugTarget(launch, rpcProcess);	
			launch.addDebugTarget(target);
		} else {
			MDebugRpcProcess rpcProcess = new MDebugRpcProcess(launch, vc, mCode, null);
			IDebugTarget target = new MDebugTarget(launch, rpcProcess);	
			launch.addDebugTarget(target);			
		}
	}
}
