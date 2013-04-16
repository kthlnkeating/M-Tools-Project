package gov.va.med.iss.meditor.editors;
/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

//import java.io.File;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
//import java.awt.event.*;

//import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.preference.*;
//import org.eclipse.ui.texteditor.AbstractTextEditor;
//import org.eclipse.jface.text.IDocument;
//import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.custom.StyledText;
//import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
/*
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AddTaskAction;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
*/
import org.eclipse.ui.views.contentoutline.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchPart;

import gov.va.med.iss.meditor.m.MCodeScanner;
import gov.va.med.iss.meditor.MEditorDocumentProvider;
import gov.va.med.iss.meditor.MEditorSourceViewerConfiguration;
import gov.va.med.iss.meditor.MEditorPlugin;
//import gov.va.med.iss.meditor.actions.RoutineEditAction;
//import gov.va.med.iss.meditor.utils.MColorProvider;
import gov.va.med.iss.meditor.utils.RoutineSave;
import gov.va.med.iss.meditor.utils.RoutineLoad;
import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.editors.MEditor;
import gov.va.med.iss.meditor.preferences.MEditorPreferencesPage;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.MPiece;

/**
 *	This class is responsible for configuring the M editor.
 *  
 */
public class MEditor extends /* AbstractDecoratedTextEditor { // */ TextEditor { //AbstractTextEditor {
	
	public static final String M_EDITOR_ID = "gov.va.med.iss.meditor.editors.MEditor";
	
	private static MCodeScanner fMCodeScanner;
	private MContentOutlinePage outlinePage = null;
	private static String routineName = "";
    private static String oldCode = ""; // JLI 090915
	public IDocumentProvider meditorDocumentProvider;
	private static MEditorPreferencesPage meditorPreferencesPage;
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
		meditorPreferencesPage = new MEditorPreferencesPage(this);
		sourceViewer = getTheSourceViewer();
		currMEditor = this;
		String wordWrapValue = MEditorPrefs.getPrefs(MEditorPlugin.P_WRAP_LINES); //"mcode");
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
	
	public void setRoutineName(String rouName) {
		routineName = rouName;
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
	 * Method to install the editor actions.
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
	 * 
	 * Changes required in V 2.1. Shortcut keys on global actions must be explicitly set.  
	 * Content Assist and Context Information Shortcut keys must be set to the key 
	 * defintion ID's. 
	 */
	protected void createActions() {
		// Added in 2.1,  global action revert, undo etc. are otherwise not enabled  
		super.createActions();
		// Above is new line reqired in 2.1 to enable undo, redo, revert actions
		ResourceBundle bundle =
			MEditorPlugin.getDefault().getResourceBundle();
/*
	IAction a =
			new TextOperationAction(
				bundle,
				"ContentAssistProposal.",
				this,
				ISourceViewer.CONTENTASSIST_PROPOSALS);
// Added this call for 2.1 changes
// New to 2.1 - CTRL+Space key doesn't work without making this call 	
				
		a.setActionDefinitionId(
			ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", a);
		a =
			new TextOperationAction(
				bundle,
				"ContentAssistTip.",
				this,
				ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
//		Added this call for 2.1 changes				
				
		a.setActionDefinitionId(
			ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistTip", a);
		a =
			new TextOperationAction(
				bundle,
				"ContentFormatProposal.",
				this,
				ISourceViewer.FORMAT);
		setAction("ContentFormatProposal", a);

		ResourceAction ra= new AddTaskAction(bundle, "AddTask.", this); 
			ra.setHelpContextId(IAbstractTextEditorHelpContextIds.ADD_TASK_ACTION);
			ra.setActionDefinitionId(ITextEditorActionDefinitionIds.ADD_TASK);
			setAction(ITextEditorActionConstants.ADD_TASK, ra);
*/		
		
	}

	/**
	 * Getter method that returns a  M Code Scanner.
	 * @return MCodeScanner
	 */
	public static MCodeScanner getMCodeScanner() {
		return fMCodeScanner;
	}

	/** The <code>MEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs any extra 
	 * save behavior required by the M editor.
	 * 
	 * @param monitor the progress monitor
	 */
	public void doSave(IProgressMonitor monitor) {
		
		if (Boolean.valueOf(MEditorPrefs.getPrefs(MEditorPlugin.OFFLINE_MODE))) {
			super.doSave(monitor);
			return;
		}
		
		// JLI 110127 Make sure a server is defined first
		//if (VistaConnection.getPrimaryServer().compareTo(";;;") == 0) {
		if (! VistaConnection.getPrimaryServer()) { // 110821
			return;
		}
		IEditorInput input = getEditorInput();
// JLI 100813
		int number = 0;
		String str = " ";
		String toolTipText = input.getToolTipText(); //happens to be "[projectName]/[filename]"
		while (str.compareTo("") != 0) {
			str = MPiece.getPiece(toolTipText,"/",++number);
		}
		String originalToolTipPath = MPiece.getPiece(toolTipText,"/",1,number-2);
		String originalTopName = MPiece.getPiece(input.getToolTipText(),"/");
		String defaultProjectName = MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME);
		if (originalTopName.compareTo(defaultProjectName) == 0) { //if current project == default 'mcode' project
			if (VistaConnection.getCurrentConnection() == null) {
				VistaConnection.getPrimaryServer();
			}
		}
		String originalLocation = "";
		try {
			IResource originalResource = MEditorUtilities.getProject(originalTopName); //"mcode");
			if (!(originalResource == null)) {
				originalLocation = originalResource.getLocation().toString();
				String routineName = input.getName();
				originalLocation = RoutineLoad.getFullFileLocation(originalTopName, routineName);
				if (originalTopName.compareTo(defaultProjectName) == 0) {
					originalLocation = MPiece.getPiece(originalLocation,defaultProjectName+"/")
					                   +defaultProjectName+"/"+MPiece.getPiece(originalToolTipPath,defaultProjectName+"/",2);
				}
			}
		}
		catch (Exception e) {
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().
					   getActiveWorkbenchWindow().getShell(),
					"Routine Save - Exception Encountered",
					"Error MEditor001 Error message = "+e.getMessage());
		}
		str = input.getName();
		String originalFileName = originalLocation+"/"+str;

