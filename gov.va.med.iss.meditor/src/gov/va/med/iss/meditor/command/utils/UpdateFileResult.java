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

package gov.va.med.iss.meditor.command.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;

import gov.va.med.iss.meditor.Messages;

public enum UpdateFileResult {
	UPDATED(StatusHelper.CODE_PROJECT_FILE_UPDATED, Messages.ROUTINE_UPDATED_IN_PROJECT),
	CREATED(StatusHelper.CODE_PROJECT_FILE_CREATED, Messages.ROUTINE_CREATED_IN_PROJECT),
	IDENTICAL(StatusHelper.CODE_PROJECT_FILE_IDENTICAL, Messages.ROUTINE_IDENTICAL_IN_PROJECT);
	
	private int code;
	private String messageKey;
	
	private UpdateFileResult(int code, String messageKey) {
		this.code = code;
		this.messageKey = messageKey;
	}
	
	public IStatus toStatus(IFile file) {
		String path = file.getFullPath().toString();
		IStatus status = StatusHelper.getStatus(this.code, IStatus.INFO, this.messageKey, path);
		return status;
	}
}
