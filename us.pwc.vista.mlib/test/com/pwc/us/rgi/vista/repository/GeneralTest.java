package com.pwc.us.rgi.vista.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.vista.repository.FileSupply;
import com.pwc.us.rgi.vista.repository.RepositoryInfo;
import com.pwc.us.rgi.vista.repository.VistaPackage;
import com.pwc.us.rgi.vista.tools.MRARoutineFactory;

public class GeneralTest {
	private RepositoryInfo getRepositoryInfo() {
		try {
			String root = RepositoryInfo.getLocation();
			Assert.assertTrue("No location for repository root is specified.", (root != null) && (root.length() > 0));
			MRARoutineFactory rf = MRARoutineFactory.getInstance(MVersion.CACHE);
			RepositoryInfo ri = RepositoryInfo.getInstance(root, rf);
			return ri;
		} catch (IOException e) {
			Assert.fail("IO Exception.  Repository info file likely inexistent.");
			return null;
		}
	}
	
	@Test
	public void test() {
		RepositoryInfo ri = this.getRepositoryInfo();
		VistaPackage pi = ri.getPackage("PROBLEM LIST");
		Assert.assertNotNull(pi);
		Path packagePath = ri.getPackagePath("PROBLEM LIST");			
		FileSupply fs = new FileSupply();
		fs.addPath(packagePath);
		try {
			List<Path> paths = fs.getMFiles();
			Assert.assertTrue("No Problem List file found.", paths.size() > 0);
			for (Path path : paths) {
				String fileName = path.getFileName().toString();
				VistaPackage pib = ri.getPackageFromRoutineName(fileName);
				Assert.assertEquals(pi, pib);
			}			
		} catch (IOException e) {
			Assert.fail("IO Exception.  Repository info file likely inexistent.");
		}
	}
	
	private void testPackageFromRoutine(String expectedPackageName, RepositoryInfo ri, String routineName) {
		String actualName = ri.getPackageFromRoutineName(routineName).getPackageName();
		Assert.assertEquals(expectedPackageName, actualName);
	}
	
	@Test
	public void testPackageFromRoutine() {
		RepositoryInfo ri = this.getRepositoryInfo();
		List<VistaPackage> packages = ri.getAllPackages();
		for (VistaPackage p : packages) {
			String packageName = p.getPackageName();
			List<String> prefixes = p.getPrefixes();
			for (String prefix : prefixes) if (prefix.charAt(0) != '!') {
				if (prefix.equals("VEJD")) continue;   // Multuple namespace
				this.testPackageFromRoutine(packageName, ri, prefix);
				this.testPackageFromRoutine(packageName, ri, prefix + 'a');
				this.testPackageFromRoutine(packageName, ri, prefix + '0');
				this.testPackageFromRoutine(packageName, ri, prefix + "aaa");
			}
		}		
	}	
}
