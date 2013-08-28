package gov.va.mumps.debug.core.launching;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class MSourcePathComputerDelegate implements ISourcePathComputerDelegate {
	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration launchConfig, IProgressMonitor progMonitor) throws CoreException {
		String projectName = launchConfig.getAttribute(MDebugConstants.ATTR_M_PROJECT_NAME, (String) null);
		if (projectName != null) {
			IProject project = ResourceUtilExtension.getProject(projectName);
			if (project != null) {
				ISourceContainer sourceContainer = new ProjectSourceContainer(project, false);
				return new ISourceContainer[]{sourceContainer};
			}
		}
		ISourceContainer sourceContainer = new WorkspaceSourceContainer();
		return new ISourceContainer[]{sourceContainer};
	}
}
