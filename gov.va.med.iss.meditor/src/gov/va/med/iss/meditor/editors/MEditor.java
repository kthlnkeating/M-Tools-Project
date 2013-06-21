package gov.va.med.iss.meditor.editors;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.meditor.MEditorDocumentProvider;
import gov.va.med.iss.meditor.MEditorPlugin;
import gov.va.med.iss.meditor.MEditorSourceViewerConfiguration;
import gov.va.med.iss.meditor.m.MCodeScanner;
import gov.va.med.iss.meditor.preferences.MEditorPreferencesPage;
import gov.va.med.iss.meditor.preferences.MEditorPrefs;
import gov.va.med.iss.meditor.utils.MEditorUtilities;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mumps.meditor.MEditorRPC;
import org.mumps.meditor.SaveRoutine;
import org.mumps.meditor.ServerSaveFailure;

/**
 *	This class is responsible for configuring the M editor.
 *  
 */
public class MEditor extends /* AbstractDecoratedTextEditor { // */ TextEditor { //AbstractTextEditor {
	
	public static final String M_EDITOR_ID = "gov.va.med.iss.meditor.editors.MEditor";
	
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

	/** The <code>MEditor</code> implementation of this 
	 * <code>AbstractTextEditor</code> method performs any extra 
	 * save behavior required by the M editor.
	 * 
	 * @param monitor the progress monitor
	 */
	public void doSave(IProgressMonitor monitor) {
		
		if (!Boolean.valueOf(MEditorPrefs.getPrefs(MEditorPlugin.P_AUTO_SAVE_TO_SERVER))) {
			super.doSave(monitor);
			return;
		}

		//Resolve object dependencies (calculate dependencies at the entry point up front)
		VistaLinkConnection connection = VistaConnection.getConnection();
		MEditorRPC rpc = new MEditorRPC(connection);
		SaveRoutine saveRoutine = new SaveRoutine(rpc);
		
		String projectName = VistaConnection.getPrimaryProject();
		String routineName = getEditorInput().getName();
		if (!routineName.endsWith(".m")) {
			MessageDialog.openInformation(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "MEditor",
					"A MUMPS Routine must end in \".m\" to be saved to server.");
			super.doSave(monitor);
			return;
		}
		routineName = routineName.substring(0, routineName.length()-2);
		String fileCode = getSourceViewer().getTextWidget().getText();
		
		try {
			saveRoutine.save(routineName, projectName, fileCode);
		} catch (ServerSaveFailure e) {
			e.printStackTrace();
		}
		
		super.doSave(monitor);

		
//		// JLI 110127 Make sure a server is defined first
//		//if (VistaConnection.getPrimaryServer().compareTo(";;;") == 0) {
//		if (! VistaConnection.getPrimaryServer()) { // 110821
//			return;
//		}
//		IEditorInput input = getEditorInput();
//// JLI 100813
//		int number = 0;
//		String str = " ";
//		String toolTipText = input.getToolTipText(); //happens to be "[projectName]/[filename]"
//		while (str.compareTo("") != 0) {
//			str = MPiece.getPiece(toolTipText,"/",++number);
//		}
//		String originalToolTipPath = MPiece.getPiece(toolTipText,"/",1,number-2);
//		String originalTopName = MPiece.getPiece(input.getToolTipText(),"/");
//		String defaultProjectName = MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME);
//		if (originalTopName.compareTo(defaultProjectName) == 0) { //if current project == default 'mcode' project
//			if (VistaConnection.getCurrentConnection() == null) {
//				VistaConnection.getPrimaryServer();
//			}
//		}
//		String originalLocation = "";
//		try {
//			IResource originalResource = MEditorUtilities.getProject(originalTopName); //"mcode");
//			if (!(originalResource == null)) {
//				originalLocation = originalResource.getLocation().toString();
//				String routineName = input.getName();
//				originalLocation = RoutineLoad.getFullFileLocation(originalTopName, routineName);
//				if (originalTopName.compareTo(defaultProjectName) == 0) {
//					originalLocation = MPiece.getPiece(originalLocation,defaultProjectName+"/")
//					                   +defaultProjectName+"/"+MPiece.getPiece(originalToolTipPath,defaultProjectName+"/",2);
//				}
//			}
//		}
//		catch (Exception e) {
//			MessageDialog.openInformation(
//					PlatformUI.getWorkbench().
//					   getActiveWorkbenchWindow().getShell(),
//					"Routine Save - Exception Encountered",
//					"Error MEditor001 Error message = "+e.getMessage());
//		}
//		str = input.getName();
//		String originalFileName = originalLocation+"/"+str;
//
//		if (VistaConnection.getCurrentConnection() == null) {
//			VistaConnection.getPrimaryServer();
//		}
//		String currentProjectName = VistaConnection.getCurrentProject();
//		if (currentProjectName.compareTo("") == 0) {
//			currentProjectName = MEditorPrefs.getPrefs(MEditorPlugin.P_PROJECT_NAME);
//		}
//		try {
//			IResource resource = MEditorUtilities.getProject(currentProjectName); //"mcode");
//			String location = "";
//			if (!(resource == null)) {
//				location = resource.getLocation().toString();
//				String routineName = input.getName();
//				location = RoutineLoad.getFullFileLocation(routineName);
//			}
//			str = input.getName();
//			String fileName = location+str; //location+"/"+str;
//			if (fileName.compareTo(originalFileName) != 0) {
//				if (MessageDialog.openQuestion(
//						MEditorUtilities.getIWorkbenchWindow().getShell(),
//						"Meditor Plug-in: Routine Save  **WARNING**",
//						"The location you are saving to differs from where it was loaded\n\n" +
//						"Loaded From:\n"+
//						"  "+originalFileName+"\n\n"+
//						"Saving To:\n"+
//						"  "+fileName+"\n\n"+
//						"Do you want to TERMINATE the Save operation?")) {
//					return;
//				}
//			}
//			RoutineSave.previousCode = MEditorUtilities.fileToString(originalFileName);
//			RoutineSave.currentCode = getTextFromActiveEditor();
//
//			VistaConnection.getPrimaryServer(); // 100730 moved here
//			super.doSave(monitor);  // Let Eclipse save the file to original location
//			
//			oldTopIndex = getTopIndex();
//			oldCaretOffset = getCaretOffset();
//			IWorkbenchWindow win = MEditorUtilities.getIWorkbenchWindow();
//			IWorkbenchPage activePage = win.getActivePage();
//			RoutineSave.saveRoutine(input.getName());
//// JLI 100811 now activate the editor page
//			activePage.activate(MEditorUtilities.getIWorkbenchPage().getActiveEditor());
//			if (outlinePage != null)
//				outlinePage.update();
//		} catch (Exception e) {
//			MessageDialog.openInformation(
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//					"Meditor Plug-in",
//					"Error encountered while saving routine: "+e.getMessage());
//		}
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