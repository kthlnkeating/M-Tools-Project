package gov.va.med.iss.meditor.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class RootPathResolver implements RoutinePathResolver {
	@Override
	public IPath getRelativePath(String routineName) {
		return new Path("");
	}
}
