package us.pwc.vista.eclipse.tools.propertyPage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.widgets.Button;

import us.pwc.vista.eclipse.tools.VistAToolsPlugin;

public class OccuranceTypesHandler {
	private static final QualifiedName OCCURANCE_TYPES = new QualifiedName(VistAToolsPlugin.PLUGIN_ID, "occurancetypes");

	private Button[] buttons;
	
	public OccuranceTypesHandler(Button[] buttons) {
		this.buttons = buttons;
	}
	
	public static String[] getTypesFromStore(IProject project) {
		try {
        	String types = project.getPersistentProperty(OCCURANCE_TYPES);
            if (types == null) {
            	return null;
            } else {
            	String[] rawResults = types.split(" ");
            	if (rawResults.length == 0) return null;
            	String[] results = new String[rawResults.length];
            	for (int i=0; i<rawResults.length; ++i) {
            		results[i] = rawResults[i].replace('_', ' ');
            	}
            	return results;
            }
        } catch (CoreException e) {
        	return null;
        }
		
	}
	
	public void initialize(IProject project) {
		String[] types = getTypesFromStore(project);
		if (types == null) {
			for (Button b : this.buttons) {
				b.setSelection(true);
			}
		} else {
			Set<String> typesSet = new HashSet<String>(Arrays.asList(types));
			for (Button b : this.buttons) {
				String text = b.getText();
				b.setSelection(typesSet.contains(text));
			}
		}
	}

	public void accept(IProject project) {
        try {
        	String result = "";
			for (Button b : this.buttons) {
				if (b.getSelection()) {
					String text = b.getText().replace(' ', '_');
					if (result.length() > 0) {
						result = result + " ";
					}
					result = result + text;
				}
			}
        	project.setPersistentProperty(OCCURANCE_TYPES, result);
         } catch (CoreException e) {
         }		
	}
}
