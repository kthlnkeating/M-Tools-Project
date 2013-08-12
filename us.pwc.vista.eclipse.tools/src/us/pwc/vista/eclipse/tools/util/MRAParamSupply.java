package us.pwc.vista.eclipse.tools.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.pwc.us.rgi.m.tool.SourceCodeFiles;

public class MRAParamSupply {
	private static void updateForResource(IResource resource, SourceCodeFiles scf) throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			String name = resource.getName();
			if (name.endsWith(".m")) {
				String filePath = file.getProjectRelativePath().toString();
				name = name.substring(0, name.length()-2);
				scf.put(name, filePath);
			}
		} else if (resource instanceof IFolder) {
			IFolder folder = (IFolder) resource;
			updateForContainer(folder, scf, null);
		}
		
	}
	
	private static void updateForContainer(IContainer container, SourceCodeFiles scf, String excludeName) throws CoreException {
		IResource[] members = container.members();
		for (int i=0; i<members.length; ++i) {
			IResource r = members[i];
			if ((excludeName == null) || (! r.getName().equals(excludeName))) {
				updateForResource(r, scf);
			}
		}		
	}

	public static SourceCodeFiles getSourceCodeFiles(IProject project, String backupDirName) throws CoreException {
		String root = project.getLocation().toString();
		SourceCodeFiles scf = new SourceCodeFiles(root);
		updateForContainer(project, scf, backupDirName);
		return scf;
	}
}
