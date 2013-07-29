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

package gov.va.med.iss.meditor.command.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * Contains static methods for manipulating Eclipse resources 
 * <code>IProject</code>, <code>IFile</code>, and <code>IFolder</code>.
 */
public class ResourceUtilsExtension {
	/** Returns the end of file string that Eclipse is configured to used for
	 *  the project.
	 * 
	 * @param project
	 * @return end of file string to be used by new files.                                                              
	 */		
	public static String getLineSeperator(IProject project) {
		IScopeContext[] scopeContext = new IScopeContext[]{new ProjectScope(project)};
		String lineSeparator = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
		if (lineSeparator != null) {
			return lineSeparator;
		} else {
			return System.getProperty("line.separator");
		}
	}

	/** Returns the document associated with a file.  Documents provide
	 *  methods for text manipulation and are useful to compare an validate 
	 *  file content.
	 * 
	 * @param file
	 * @return document for the file.                                                              
	 */		
	public static IDocument getDocument(IFile file) throws CoreException {
		IDocumentProvider provider = new TextFileDocumentProvider();
		provider.connect(file);
		IDocument document = provider.getDocument(file);
		provider.disconnect(file);
		return document;
	}
	
	public static void prepareFolders(IContainer container) throws CoreException {
	    if (! container.exists()) {
	        prepareFolders(container.getParent());
	        IFolder folder = (IFolder) container;
	        folder.create(true, false, null);
	    }
	}
	
	public static void updateFile(IFile file, String newContent) throws CoreException, UnsupportedEncodingException {
		InputStream source = new ByteArrayInputStream(newContent.getBytes("UTF-8"));		
		if (file.exists()) {
			file.setContents(source, true, true, null);
		} else {
			prepareFolders(file.getParent());
			file.create(source, true, null);
		}
	}
}
