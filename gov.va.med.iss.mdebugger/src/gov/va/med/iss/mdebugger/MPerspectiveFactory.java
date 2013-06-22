package gov.va.med.iss.mdebugger;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class MPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		defineLayout(layout);

	}
	
	public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place navigator and outline to left of
        // editor area.
        IFolderLayout topLeft =
                layout.createFolder("topLeft", IPageLayout.TOP, (float) 0.4, editorArea);
        topLeft.addView("gov.va.med.iss.mdebugger.views.CurrentStackView");
        IFolderLayout topRight =
        	    layout.createFolder("topRight",IPageLayout.RIGHT, 0.4f, "topLeft");
        topRight.addView("gov.va.med.iss.mdebugger.views.BreakpointView");
        topRight.addView("gov.va.med.iss.mdebugger.views.AllValuesView");
        topRight.addView("gov.va.med.iss.mdebugger.views.WatchValuesView");
        topRight.addView("gov.va.med.iss.mdebugger.views.WatchVariablesView");
        topRight.addView("gov.va.med.iss.mdebugger.views.InitializationValuesView");
        
        IFolderLayout bottomRight =
        	layout.createFolder("bottomRight",IPageLayout.BOTTOM, 0.4f, editorArea);
        bottomRight.addView("gov.va.med.iss.mdebugger.views.MDebuggerConsoleDisplay");
	}
}