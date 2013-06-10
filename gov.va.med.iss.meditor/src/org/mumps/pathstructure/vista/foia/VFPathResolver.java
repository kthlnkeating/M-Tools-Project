package org.mumps.pathstructure.vista.foia;

import java.nio.file.FileSystems;

import org.mumps.pathstructure.vista.RoutinePathResolver;

public class VFPathResolver implements RoutinePathResolver {

	private RoutinePathResolver resolver;
	private PackageRepository packageRepository;
	
	private static final String SEP = FileSystems.getDefault().getSeparator();
	
	public VFPathResolver(RoutinePathResolver backupResolver, PackageRepository packageRepository) {
		this.resolver = backupResolver;
		this.packageRepository = packageRepository;
	}

	//needs to query from a lazily loaded (singleton) list of [namespace,packge directory]
	//-this should be populated by an object/dependency
		
	@Override
	public String getRelativePath(String routineName) {
		
		for (int i = 1; i <= 4; i++) { //try using all the namespaces prefix of up to 4
			String packageDirectory = packageRepository.getPackageDirectory(routineName.substring(0, i));
			if (packageDirectory != null)
				return "Packages"+SEP+ packageDirectory +SEP+"Routines";
		}
		
		return resolver.getRelativePath(routineName);
	}

}
