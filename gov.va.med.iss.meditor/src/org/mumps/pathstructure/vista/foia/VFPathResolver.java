package org.mumps.pathstructure.vista.foia;

import org.mumps.pathstructure.vista.RoutinePathResolver;

public class VFPathResolver extends RoutinePathResolver {

	private PackageRepository packageRepository = VFPackageRepo.getInstance();
	
	//needs to query from a lazily loaded (singleton) list of [namespace,packge directory]
	//-this should be populated by an object/dependency
	
	
	@Override
	public String getRelativePath(String routineName) {
		
		for (int i = 1; i <= 4; i++) { //try using all the namespaces prefix of up to 4
			String relativePath = packageRepository.getPackageDirectory(routineName.substring(0, i));
			if (relativePath != null)
				return relativePath;
		}
		
		return ""; //return the root path if a routine cannot be mapped
	}

}
