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

package gov.va.med.iss.meditor.command;

import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.dialog.MessageDialogHelper;
import gov.va.med.iss.meditor.editors.MEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

public class CommandCommon {
	public static boolean openEditor(ExecutionEvent event, IFile file) {	
		try {
			IWorkbenchWindow w = HandlerUtil.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = w.getActivePage();
			FileEditorInput editorInput = new FileEditorInput(file);
			page.openEditor(editorInput, MEditor.M_EDITOR_ID);		
			return true;
		} catch (Throwable t) {
			String message = Messages.bind(Messages.UNABLE_OPEN_EDITOR, file.getName());
			MessageDialogHelper.logAndShow(message, t);
			return false;
		}
	}
}
