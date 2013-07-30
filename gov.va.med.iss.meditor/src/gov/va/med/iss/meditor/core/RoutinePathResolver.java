package gov.va.med.iss.meditor.core;

import org.eclipse.core.runtime.IPath;

public interface RoutinePathResolver {
	IPath getRelativePath(String routineName);
}
