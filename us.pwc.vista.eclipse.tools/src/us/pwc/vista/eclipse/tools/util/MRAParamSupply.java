package us.pwc.vista.eclipse.tools.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import us.pwc.vista.eclipse.core.VistACorePrefs;

import com.pwc.us.rgi.m.tool.SourceCodeFiles;

public class MRAParamSupply {
	public static SourceCodeFiles getSourceCodeFiles(IProject project, String backupDirName) throws CoreException {
		String[] doNotUseFolderNames = VistACorePrefs.getDoNotUseProjectSubfolders(project, null);
		FileFillVisitor ffv = new FileFillVisitor(project, doNotUseFolderNames);
		project.accept(ffv);
		SourceCodeFiles scf = ffv.getSourceCodeFiles();
		return scf;
	}
}
