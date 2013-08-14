package gov.va.med.iss.meditor.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class VFPathResolver extends RoutinePathResolver {
	private PackageRepository packageRepository;
	
	public VFPathResolver(PackageRepository packageRepository) {
		this.packageRepository = packageRepository;
	}

	@Override
	protected IPath getRelativePath(String routineName) {
		for (int i = 1; i <= 4; i++) { //try using all the namespaces prefix of up to 4
			String packageDirectory = this.packageRepository.getPackageDirectory(routineName.substring(0, i));
			if (packageDirectory != null) {
				IPath path = new Path("Packages");
				path = path.append(packageDirectory);
				path = path.append("Routines");
				return path;
			}
		}
		return null;
	}
}
