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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;

public enum UpdateFileResult {
	UPDATED(1000, Messages.ROUTINE_UPDATED_IN_PROJECT),
	CREATED(1001, Messages.ROUTINE_CREATED_IN_PROJECT),
	IDENTICAL(1002, Messages.ROUTINE_IDENTICAL_IN_PROJECT);
	
	private int code;
	private String messageKey;
	
	private UpdateFileResult(int code, String messageKey) {
		this.code = code;
		this.messageKey = messageKey;
	}
	
	public IStatus toStatus(IFile file) {
		String path = file.getFullPath().toString();
		String message = Messages.bind(this.messageKey, path);
		IStatus status = new Status(IStatus.INFO, VistAServerPlugin.PLUGIN_ID, code, message, null);
		return status;
	}
}
