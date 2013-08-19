package gov.va.mumps.debug.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

public class MSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	@Override
	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration launchConfig, IProgressMonitor progMonitor)
			throws CoreException {
		/*
		 * this code runs from a plugin perspective...
		 * meaning that when looking for a path for a given routine, that
		 * routine could exist in any project, multiple projects, and
		 * also multiple times in a project for whatever reason (eg: like
		 * the backup directory).
		 * 
		 * What is really being debugged is a routine on the server, so that
		 * routine may not be loaded into the the workspace at all. For this
		 * case we'll just fail the lookup, in the future it could support
		 * loading the missing routine. TODO
		 * 
		 * Anyway, as for finding the "correct" one, the right workspace
		 * will either be 'mcode' if the current connection does not have
		 * a project specified, otherwise it will be what is in the connection
		 * string's 4th piece, the project name. The file will need to be
		 * scanned for, since it could be anywhere. for 'mcode' and custom
		 * projects, do not look in the backup folder.
		 * 
		 * Also, the same workspace should be chosen for all debugger
		 * stack frames. It shouldn't point the user to different workspaces,
		 * while it is debugging, that would be confusing. The single workspace
		 * to chose will be either 'mcode', or the one specified in the
		 * current connection string.
		 * 
		 * Update: Do not implement the search. Instead only add the source containers
		 * for each directory in the current project, excluding the backup directory.
		 * 
		 * Update2: Notice that the SourceLookupTab lets the user further configure
		 * and modify the source path.
		 * 
		 * Update3: backup folder is only applicable to the mcode project.
		 */
		
		//String currentPojrect = VistaConnection.getCurrentProject();
		//IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(currentPojrect);
//		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(currentPojrect);
//		URI uri = resource.getLocationURI();
//		System.out.println(uri.toString());
		
		//ISourceContainer sourceContainer = null;
		//IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(currentPojrect));
		//if (resource != null) {
		//	IContainer container = resource.getParent();
		//	if (container.getType() == IResource.PROJECT) {
		//		sourceContainer = new ProjectSourceContainer((IProject)container, false);
		//	} 
//			else if (container.getType() == IResource.FOLDER) {
//				sourceContainer = new FolderSourceContainer(container, false);
//			}			
		//}
		
		//if (sourceContainer == null) {
		ISourceContainer sourceContainer = new WorkspaceSourceContainer();
		//}
		return new ISourceContainer[]{sourceContainer};
	}

}
