package us.pwc.vista.eclipse.wizard.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;
import us.pwc.vista.eclipse.wizard.VistAWizardsPlugin;

public class NewVistAFileWizard extends Wizard implements INewWizard {
	private IWorkbench workbench;
	private IStructuredSelection selection;

	public NewVistAFileWizard() {
		super();
        super.setWindowTitle("New VistA M File");
	}
	
	@Override
	public void addPages() {
		addPage(new NewFileSelectPage("Select File", this.selection));
		addPage(new NewFileTopLinesPage("Create Top Lines"));
	}

	@Override
	public boolean performFinish() {
		NewFileSelectPage page1st = (NewFileSelectPage) this.getPage("Select File");
		IProject project = page1st.getProject();
		if (project != null) {		
			NewFileTopLinesPage page2nd = (NewFileTopLinesPage) this.getPage("Create Top Lines");
			String line1st = page1st.getRoutineName() + " " + page2nd.getFirstLine();
			String line2nd = " " + page2nd.getSecondLine();
			String eol = ResourceUtilExtension.getLineSeperator(project);
			String lines = line1st + eol + line2nd + eol;
			page1st.setInitialContent(lines);
			IFile file = page1st.createNewFile();
			if (file != null) {
				IWorkbenchWindow w = this.workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = w.getActivePage();
				try {
					IDE.openEditor(page, file);
				} catch (Throwable t) {
					Status status = new Status(IStatus.ERROR, VistAWizardsPlugin.PLUGIN_ID, "Unable to open editor.", t);				
					StatusManager.getManager().handle(status, StatusManager.LOG);
				}					
				return true;
			}
		}
		return true;
	}

	@Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
        this.selection = selection;
    }
}
