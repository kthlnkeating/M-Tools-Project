package org.mumps.pathstructure.vista;


import java.util.HashMap;
import java.util.Map;

import org.mumps.pathstructure.vista.foia.VFPackageRepo;


public class PathStructureFactory {
	
	//Singleton class
	private static volatile PathStructureFactory psf = null;
	public static PathStructureFactory getInstance() {
		if (psf == null) {
			synchronized (VFPackageRepo.class) {
				if (psf == null)
					psf = new PathStructureFactory();
			}
		}
		return psf;
	}

	private PathStructureFactory() {
		factoryCache = new HashMap<String,RoutinePathResolver>(5);
	}
	//Singleton class
	
	private Map <String, RoutinePathResolver> factoryCache;
	
	public RoutinePathResolver getPathResolverForProject(String project) {
		//determine the type of directory structure
		
		return null;
	}
	
	private PathStructureENUM getPathStructureType(String project) {
		
		return null;
	}

}
