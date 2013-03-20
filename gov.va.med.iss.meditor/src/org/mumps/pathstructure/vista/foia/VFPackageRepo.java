package org.mumps.pathstructure.vista.foia;


import java.util.Map;

public class VFPackageRepo implements PackageRepository {
	
	//Singleton class
	private static volatile VFPackageRepo vfpr = null;
	public static VFPackageRepo getInstance() {
		if (vfpr == null) {
			synchronized (VFPackageRepo.class) {
				if (vfpr == null)
					vfpr = new VFPackageRepo();
			}
		}
		return vfpr;
	}

	private VFPackageRepo() {
		//TODO: uncomment packagesByPrefix = repoScanner.loadPackages();
	}
	//Singleton class
	
	//private VFRepoScanner repoScanner = new VFRepoScanner(); //TODO: uncomment
	private Map<String,String> packagesByPrefix;

	@Override
	public String getPackageDirectory(String prefix) {
		return packagesByPrefix.get(prefix);
	}

}