		if (VistaConnection.getCurrentConnection() == null) {
			VistaConnection.getPrimaryServer();
		}
		String currentProjectName = VistaConnection.getCurrentProject();
		if (currentProjectName.compareTo("") == 0) {
			currentProjectName = MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME);
		}
		try {
			IResource resource = MEditorUtilities.getProject(currentProjectName); //"mcode");
			String location = "";
			if (!(resource == null)) {
				location = resource.getLocation().toString();
				String routineName = input.getName();
				location = RoutineLoad.getFullFileLocation(routineName);
			}
			str = input.getName();
			String fileName = location+"/"+str;
			if (fileName.compareTo(originalFileName) != 0) {
				if (MessageDialog.openQuestion(
						MEditorUtilities.getIWorkbenchWindow().getShell(),
						"Meditor Plug-in: Routine Save  **WARNING**",
						"The location you are saving to differs from where it was loaded\n\n" +
						"Loaded From:\n"+
						"  "+originalFileName+"\n\n"+
						"Saving To:\n"+
						"  "+fileName+"\n\n"+
						"Do you want to TERMINATE the Save operation?")) {
					return;
				}
			}
			RoutineSave.previousCode = MEditorUtilities.fileToString(originalFileName);
			RoutineSave.currentCode = getTextFromActiveEditor();

			VistaConnection.getPrimaryServer(); // 100730 moved here
			super.doSave(monitor);  // Let Eclipse save the file to original location
			
			oldTopIndex = getTopIndex();
			oldCaretOffset = getCaretOffset();
			IWorkbenchWindow win = MEditorUtilities.getIWorkbenchWindow();
			IWorkbenchPage activePage = win.getActivePage();
			RoutineSave.saveRoutine(input.getName());
// JLI 100811 now activate the editor page
			activePage.activate(MEditorUtilities.getIWorkbenchPage().getActiveEditor());
			if (outlinePage != null)
				outlinePage.update();
		} catch (Exception e) {
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Meditor Plug-in",
					"Error encountered while saving routine: "+e.getMessage());
		}
//		timer = new java.util.Timer();
//		timer.schedule(new UpdateTask(),5000);
	}
/*
	private void copyFile(String fromFileName, String toFileName) {
		try {
		    // Create channel on the source
		    FileChannel srcChannel = new FileInputStream(fromFileName).getChannel();

		    // Create channel on the destination
		    FileChannel dstChannel = new FileOutputStream(toFileName).getChannel();

		    // Copy file contents from source to destination
		    dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

		    // Close the channels
		    srcChannel.close();
		    dstChannel.close();
		} catch (IOException e) {
		}
	}
	
	private void saveFile(String fileName, String sourceCode) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(sourceCode);
			fw.flush();
			fw.close();
			} catch (Exception e) {
//				throw new Exception(e.getMessage()+" Error 003");
			}

	}
	
	private String getDocumentText() {
        IWorkbenchPage page1 =
            PlatformUI.getWorkbench().
            getActiveWorkbenchWindow().
            getActivePage();
        IEditorPart part = page1.getActiveEditor();
        String text = "";
        if (part instanceof MEditor) {
        	text = ((MEditor) part).getSourceViewer().getDocument().get();
        }
        return text;
	}
*/
	
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
	
	public static String getTextFromActiveEditor() {
		MEditor meditor = (MEditor) MEditorUtilities.getIWorkbenchPage().getActiveEditor();
		ISourceViewer sourceViewer = meditor.getTheSourceViewer();
		StyledText textWidget = sourceViewer.getTextWidget();
		return textWidget.getText();
	}
}