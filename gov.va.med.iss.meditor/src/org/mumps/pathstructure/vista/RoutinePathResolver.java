package org.mumps.pathstructure.vista;

import java.io.File;

public abstract class RoutinePathResolver {

	private File projectFilesLocation;
	
	public abstract String getRelativePath(String routineName);
	
}
