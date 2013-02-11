/*
 * Created on Aug 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPerspectiveFactory;
//import org.eclipse.ui.texteditor.MoveLinesAction;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorPerspectiveFactory implements IPerspectiveFactory {

//	private static final String MEDITOR_VIEW_ID = "";
//	private static final String MEDITOR_ACTION_ID = "";
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		// TODO Auto-generated method stub
		String editorArea = layout.getEditorArea();
		
		layout.addView(
				IPageLayout.ID_OUTLINE,
				IPageLayout.LEFT,
				0.25f,
				editorArea);
		IFolderLayout bottom = layout.createFolder(
				"bottom",
				IPageLayout.BOTTOM,
				0.66f,
				editorArea);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);

	}

}
