package org.mumps.pathstructure.vista.foia;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VFPackageRepo implements PackageRepository {

	private File packagesCSVFile;
	private Map<String,String> cache;
	
	public VFPackageRepo(File packagesCSVFile) {
		this.packagesCSVFile = packagesCSVFile;
	}
	
	private Map<String,String> loadPackages() {
		Map<String,String> results = new TreeMap<String,String>();
		

		Scanner scanner = null;;
		try {
			scanner = new Scanner(packagesCSVFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		scanner.nextLine();
		while (scanner.hasNextLine()) {
			//String prefix;
			String packageDirectory = null;
			
			String line = scanner.nextLine();
			String[] pieces = line.split(",");
			if (!pieces[0].isEmpty() && !pieces[1].isEmpty() && !pieces[2].isEmpty()) { //0 = package name, 1 - directory name, 2 - prefix
				packageDirectory = pieces[1];
				results.put(pieces[2], packageDirectory);
			} else if (!pieces[2].isEmpty()) {
				results.put(pieces[2], packageDirectory);
			}
//			if ((pieces.length > 2) && (pieces[2].length() > 0)) {
//				packageInfo.addPrefix(pieces[2]);
//				if (pieces[2].charAt(0) != '!') {
//					r.packagesByPrefix.put(pieces[2], packageInfo);				
//				}
//			}
//			if ((pieces.length > 3) && (pieces[3].length() > 0)) {
//				packageInfo.addFile(pieces[3], pieces[4]);
//			}
		}
		
		scanner.close();
		return results;
	}
	
	@Override
	public String getPackageDirectory(String prefix) {
		if (cache == null)
			cache = loadPackages();
		
		return cache.get(prefix);
	}

}
