package gov.va.mumps.debug.ui.launching;

import gov.va.mumps.debug.core.MDebugConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class TagUtilities {
	public static String selectTag(List<String> tags) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		LabelProvider lp = new LabelProvider();
		ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, lp);
		dlg.setMultipleSelection(false);
		dlg.setTitle("Entry Tag Selection");
		dlg.setMessage("Select entry tag.");
		dlg.setElements(tags.toArray(new String[0]));
		if (ListSelectionDialog.OK == dlg.open()) {
			String result = (String) dlg.getFirstResult();
			return result;
		}
		return null;
	}

	public static List<String> getTags(IFile file) {
		try {
			List<String> result = new ArrayList<String>();
			IDocument document = ResourceUtilExtension.getDocument(file);
			int n = document.getNumberOfLines(); 
			for (int i=0; i<n; ++i) {
				IRegion lineInfo = document.getLineInformation(i); 
				int lineLength = lineInfo.getLength();
				if (lineLength > 0) {
					int offset = lineInfo.getOffset(); 
					String lineText = document.get(offset, lineLength); 
					lineText = lineText.replace('\t', ' ');
					if (lineText.charAt(0) != ' ') {
						String tagFull = lineText.split(" ")[0];
						String tag = tagFull.split("\\(")[0];
						result.add(tag);
					}	    			
				}
			}
			return result;
		} catch (Throwable t) {
			IStatus status = new Status(IStatus.ERROR, MDebugConstants.M_DEBUG_MODEL, "Error obtaining tags for the routine.", t);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return null;
		}
	}		
}
