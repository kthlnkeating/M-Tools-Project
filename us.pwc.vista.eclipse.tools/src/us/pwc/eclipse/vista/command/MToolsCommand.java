package us.pwc.eclipse.vista.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

import us.pwc.eclipse.vista.toolconsole.MToolsConsoleHandler;
import us.pwc.eclipse.vista.toolconsole.MToolsPatternMatchListener;
import us.pwc.eclipse.vista.util.MRAParamSupply;

import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.tool.OutputFlags;
import com.raygroupintl.m.tool.ParseTreeSupply;
import com.raygroupintl.m.tool.SourceCodeFiles;
import com.raygroupintl.m.tool.SourceCodeToParseTreeAdapter;
import com.raygroupintl.m.tool.ToolResult;
import com.raygroupintl.output.OSTerminal;
import com.raygroupintl.output.Terminal;
import com.raygroupintl.output.TerminalFormatter;

abstract class MToolsCommand extends AbstractHandler{
	protected List<String> getFileNames(TreePath[] selections) {
		if (selections == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (TreePath path : selections) {
			Object lastSegment = path.getLastSegment();
			if (lastSegment instanceof IFile) {
				IFile selected = (IFile) lastSegment;
				String name = selected.getName();
				if (name.endsWith(".m")) {
					name = name.substring(0, name.length()-2);
					result.add(name);
				}
			}
		}
		if (result.size() == 0) {
			return null;
		} else {
			return result;
		}
	}
	
	protected abstract ToolResult getResult(ParseTreeSupply pts, List<String> selectedFileNames);

	protected abstract ToolResult getResult(ParseTreeSupply pts, EntryId entryId);

	protected OutputFlags getOutputFlags() {
		OutputFlags fs = new OutputFlags();
		fs.setSkipEmpty(true);
		return fs;
	}
	
	protected void updateFormat(TerminalFormatter formatter) {
		formatter.setTitleWidth(12);
	}
	
	private void writeResult(IProject project, IWorkbenchWindow window, ToolResult result, SourceCodeFiles scf) throws IOException {
		OutputFlags flags = this.getOutputFlags();
		MessageConsole console = MToolsConsoleHandler.getMessageConsole();
		IPatternMatchListener listener = new MToolsPatternMatchListener(project, window, scf);
		console.addPatternMatchListener(listener);
		console.clearConsole();
		MessageConsoleStream os = console.newMessageStream();
		Terminal t = new OSTerminal(os);  //MToolsConsoleOSTerminal(this.project, this.window, os, scf);
		this.updateFormat(t.getTerminalFormatter());
		result.write(t, flags);	
		MToolsConsoleHandler.displayMToolsConsole();
	}
	
	/**
	 * Runs for a file collection of files
	 * 
	 * @param window
	 * @param shell
	 * @param project
	 * @param selections
	 */
	public void run(IWorkbenchWindow window, final Shell shell, IProject project, List<String> fileNames) {
		try {

			SourceCodeFiles scf = MRAParamSupply.getSourceCodeFiles(project);
			SourceCodeToParseTreeAdapter pts = new SourceCodeToParseTreeAdapter(scf);
			
			ToolResult result = this.getResult(pts, fileNames);
			this.writeResult(project, window, result, scf);
		} catch (Exception e) {
			e.printStackTrace();
			final String msg = "Unexpected error.";
			Display.getDefault().asyncExec(new Runnable() {
				  public void run() {
					  MessageDialog.openInformation(shell, "M Tools", msg);
				  }
				});
		}
	}

	/**
	 * Runs only for a given tag. This is for right clicking on the outline view.
	 * 
	 * @param window
	 * @param shell
	 * @param project
	 * @param file
	 * @param tag
	 */
	public void run(IWorkbenchWindow window, final Shell shell, IProject project, IFile file, String tag) {
		try {
			SourceCodeFiles scf = MRAParamSupply.getSourceCodeFiles(project);
			SourceCodeToParseTreeAdapter pts = new SourceCodeToParseTreeAdapter(scf);
			String name = file.getName();
			if (name.endsWith(".m")) {
				name = name.substring(0, name.length()-2);
				EntryId entryId = new EntryId(name, tag);
				ToolResult result = this.getResult(pts, entryId);
				this.writeResult(project, window, result, scf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			final String msg = "Unexpected error.";
			Display.getDefault().asyncExec(new Runnable() {
				  public void run() {
					  MessageDialog.openInformation(shell, "M Tools", msg);
				}
			});
		}
	}

	
	/**
	 * This occurs in the main/UI eclipse thread. It schedules a new 
	 * asynchronous job to do the heavy lifting.
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final MToolsCommand this1 = this;
		
		Job job = new Job("MTools Fetch Report") {
			@Override
			protected IStatus run(IProgressMonitor monitor) { //TODO: the plugin.xml should only show the M Tools menu when M files are selected only, so no validations need to be done.

				ISelection selection = HandlerUtil.getCurrentSelection(event);
				if (selection instanceof TreeSelection) {
					TreeSelection ts = (TreeSelection) selection;
					TreePath[] paths = ts.getPaths();
					TreePath[] selections = paths;
					TreePath path = paths[paths.length-1];
					Object lastSegment = path.getLastSegment();
					if (lastSegment instanceof IResource) {
						IProject project = ((IResource) lastSegment).getProject();
						IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
						Shell shell = HandlerUtil.getActiveShell(event);
						
						List<String> selectedFileNames = this1.getFileNames(selections);
						if (selectedFileNames == null) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openInformation(Display
											.getCurrent().getActiveShell(),
											"M Tools", "No file is selected.");
								}
							});
							return Status.CANCEL_STATUS;			
						}
						
						this1.run(window, shell, project, selectedFileNames);
					} else {
						IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
						IEditorInput input = editorPart.getEditorInput();
						IFile file = (IFile) input.getAdapter(IFile.class);
						String tag = lastSegment.toString();
						if (! (tag.startsWith(" ") || tag.startsWith(">"))) {
							IProject project = file.getProject();
							IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
							Shell shell = HandlerUtil.getActiveShell(event);
							tag = tag.replace((char)10, (char)0); //remove the newlines that come in from the outline selection
							tag = tag.replace((char)13, (char)0);
							this1.run(window, shell, project, file, tag.trim());
						}
					}
				} else if (selection instanceof TextSelection) { //assume it was invoked from the M Editor
					IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
					IEditorInput input = editorPart.getEditorInput();
					IFile file = (IFile) input.getAdapter(IFile.class);

					IProject project = file.getProject();
					IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
					Shell shell = HandlerUtil.getActiveShell(event);
					List<String> selectedRoutines = new ArrayList<String>();
					String routineName = file.getLocation().lastSegment();
					if (routineName.contains(".")) {
						// although the plugin.xml will prevent non M Editors
						// from seeing/using this command. It is possible in
						// Eclipse to open an arbitrary in any editor
						routineName = routineName.split("\\.")[0];
					}
					selectedRoutines.add(routineName);
					this1.run(window, shell, project, selectedRoutines);
				}

				return Status.OK_STATUS;
			}
		};
		
		job.schedule();

		return null;
	}
}
