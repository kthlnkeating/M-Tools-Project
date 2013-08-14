package gov.va.med.iss.meditor.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class RootPathResolver extends RoutinePathResolver {
	@Override
	protected IPath getRelativePath(String routineName) {
		return new Path("");
	}
}
