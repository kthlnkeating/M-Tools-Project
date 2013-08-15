package us.pwc.vista.eclipse.server.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

public abstract class RoutinePathResolver {
	protected abstract IPath getRelativePath(String routineName);

	public IFile getFileHandle(IProject project, String routineName) {
		IPath relRoutinePath = this.getRelativePath(routineName);
		relRoutinePath = relRoutinePath.append(routineName + ".m");
		IFile result = project.getFile(relRoutinePath);
		return result;		
	}
}
