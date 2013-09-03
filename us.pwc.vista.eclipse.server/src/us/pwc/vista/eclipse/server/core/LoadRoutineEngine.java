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

package us.pwc.vista.eclipse.server.core;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;

import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;

import gov.va.med.iss.connection.VistAConnection;

public class LoadRoutineEngine {
	private static CommandResult<MServerRoutine> loadRoutine(MServerRoutine serverRoutine) throws CoreException, BadLocationException, UnsupportedEncodingException {
		String routineName = serverRoutine.getRoutineName();
		if (! serverRoutine.isLoaded()) {
			String message = Messages.bind(Messages.ROUTINE_NOT_ON_SERVER, routineName);
			IStatus status = new Status(IStatus.ERROR, VistAServerPlugin.PLUGIN_ID, message);
			return new CommandResult<MServerRoutine>(serverRoutine, status);
		}		
		UpdateFileResult result = serverRoutine.updateClient();
		IFile file = serverRoutine.getFileHandle();
		IStatus status = result.toStatus(file);
		return new CommandResult<MServerRoutine>(serverRoutine, status);
	}
	
	public static CommandResult<MServerRoutine> loadRoutine(VistAConnection vistaConnection, IFile file) {
		try {			
			MServerRoutine serverRoutine = MServerRoutine.load(vistaConnection, file);
			return loadRoutine(serverRoutine);
		} catch (Throwable t) {
			IStatus status = new Status(IStatus.ERROR, VistAServerPlugin.PLUGIN_ID, t.getMessage(), t);
			return new CommandResult<MServerRoutine>(null, status);					
		}	
	}
}
