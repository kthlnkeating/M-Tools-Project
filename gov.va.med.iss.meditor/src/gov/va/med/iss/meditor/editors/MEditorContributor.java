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
import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

import gov.va.med.iss.meditor.MEditorPlugin;

/**
 *  Manages the installation and deinstallation of actions for the SQL editor. 
 */
public class MEditorContributor extends TextEditorActionContributor {
/*
	protected RetargetTextEditorAction fContentAssistProposal;
	protected RetargetTextEditorAction fContentAssistTip;
	protected RetargetTextEditorAction fContentFormatProposal;
*/
	/**
	 * Constructor for SQLEditorContributor.  Creates a new contributor in the form of 
	 * adding Content Assist, Conent Format and Assist tip menu items.
	 * 
	 * Changes required in V 2.1. Shortcut keys on global actions must be explicitly set.  
	 * Content Assist and Context Information Shortcut keys must be set to the key 
	 * defintion ID's. 
	 */
	public MEditorContributor() {
		super();
		ResourceBundle bundle =
			MEditorPlugin.getDefault().getResourceBundle();
/*
		fContentAssistProposal =
			new RetargetTextEditorAction(bundle, "ContentAssistProposal.");
		//		Added this call for 2.1 changes
		// 		New to 2.1 - CTRL+Space key doesn't work without making this call 	

		fContentAssistProposal.setActionDefinitionId(
			ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

		fContentFormatProposal =
			new RetargetTextEditorAction(bundle, "ContentFormatProposal.");
		fContentAssistTip =
			new RetargetTextEditorAction(bundle, "ContentAssistTip.");
		fContentAssistTip.setActionDefinitionId(
			ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
*/
	}
	public void contributeToMenu(IMenuManager mm) {
		IMenuManager editMenu =
			mm.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
/*
 			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			editMenu.add(fContentFormatProposal);
			editMenu.add(fContentAssistTip);
*/
		}
	}
	/**
	 * Sets the active editor to this contributor.
	 * This updates the actions to reflect the M editor.
	 * @see EditorActionBarContributor#editorChanged
	 */
	public void setActiveEditor(IEditorPart part) {

		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor) part;
		if (part instanceof MEditor)
			((MEditor) part).setWordWrap();
/*
		fContentAssistProposal.setAction(
			getAction(editor, "ContentAssistProposal"));
		fContentFormatProposal.setAction(
			getAction(editor, "ContentFormatProposal"));
		fContentAssistTip.setAction(getAction(editor, "ContentAssistTip"));
*/
	}
	/**
	 * 
	 * Contributes to the toolbar. 
	 * @see EditorActionBarContributor#contributeToToolBar
	 */
	public void contributeToToolBar(IToolBarManager tbm) {
		tbm.add(new Separator());
	}

}
