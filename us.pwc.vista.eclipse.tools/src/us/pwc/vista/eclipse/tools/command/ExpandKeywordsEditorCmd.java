//---------------------------------------------------------------------------
// Copyright 2013 PwC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package us.pwc.vista.eclipse.tools.command;

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

import com.pwc.us.rgi.m.struct.MRefactorSettings;
import com.pwc.us.rgi.m.struct.MRoutineContent;
import com.pwc.us.rgi.m.token.MRoutine;
import com.pwc.us.rgi.m.token.MTFSupply;
import com.pwc.us.rgi.m.token.MVersion;
import com.pwc.us.rgi.m.token.TFRoutine;

public class ExpandKeywordsEditorCmd extends AbstractHandler {
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
