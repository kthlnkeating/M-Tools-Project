package gov.va.med.iss.meditor.command.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;

public class FileSearchVisitor implements IResourceProxyVisitor {
	private IFile file;
	private String fileName;
	private String excludeDirectory;
	
	public FileSearchVisitor(String fileName, String excludeDirectory) {
		this.fileName = fileName;
		this.excludeDirectory = excludeDirectory;
	}
	
	@Override
	public boolean visit (IResourceProxy proxy) { 
		if (this.file != null) {
			return false;
		}
		String name = proxy.getName();
		if (proxy.getType() != IResource.FILE) {
			return ! name.equals(this.excludeDirectory);
		}
		if (! name.equals(this.fileName)) {
			return true;
		}
		this.file = (IFile) proxy.requestResource();
		return false;
    } 
	
	public IFile getFile() {
		return this.file;
	}
}
	
