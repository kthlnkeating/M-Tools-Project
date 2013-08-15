package gov.va.med.iss.meditor.editors;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
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
import us.pwc.vista.eclipse.server.core.SaveRoutineEngine;
import us.pwc.vista.eclipse.server.core.StringRoutineBuilder;
import us.pwc.vista.eclipse.server.resource.ResourceUtilExtension;

/**
 *	This class is responsible for configuring the M editor.
 *  
 */
public class MEditor extends TextEditor {
	private static final String MESSAGE_TITLE = "MEditor";
	
	private static MCodeScanner fMCodeScanner;
	private MContentOutlinePage outlinePage = null;
	public IDocumentProvider meditorDocumentProvider;
	public ISourceViewer sourceViewer = null;
	public static MEditorSourceViewerConfiguration meditorSourceViewerConfiguration = null;
	public static MEditor currMEditor = null;
	public static boolean wordWrap = false;
	public int newTopIndex = 0;
	public int newCaretOffset = 0;
	public static int oldTopIndex = 0;
	public static int oldCaretOffset = 0;
	private String routinePrimaryServer = "";
	
	/**
	 * Constructor for MEditor. Intialization takes place in the constructor 
	 * of MEditor using setDocumentProvider and setRangeIndicator.
	 */
	public MEditor() {
		super();
		setDocumentProvider(new MEditorDocumentProvider());
		meditorDocumentProvider = getDocumentProvider();
		updateSourceViewerConfiguration();
		fMCodeScanner = new MCodeScanner();
		setRangeIndicator(new DefaultRangeIndicator());
		new MEditorPreferencesPage(); //This is invoked so that preferences are set to their default values in case this is the first time running MEditor
		sourceViewer = getTheSourceViewer();
		currMEditor = this;
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
	
	public ISourceViewer getTheSourceViewer() {
		return super.getSourceViewer();
	}
	
	public int getTopIndex() {
		int val = getSourceViewer().getTextWidget().getTopIndex();
		return val;
	}
	
	public void setWordWrap() {
		//getSourceViewer().getTextWidget().setWordWrap(wordWrap);
		ISourceViewer viewer = this.getTheSourceViewer();
		if (viewer != null) {
			StyledText text = viewer.getTextWidget();
			text.setWordWrap(wordWrap);
		}
	}
	
	public static void setWordWrapValue(boolean wrap) {
		wordWrap = wrap;
	}
	
	public void setRoutinePrimaryServer(String primaryServer) {
		routinePrimaryServer = primaryServer;
	}
	
	public String getRoutineServer() {
		return routinePrimaryServer;
	}
	public int getCaretOffset() {
		int val = getSourceViewer().getTextWidget().getCaretOffset();
		return val;
	}
	
	public void setCaretOffset(int caretOffset) {
		getSourceViewer().getTextWidget().setCaretOffset(caretOffset);
	}
	
	public void update() {
		getSourceViewer().getTextWidget().update();
	}
	
	public void setTopIndex(int offset) {
		getSourceViewer().getTextWidget().setTopIndex(offset);
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

		VistaLinkConnection connection = VistaConnection.getConnection();
		if (connection == null) {
			return;
		}
		
		IStatus result = SaveRoutineEngine.save(connection, file);
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
	
	public void updateSourceViewerConfiguration() {
		meditorSourceViewerConfiguration = null;
		meditorSourceViewerConfiguration = new MEditorSourceViewerConfiguration();
		setSourceViewerConfiguration(meditorSourceViewerConfiguration);
		sourceViewer = getTheSourceViewer();
	}
}