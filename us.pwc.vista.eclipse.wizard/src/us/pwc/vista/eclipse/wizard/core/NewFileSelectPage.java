package us.pwc.vista.eclipse.wizard.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewFileSelectPage extends WizardNewFileCreationPage {
	private String initialContent;
	
	public NewFileSelectPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
        setTitle("M File");
        setDescription("Create a new M file resource.");
        setFileExtension("m");	
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) {
	}

	@Override
	protected IStatus validateLinkedResource() {
		return Status.OK_STATUS;
	}
	
	@Override
	protected void createLinkTarget() {
	}
		
	@Override
	protected InputStream getInitialContents() {
		if (this.initialContent == null) {
			return super.getInitialContents();
		} else {
			try {
				return new ByteArrayInputStream(this.initialContent.getBytes("UTF-8"));
			} catch (Throwable t) {
				return super.getInitialContents();				
			}
		}
	}
	
	public void setInitialContent(String initialContent) {
		this.initialContent = initialContent;
	}
	
	public IProject getProject() {
		IPath containerPath = this.getContainerFullPath();
		if (containerPath != null) {
			String fileName = this.getFileName();
			if (fileName != null) {
				IPath path = containerPath.append(fileName);
				IFile file = this.createFileHandle(path);
				if (file != null) {
					return file.getProject();
				}
			}
		}
		return null;
	}

	public String getRoutineName() {
		String fileName = this.getFileName();
		return fileName.substring(0, fileName.length()-2);
	}
}
