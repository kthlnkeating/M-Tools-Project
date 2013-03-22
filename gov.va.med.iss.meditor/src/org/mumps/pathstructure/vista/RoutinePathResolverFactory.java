package org.mumps.pathstructure.vista;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mumps.pathstructure.generic.RootPathResolver;
import org.mumps.pathstructure.vista.foia.VFPackageRepo;
import org.mumps.pathstructure.vista.foia.VFPathResolver;


public class RoutinePathResolverFactory {
	
	//Singleton class, easier to put inside the existing code this way
	private static volatile RoutinePathResolverFactory psf = null;
	public static RoutinePathResolverFactory getInstance() {
		if (psf == null) {
			synchronized (VFPackageRepo.class) {
				if (psf == null)
					psf = new RoutinePathResolverFactory();
			}
		}
		return psf;
	}

	private RoutinePathResolverFactory() {
		rprPool = new HashMap<File,RoutinePathResolver>(5);
	}
	//Singleton class
	
	private Map <File, RoutinePathResolver> rprPool;
	
	public RoutinePathResolver getRoutinePathResolver(File projectPath) {
		
		if (rprPool.get(projectPath) != null)
			return rprPool.get(projectPath);
		
		RoutinePathResolver result;
		
		File packagesCsvFile = new File(projectPath, "Packages.csv");
		if (!packagesCsvFile.exists())
			result = new RootPathResolver();
		else {
			result = new VFPathResolver(new VFPackageRepo(packagesCsvFile));
		}
		
		rprPool.put(projectPath, result);
		return result;
	}

}
