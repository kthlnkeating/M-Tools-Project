package us.pwc.eclipse.vista.command;

import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.raygroupintl.m.struct.MRefactorSettings;
import com.raygroupintl.m.struct.MRoutineContent;
import com.raygroupintl.m.token.MRoutine;
import com.raygroupintl.m.token.MTFSupply;
import com.raygroupintl.m.token.MVersion;
import com.raygroupintl.m.token.TFRoutine;

public class ExpandKeywords extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
		IEditorInput input = editorPart.getEditorInput();
		IFile file = (IFile) input.getAdapter(IFile.class);
		String fileName = file.getName();
		try {
			ITextFileBufferManager mgr = FileBuffers.getTextFileBufferManager();
			ITextFileBuffer buffer = mgr.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			if (buffer != null) {	
				InputStream is = file.getContents();
				MTFSupply m = MTFSupply.getInstance(MVersion.CACHE);
				TFRoutine tf = new TFRoutine(m);
				String routineName = fileName.split("\\.m")[0];
				MRoutineContent content = MRoutineContent.getInstance(routineName, is);
				MRoutine r = tf.tokenize(content);
				is.close();
				MRefactorSettings settings = new MRefactorSettings();
				r.refactor(settings);
				String v = r.toValue().toString();
				buffer.getDocument().set(v);							
			}
		} catch (Exception t) {
			String msg ="Unexpected error beautifying " + fileName + " :";
			Shell shell = HandlerUtil.getActiveShell(event);
			MessageDialog.openInformation(shell, "Vista", msg);
		}
		return null;
	}
}
