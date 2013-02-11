package gov.va.med.iss.mdebugger.util;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbench;

public class TextToArray {
	
	public static List convert(String strval) {
		List list = new LinkedList();
		list = new ArrayList();
		int startLineCount = 0;
		int fromIndex = 0;
		String stra;
		int fromIndex1;
		while (strval.indexOf('\n',fromIndex) > -1) {
			fromIndex1 = strval.indexOf('\n',fromIndex);
			if ( (fromIndex1 == -1) && (fromIndex < strval.length()))
				fromIndex1 = strval.length();
			if (fromIndex1 > -1) {
				stra = strval.substring(fromIndex,fromIndex1);
				// trim white space from end of lines
				if (stra.length() > 0) {
				try {
				while ((stra.charAt(stra.length()-1) == '\t') || (stra.charAt(stra.length()-1) == ' ')) {
					stra = stra.substring(0,stra.length()-1);
					if (stra.length() < 1)
						break;
				}
				} catch (Exception e){
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),"M Debugger","Error Here");
				}
				}
					list.add(stra);  // hm.put(Integer.toString(nlines),stra);
			}
			fromIndex = fromIndex1+1;
		}
		return list;
	}

}
