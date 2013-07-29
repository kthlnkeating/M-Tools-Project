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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.mumps.meditor.MEditorException;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.command.utils.MServerRoutine;

public class CommandEngine {
	public static CommandResult<MServerRoutine> loadRoutine(VistaLinkConnection connection, IProject project, String routineName) {
		try {
			MServerRoutine serverRoutine = MServerRoutine.load(connection, project, routineName);
			if (! serverRoutine.isLoaded()) {
				IStatus status = MEditorPlugin.getDefault().getStatus(IStatus.ERROR, Messages.ROUTINE_NOT_ON_SERVER, routineName);
				return new CommandResult<MServerRoutine>(serverRoutine, status);
			}		
			boolean updated = serverRoutine.updateClient();
			IFile file = serverRoutine.getFileHandle();
			if (updated) {
				IStatus status = MEditorPlugin.getDefault().getStatus(IStatus.INFO, Messages.ROUTINE_UPDATED_IN_PROJECT, file.getFullPath().toString());
				return new CommandResult<MServerRoutine>(serverRoutine, status);
			}
			return new CommandResult<MServerRoutine>(serverRoutine, MEditorPlugin.getDefault().getOKStatus());
		} catch(MEditorException mee) {
			IStatus status = MEditorPlugin.getDefault().getStatus(mee);
			return new CommandResult<MServerRoutine>(null, status);			
		} catch (Throwable t) {
			String message = Messages.bind(Messages.UNEXPECTED_INTERNAL, t.getMessage());
			IStatus status = MEditorPlugin.getDefault().getStatus(message, t);
			return new CommandResult<MServerRoutine>(null, status);			
		}	
	}
}
