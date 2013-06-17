package org.mumps.pathstructure.vista.foia;

import java.nio.file.FileSystems;

import org.mumps.pathstructure.vista.RoutinePathResolver;

public class PreferencesPathResolver implements RoutinePathResolver {
	
	
	private String serverName;
	private int namespace;
	
	public PreferencesPathResolver(String serverName, int namespace) {
		super();
		this.serverName = serverName;
		this.namespace = namespace;
	}

	@Override
	public String getRelativePath(String routineName) {

		return 
				(serverName != null ? serverName+"/" : "") +
				(namespace != 0 ? routineName.substring(0,namespace) : "");
	}

}
