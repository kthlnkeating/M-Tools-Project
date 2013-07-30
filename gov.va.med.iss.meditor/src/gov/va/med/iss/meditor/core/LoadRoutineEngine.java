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

package gov.va.med.iss.meditor.core;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.error.MEditorException;

public class LoadRoutineEngine {
	private static CommandResult<MServerRoutine> loadRoutine(MServerRoutine serverRoutine) throws CoreException, BadLocationException, UnsupportedEncodingException {
		String routineName = serverRoutine.getRoutineName();
		if (! serverRoutine.isLoaded()) {
			IStatus status = StatusHelper.getStatus(IStatus.ERROR, Messages.ROUTINE_NOT_ON_SERVER, routineName);
			return new CommandResult<MServerRoutine>(serverRoutine, status);
		}		
		UpdateFileResult result = serverRoutine.updateClient();
		IFile file = serverRoutine.getFileHandle();
		IStatus status = result.toStatus(file);
		return new CommandResult<MServerRoutine>(serverRoutine, status);
	}
	
	private static CommandResult<MServerRoutine> getKnownExceptionResult(MEditorException exception) {
		IStatus status = StatusHelper.getStatus(exception);
		return new CommandResult<MServerRoutine>(null, status);					
	}
	
	private static CommandResult<MServerRoutine> getUnknownExceptionResult(Throwable t) {
		String message = Messages.bind(Messages.UNEXPECTED_INTERNAL, t.getMessage());
		IStatus status = StatusHelper.getStatus(message, t);
		return new CommandResult<MServerRoutine>(null, status);			
	}
		
	public static CommandResult<MServerRoutine> loadRoutine(VistaLinkConnection connection, IProject project, String routineName) {
		try {			
			MServerRoutine serverRoutine = MServerRoutine.load(connection, project, routineName);
			return loadRoutine(serverRoutine);
		} catch(MEditorException mee) {
			return getKnownExceptionResult(mee);
		} catch (Throwable t) {
			return getUnknownExceptionResult(t);
		}	
	}

	public static CommandResult<MServerRoutine> loadRoutine(VistaLinkConnection connection, IFile file) {
		try {			
			MServerRoutine serverRoutine = MServerRoutine.load(connection, file);
			return loadRoutine(serverRoutine);
		} catch(MEditorException mee) {
			return getKnownExceptionResult(mee);
		} catch (Throwable t) {
			return getUnknownExceptionResult(t);
		}	
	}
}
