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

package us.pwc.vista.eclipse.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

public class CommonUtil {
	public static void showException(String pluginId, String title, Throwable throwable) {
		IStatus status = new Status(IStatus.ERROR, pluginId, throwable.getMessage(), throwable);
		if ((title == null) || (title.isEmpty())) {
			StatusManager.getManager().handle(status, StatusManager.SHOW);
		} else {
			StatusAdapter sa = new StatusAdapter(status);
			sa.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, title);
			StatusManager.getManager().handle(sa, StatusManager.SHOW);	
		}
	}
}
