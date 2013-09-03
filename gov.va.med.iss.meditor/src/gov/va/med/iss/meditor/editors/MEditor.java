package gov.va.med.iss.meditor.editors;

import gov.va.med.iss.connection.VistAConnection;
import gov.va.med.iss.connection.VLConnectionPlugin;
import gov.va.med.iss.meditor.MEditorDocumentProvider;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.MEditorSourceViewerConfiguration;
import gov.va.med.iss.meditor.Messages;
import gov.va.med.iss.meditor.m.MCodeScanner;
import gov.va.med.iss.meditor.preferences.MEditorPreferencesPage;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import us.pwc.vista.eclipse.core.helper.MessageDialogHelper;
import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;
import us.pwc.vista.eclipse.server.core.SaveRoutineEngine;
import us.pwc.vista.eclipse.server.core.StringRoutineBuilder;

/**
 *	This class is responsible for configuring the M editor.
 *  
 */
public class MEditor extends TextEditor {
	private static final String MESSAGE_TITLE = "MEditor";
	
	private static MCodeScanner fMCodeScanner;
	private MContentOutlinePage outlinePage = null;
	private static MEditorSourceViewerConfiguration meditorSourceViewerConfiguration = null;
	private static boolean wordWrap = false;
	
	/**
	 * Constructor for MEditor. Intialization takes place in the constructor 
	 * of MEditor using setDocumentProvider and setRangeIndicator.
	 */
	public MEditor() {
		super();
		setDocumentProvider(new MEditorDocumentProvider());
		updateSourceViewerConfiguration();
		fMCodeScanner = new MCodeScanner();
		setRangeIndicator(new DefaultRangeIndicator());
		new MEditorPreferencesPage(); //This is invoked so that preferences are set to their default values in case this is the first time running MEditor
		String wordWrapValue = MEditorPrefs.getPrefs(MEditorPlugin.P_WRAP_LINES);
		if (wordWrapValue.compareTo("true") == 0)
			wordWrap = true;
		else
			wordWrap = false;
		this.setWordWrap();
	}
	/**
	 * Clean up the font colors used in the syntax hilighting
	 *  
	 */
	public void disposeColorProvider() {
		MEditorPlugin.getDefault().disposeColorProvider();
		super.dispose();
	}
	
	public void setWordWrap() {
		ISourceViewer viewer = this.getSourceViewer();
		if (viewer != null) {
			StyledText text = viewer.getTextWidget();
			text.setWordWrap(wordWrap);
		}
	}
	
	public static void setWordWrapValue(boolean wrap) {
		wordWrap = wrap;
	}
	
	/**
	 * Getter method that returns a  M Code Scanner.
	 * @return MCodeScanner
	 */
	public static MCodeScanner getMCodeScanner() {
		return fMCodeScanner;
	}

	private IFile getFile() {
		IEditorInput input = this.getEditorInput();
		IFile file =  ResourceUtil.getFile(input);
		if (file == null) {			
			IStatus status = new Status(IStatus.ERROR, MEditorPlugin.PLUGIN_ID, Messages.UNEXPECTED_EDITOR_FILE_NULL);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		}
		return file;
	}
	
	private void updateCode(IDocument document) throws BadLocationException {
		StringRoutineBuilder srb = new StringRoutineBuilder();
		boolean updated = ResourceUtilExtension.cleanCode(document, srb);
		if (updated) {
        	document.set(srb.getRoutine()); 
        }         
	} 
	
	private boolean cleanCode() {
		IEditorInput input = this.getEditorInput();
		IDocumentProvider documentProvider = this.getDocumentProvider();
		IDocument document = documentProvider.getDocument(input);
		try {
			this.updateCode(document);			
		} catch (BadLocationException e) {
			IStatus status = new Status(IStatus.ERROR, MEditorPlugin.PLUGIN_ID, e.getMessage(), e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return false;
		}
		return true;
	}
	
	
	/** The <code>MEditor</code> implementation of this <code>TextEditor</code>
	 *  method performs any extra behavior required by the M Editor.
	 *  <code>MEditor</code> always remove control characters and empty lines
	 *  before saving the file.  Once the file is saved, based on preference
	 *  the M Server routine is also updated.                                                                                                     
	 *  <p>
	 *  MEditor always saves the server version of the file in the backup
	 *  directory before updating the file.
	 *  
	 * @param monitor the progress monitor.                                                            
	 */
	public void doSave(IProgressMonitor monitor) {
		if (! this.cleanCode()) {
			return;
		}
		
		super.doSave(monitor);
		if (! MEditorPrefs.getAutoSaveToServer()) {
			return;
		}
		
		IFile file = this.getFile();
		if (file == null) {
			return;
		}

		VistAConnection vistaConnection = VLConnectionPlugin.getConnectionManager().getConnection(file.getProject());
		if (vistaConnection == null) {
			return;
		}
		
		IStatus result = SaveRoutineEngine.save(vistaConnection, file);
		MessageDialogHelper.logAndShow(MESSAGE_TITLE, result);
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			if (outlinePage == null) {
				outlinePage = new MContentOutlinePage(getDocumentProvider(), this, this);
				if (getEditorInput() != null)
					outlinePage.setInput(getEditorInput());
			}
			return outlinePage;
		}
		return super.getAdapter(key);
	}
	
	private void updateSourceViewerConfiguration() {
		meditorSourceViewerConfiguration = null;
		meditorSourceViewerConfiguration = new MEditorSourceViewerConfiguration();
		setSourceViewerConfiguration(meditorSourceViewerConfiguration);
	}
	
	public void updateAfterPreferencesChanged() {
		this.updateSourceViewerConfiguration();
		this.getSourceViewer().invalidateTextPresentation();
	}	
}