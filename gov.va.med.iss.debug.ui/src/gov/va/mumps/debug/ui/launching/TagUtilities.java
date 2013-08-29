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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.resource.ResourceUtilExtension;

public class TagUtilities {
	public static EntryTag selectTag(List<EntryTag> tags) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		LabelProvider lp = new LabelProvider() {
	   		@Override
    		public Image getImage(Object element) {
    			return null;
    		}
    		
    		@Override
    		public String getText(Object element) {
    			if (element instanceof EntryTag) {
    				EntryTag et = (EntryTag) element;
    				return et.getLabel();
    			}
    			return null;
    		}
    	};
		ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, lp);
		dlg.setMultipleSelection(false);
		dlg.setTitle("Entry Tag Selection");
		dlg.setMessage("Select entry tag.");
		dlg.setElements(tags.toArray(new EntryTag[0]));
		if (ListSelectionDialog.OK == dlg.open()) {
			EntryTag result = (EntryTag) dlg.getFirstResult();
			return result;
		}
		return null;
	}
	
	private static EntryTag getEntryTag(String lineText) {
		String[] tagWithParams = lineText.split(" ")[0].trim().split("\\(");
		String tag = tagWithParams[0];
		if ((tagWithParams.length < 2)) {
			return new EntryTag(tag, null);
		} else {
			String paramsWithPar = tagWithParams[1].trim();
			String paramsString = paramsWithPar.substring(0, paramsWithPar.length()-1);
			if (paramsString.isEmpty()) {
				return new EntryTag(tag, new String[0]);				
			} else {
				String[] params = paramsString.split("\\,");
				for (int i=0; i<params.length; ++i) {
					params[i] = params[i].trim();
				}
				return new EntryTag(tag, params);
			}
		}
	}

	public static List<EntryTag> getTags(IFile file) {
		try {
			List<EntryTag> result = new ArrayList<EntryTag>();
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
						EntryTag entryTag = getEntryTag(lineText);
						result.add(entryTag);
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
