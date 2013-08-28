package gov.va.mumps.debug.ui.launching;

import java.util.ArrayList;
import java.util.List;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class MLaunchShortcut implements ILaunchShortcut {

	private final String CONFIG_TYPE = "gov.va.mumps.debug.core.launchConfigurationType";

	@Override
	public void launch(ISelection selection, final String mode) {
		if (selection != null && selection instanceof TreeSelection) {
			TreeSelection ts = (TreeSelection) selection;
			TreePath[] paths = ts.getPaths();
			TreePath path = paths[paths.length - 1];
			Object lastSegment = path.getLastSegment();
			if (lastSegment instanceof IFile) {
				final IFile file = (IFile) lastSegment;
				this.launch(file, mode);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, final String mode) {
		Object adaptor = editor.getEditorInput().getAdapter(IFile.class);
		if (adaptor instanceof IFile) {
			final IFile file = (IFile) adaptor;
			this.launch(file, mode);
		}
	}
	
	private void launch(IFile file, String mode) {
		try {
			List<ILaunchConfiguration> runables = this.getConfigurations(file);
			int n = runables.size();
			if (n == 1) {
				this.scheduleLaunch(runables.get(0), mode);
				return;
			}
			if (n > 1) {
				ILaunchConfiguration config = this.selectConfiguration(runables.toArray(new ILaunchConfiguration[0]), mode);
				if (config != null) {
					this.scheduleLaunch(config, mode);
					return;
				}
			}
			List<String> tags = TagUtilities.getTags(file);
			if ((tags != null) && (tags.size() > 0)) {
				String tag = TagUtilities.selectTag(tags);
				if (tag != null) {
					this.run(file, tag, mode);
				}
			}
			
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, MDebugConstants.M_DEBUG_MODEL);
		}
	}

	private void scheduleLaunch(final ILaunchConfiguration config, final String mode) {
		Job job = new Job("Start M Debug") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					config.launch(mode, null);
					return Status.OK_STATUS;
				} catch (final CoreException coreException) {
					Display.getDefault().asyncExec(new Runnable() {						
						@Override
						public void run() {
							String message = "'Launching " + config.getName() + "' has encountered an problem.";
							IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, message, coreException);
							StatusManager.getManager().handle(status, StatusManager.SHOW);
						}
					});
					return Status.OK_STATUS;
				}
			}
		};
		job.schedule();		
	}
	
	private List<ILaunchConfiguration> getConfigurations(IFile file) throws CoreException {
		List<ILaunchConfiguration> runables	= new ArrayList<ILaunchConfiguration>();
		IProject project  = file.getProject();
		String projectName = file.getProject().getName();
		String filePath = ResourceUtilExtension.getRelativePath(project, file);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(CONFIG_TYPE);
		ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations(configType);
		for (ILaunchConfiguration config : configs) {
			String configProjectName = config.getAttribute(MDebugConstants.ATTR_M_PROJECT_NAME, (String) null);
			if (! projectName.equals(configProjectName)) continue;			
			String configFilePath = config.getAttribute(MDebugConstants.ATTR_M_FILE_PATH, (String) null);
			if (! filePath.equals(configFilePath)) continue;						
			String entryTag = config.getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, (String) null);
			if ((entryTag == null) || entryTag.isEmpty()) continue;
			runables.add(config);
		}
		return runables;
	}
	
	private ILaunchConfiguration selectConfiguration(final ILaunchConfiguration[] configs, final String mode) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		LabelProvider lp = new LabelProvider() {
	    	@Override
	    	public Image getImage(Object element) {
	    		return null;
	    	}
	    		
	    	@Override
	    	public String getText(Object element) {
	    		if (element instanceof ILaunchConfiguration) {
	    			return ((ILaunchConfiguration) element).getName();
	    		} else {
	    			return null;
	    		}
	    	}
    	}; 
		ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, lp);
		dlg.setMultipleSelection(false);
		dlg.setTitle("Entry Tag Selection");
		dlg.setMessage("Select entry tag.");
		dlg.setElements(configs);
		if (ListSelectionDialog.OK == dlg.open()) {
			ILaunchConfiguration result = (ILaunchConfiguration) dlg.getFirstResult();
			return result;
		}
		return null;
	}
		
	public void run(IFile file, String tag, String mode) {
		IProject project = file.getProject();
		String projectName = project.getName();
		String filePath = ResourceUtilExtension.getRelativePath(project, file);
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager.getLaunchConfigurationType(CONFIG_TYPE);
		ILaunchConfigurationWorkingCopy wc;
		try {
			String fileName = file.getName();
			String routineName = fileName.substring(0, fileName.length()-2); 
			wc = configType.newInstance(null, manager.generateLaunchConfigurationName(routineName));
			wc.setAttribute(MDebugConstants.ATTR_M_PROJECT_NAME, projectName);
			wc.setAttribute(MDebugConstants.ATTR_M_FILE_PATH, filePath);
			wc.setAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, tag);
			ILaunchConfiguration config = wc.doSave();
			this.scheduleLaunch(config, mode);
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, MDebugConstants.M_DEBUG_MODEL);
		}
	}
}