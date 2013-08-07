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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

import com.pwc.us.rgi.m.parsetree.data.EntryId;
import com.pwc.us.rgi.m.tool.OutputFlags;
import com.pwc.us.rgi.m.tool.ParseTreeSupply;
import com.pwc.us.rgi.m.tool.SourceCodeFiles;
import com.pwc.us.rgi.m.tool.ToolResult;
import com.pwc.us.rgi.output.OSTerminal;
import com.pwc.us.rgi.output.Terminal;
import com.pwc.us.rgi.output.TerminalFormatter;

import us.pwc.eclipse.vista.core.ToolExecuter;
import us.pwc.eclipse.vista.core.ToolExecuterOnRoutines;
import us.pwc.eclipse.vista.core.ToolExecuterOnTags;
import us.pwc.eclipse.vista.toolconsole.MToolsConsoleHandler;
import us.pwc.eclipse.vista.toolconsole.MToolsPatternMatchListener;

public abstract class MToolsCommand extends AbstractHandler{
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
	
	protected OutputFlags getOutputFlags() {
		OutputFlags fs = new OutputFlags();
		fs.setSkipEmpty(true);
		return fs;
	}
	
	protected void updateFormat(TerminalFormatter formatter) {
		formatter.setTitleWidth(12);
	}
		
	public void writeResult(IProject project, IWorkbenchWindow window, ToolResult result, SourceCodeFiles scf) throws IOException {
		OutputFlags flags = this.getOutputFlags();
		MessageConsole console = MToolsConsoleHandler.getMessageConsole();
		IPatternMatchListener listener = new MToolsPatternMatchListener(project, window, scf);
		console.addPatternMatchListener(listener);
		console.clearConsole();
		MessageConsoleStream os = console.newMessageStream();
		Terminal t = new OSTerminal(os);
		this.updateFormat(t.getTerminalFormatter());
		result.write(t, flags);	
		MToolsConsoleHandler.displayMToolsConsole();
	}
	
	public abstract ToolResult getResult(IProject project, ParseTreeSupply pts, List<String> selectedFileNames);

	public abstract ToolResult getResult(IProject project, ParseTreeSupply pts, EntryId entryId);

	/**
	 * Runs only for a given tag. This is for right clicking on the outline view.
	 * 
	 * @param window
	 * @param shell
	 * @param project
	 * @param file
	 * @param tag
	 */
	public void run(IWorkbenchWindow window, final ExecutionEvent event, IProject project, IFile file, String tag) {
		try {
			String name = file.getName();
			if (name.endsWith(".m")) {
				name = name.substring(0, name.length()-2);
				EntryId entryId = new EntryId(name, tag);
				ToolExecuter executer = new ToolExecuterOnTags(this, event, entryId);
				executer.run(project);
			}
		} catch (Exception e) {
			e.printStackTrace();
			final String msg = "Unexpected error.";
			Display.getDefault().asyncExec(new Runnable() {
				  public void run() {
					  MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "M Tools", msg);
				}
			});
		}
	}

	public Object executeFiles(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection ts = (TreeSelection) selection;
			TreePath[] paths = ts.getPaths();
			TreePath[] selections = paths;
			TreePath path = paths[paths.length-1];
			Object lastSegment = path.getLastSegment();
			if (lastSegment instanceof IResource) {
				IProject project = ((IResource) lastSegment).getProject();
				List<String> selectedFileNames = this.getFileNames(selections);
				if (selectedFileNames == null) {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "M Tools", "No file is selected.");
				}
				else {
					ToolExecuter executer = new ToolExecuterOnRoutines(this, event, selectedFileNames);
					executer.run(project);
				}
			}
		}
		return null;
	}
	
	public Object executeEditorFile(ExecutionEvent event) throws ExecutionException {
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IEditorInput input = editorPart.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		IProject project = file.getProject();
		List<String> selectedRoutines = new ArrayList<String>();
		String routineName = file.getLocation().lastSegment();
		if (routineName.contains(".")) {
			// although the plugin.xml will prevent non M Editors
			// from seeing/using this command. It is possible in
			// Eclipse to open an arbitrary in any editor
			routineName = routineName.split("\\.")[0];
		}
		selectedRoutines.add(routineName);
		ToolExecuter executer = new ToolExecuterOnRoutines(this, event, selectedRoutines);
		executer.run(project);
		return null;
	}
	
	public Object executeTags(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection ts = (TreeSelection) selection;
			TreePath[] paths = ts.getPaths();
			TreePath path = paths[paths.length-1];
			Object lastSegment = path.getLastSegment();
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			IEditorInput input = editorPart.getEditorInput();
			IFile file = (IFile) input.getAdapter(IFile.class);
			String tag = lastSegment.toString();
			if (! (tag.startsWith(" ") || tag.startsWith(">"))) {
				IProject project = file.getProject();
				tag = tag.replace((char)10, (char)0); //remove the newlines that come in from the outline selection
				tag = tag.replace((char)13, (char)0);
				String name = file.getName();
				if (name.endsWith(".m")) {
					name = name.substring(0, name.length()-2);
					EntryId entryId = new EntryId(name, tag);
					ToolExecuter executer = new ToolExecuterOnTags(this, event, entryId);
					executer.run(project);
				}
			}
		}
		return null;
	}
}
