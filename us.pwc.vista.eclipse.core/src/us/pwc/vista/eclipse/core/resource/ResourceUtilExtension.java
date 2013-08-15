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

package us.pwc.vista.eclipse.core.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

import us.pwc.vista.eclipse.core.Messages;
import us.pwc.vista.eclipse.core.VistACorePlugin;

/**
 * Contains static methods for manipulating Eclipse resources 
 * <code>IProject</code>, <code>IFile</code>, and <code>IFolder</code>.
 */
public class ResourceUtilExtension {
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
	
	/** Returns the routine name for a specific extension.
	 * 
	 * @param file
	 * @return routine name.
	 * @throws InvalidFileException when file does not end with the extension.                                                              
	 */	
	public static String getRoutineName(IFile file, String extension) throws InvalidFileException {
		String fileName = file.getName();
		String fileEnd = '.' + extension;
		if (! fileName.endsWith(fileEnd)) {
			String message = Messages.bind(Messages.UNEXPECTED_FILE_EXT, extension);
			throw new InvalidFileException(message);
		}
		int length = fileName.length();
		String routineName = fileName.substring(0, length-2);
		return routineName;
	}

	/**
	 * Converts all tabs to space, removes all control characters, and removes
	 * all empty lines.  This method also makes sure that all the end of line
	 * characters are consistent. 
	 * 
	 * @param source code.
	 * @param target updated code.
	 * @return if any update needed.
	 * @throws BadLocationException
	 */
	public static boolean cleanCode(IDocument source, IRoutineBuilder target) throws BadLocationException { 
        boolean result = false; 
		String eol = ((IDocumentExtension4) source).getDefaultLineDelimiter();
        int n = source.getNumberOfLines(); 
        for (int i=0; i<n; ++i) { 
        	IRegion lineInfo = source.getLineInformation(i); 
        	int lineLength = lineInfo.getLength();
        	boolean emptyLine = (lineLength == 0); 
        	result = result || (emptyLine && (source.getLineLength(i) > 0)); 
        	if (! emptyLine) { 
        		int offset = lineInfo.getOffset(); 
        		String lineText = source.get(offset, lineLength); 
        		if (lineText.indexOf('\t') >= 0) { 
        			lineText = lineText.replace('\t', ' '); 
        			result = true; 
        		}        		
        		lineText = lineText.replaceAll("\\p{Cntrl}", "");        		
        		result = result || (lineText.length() != lineLength);
        		if (lineText.trim().isEmpty()) {
        			result = true;
        			continue;
        		}
        		String eolLine = source.getLineDelimiter(i);
        		if (! eol.equals(eolLine)) {
        			result = true;
        		}
        		target.appendLine(lineText, eol);
        	} 
        } 
        return result;
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
	
	public static IFolder getFolder(TreePath path) {
		Object lastSegment = path.getLastSegment();			
		if (lastSegment instanceof IFolder) {
			return (IFolder) lastSegment;
		} else {
			return null;
		}
	}
	
	public static FileFillState getSelectedFiles(TreePath[] selections, IResourceFilter filter) throws CoreException {
		FileFillState result = new FileFillState(filter);
		for (TreePath path : selections) {
			Object lastSegment = path.getLastSegment();			
			if (lastSegment instanceof IResource) {
				IResource selected = (IResource) lastSegment;
				result.add(selected);
			} else {
				String message = Messages.bind(Messages.UNEXPECTED_OBJECT, lastSegment.getClass().getName());
				IStatus status = new Status(IStatus.ERROR, VistACorePlugin.PLUGIN_ID, message);
				throw new CoreException(status);
			}
		}
		return result;
	}
}
