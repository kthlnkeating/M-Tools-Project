package gov.va.mumps.debug.ui.launching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

@SuppressWarnings("restriction")
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
				scheduleLaunch(file, mode);
			}
		}
	}

	@Override
	public void launch(IEditorPart editor, final String mode) {
		Object adaptor = editor.getEditorInput().getAdapter(IFile.class);
		if (adaptor instanceof IFile) {
			final IFile file = (IFile) adaptor;
			scheduleLaunch(file, mode);
		}
	}

	private void scheduleLaunch(final IFile file, final String mode) {
		Job job = new Job("Start M Debug") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				MLaunchShortcut.this.run(file, mode);
				return Status.OK_STATUS;
			}
		};
		job.schedule();		
	}
	
	private ILaunchConfiguration[] getConfigurations(IFile file) throws CoreException {
		LaunchConfigurationManager lcm = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchConfiguration[] configs = lcm.getApplicableLaunchConfigurations(new String[]{CONFIG_TYPE}, file);
		return configs;
	}
	
	private void launchFromConfigurations(ILaunchConfiguration[] configs, String mode) throws CoreException {
		ILaunchConfiguration config = (configs.length == 1) ? configs[0] : this.selectConfiguration(configs, mode);
		if (config != null) {
			config.launch(mode, null);
		}		
	}
	
	private ILaunchConfiguration selectConfiguration(final ILaunchConfiguration[] configs, final String mode) {
		final IDebugModelPresentation renderer = DebugUITools.newDebugModelPresentation();
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(null, renderer);
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialog.setElements(configs);
				dialog.setTitle("M " + mode + " configurations");
				dialog.setMessage("Select an M " + mode + " configuration");
				dialog.setMultipleSelection(false);
				dialog.open();
				renderer.dispose();
			}
		});
		if (dialog.getReturnCode() == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;	
	}
		
	public void run(IFile file, String mode) {
		try {
			ILaunchConfiguration[] configs = this.getConfigurations(file);
			if ((configs != null) && (configs.length > 0)) {
				this.launchFromConfigurations(configs, mode);
				return;
			}
			
			Vector<String> tags = null;
			try {
				tags = getTags(file);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String tag = selectTag(tags, mode);
			if (tag == null) {
				return;
			} else {
				String fileName = file.getName().split("\\.")[0];
				run(fileName, tag, mode);
				return;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private String selectTag(final Vector<String> tags, final String mode) {
		final IDebugModelPresentation renderer = DebugUITools
				.newDebugModelPresentation();
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				null, renderer);
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				dialog.setElements(tags.toArray());
				dialog.setTitle("Select M Tag");
				dialog.setMessage((new StringBuilder("Select M Tag to ")).append(mode).append(":").toString());
				dialog.setMultipleSelection(false);
				dialog.open();
				renderer.dispose();
			}
		});
		if (dialog.getReturnCode() == Window.OK) {
			return (String) dialog.getFirstResult();
		}
		return null;
	}

	public void run(String routine, String tag, String mode) {
		if (null == routine || null == tag)
			return;
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = manager.getLaunchConfigurationType(CONFIG_TYPE);
		ILaunchConfigurationWorkingCopy wc;
		try {
			String command = "D " + tag + "^" + routine;
			wc = configType.newInstance(null, manager.generateLaunchConfigurationName(routine));
			wc.setAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, command);
			ILaunchConfiguration config = wc.doSave();
			config.launch(mode, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static Vector<String> getTags(IFile routineFile) throws CoreException, IOException {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(routineFile.getContents()));
		} catch (ResourceException e) {
			throw e;
		}
		Vector<String> tags = new Vector<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			int ichr = 0;
			StringBuilder tag = new StringBuilder();
			for (; ichr < line.length() && line.charAt(ichr) != '\t' && line.charAt(ichr) != ' ' && line.charAt(ichr) != '('; ichr++)
				tag.append(line.charAt(ichr));

			if (!tag.toString().equals(""))
				tags.add(tag.toString());
		}
		reader.close();
		return tags;
	}

}