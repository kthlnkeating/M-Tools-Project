package us.pwc.eclipse.vista.toolconsole;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import com.raygroupintl.m.tool.SourceCodeFiles;

public class MToolsPatternMatchListener implements IPatternMatchListener {
	private SourceCodeFiles scf;
	private IProject project;
	private IWorkbenchWindow window;

	public MToolsPatternMatchListener(IProject project, IWorkbenchWindow window, SourceCodeFiles scf) {
		this.project = project;
		this.window = window;
		this.scf = scf;		
	}
	
	@Override
	public int getCompilerFlags() {
		return 0;
	}
	 
	@Override
	public String getLineQualifier() {
		return null;
	}

	@Override
	public String getPattern() {
		return "\\([\\w%]+\\:\\d+\\)"; //TODO: convert this returning a precompile regex for optimization
	}
	
	@Override
	public void connect(TextConsole console) {
	}

	@Override
	public void disconnect() {		
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		Object source = event.getSource();
		if ((source != null) && (source instanceof TextConsole)) {
			TextConsole console = (TextConsole) source;
			int offset = event.getOffset();
			int length = event.getLength();
			IDocument document = console.getDocument();
			if (document != null) {
				try {
					String matchedWithParantheses = document.get(offset, length);
					String matched = matchedWithParantheses.substring(1, matchedWithParantheses.length()-1);					
					String[] routineInfo = matched.split(":");					
					String routineName = routineInfo[0];
					int lineNumber = Integer.parseInt(routineInfo[1]);
					String filePath = this.scf.get(routineName);
					IPath path = Path.fromOSString(filePath);
					IFile file = (IFile) this.project.findMember(path);
					MFileLink link = new MFileLink(this.window, file, lineNumber+1);
					console.addHyperlink(link, offset, length);
				} catch (BadLocationException e){
					return;
				}
			}
		}
	}
}