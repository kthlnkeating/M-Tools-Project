package us.pwc.vista.eclipse.tools.propertyPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import us.pwc.vista.eclipse.tools.VistAToolsPlugin;

public class RecursionSpecificationHandler {
	public static final String AV_PREFIX = "assumedvars";
	public static final String OC_PREFIX = "occurances";
	
	private String propertyNamePrefix;
	private Button[] radioButtons;
	private TableViewer viewer;
	private Button addButton;
	private Button removeButton;
	private List<NameFilter> filters;
	
	public RecursionSpecificationHandler(String propertyNamePrefix, Button[] radioButtons, TableViewer viewer, Button addButton, Button removeButton) {
		this.propertyNamePrefix = propertyNamePrefix;
		this.radioButtons = radioButtons;
		this.viewer = viewer;
		this.addButton = addButton;
		this.removeButton = removeButton;
		this.attachListeners(viewer, addButton, removeButton);		
	}
	
	private static QualifiedName getRecursionDepthQualifiedName(String prefix) {
		return new QualifiedName(VistAToolsPlugin.PLUGIN_ID, prefix + "recursiondepth");
	}
	
	private static QualifiedName getNameFiltersQuaifiedName(String prefix) {
		return new QualifiedName(VistAToolsPlugin.PLUGIN_ID, prefix + "recursionnamefilters");		
	}
		
	private void attachListeners(TableViewer viewer, Button addButton, Button removeButton) {
		for (int i=0; i<3; ++i) {
			radioButtons[i].addSelectionListener(new SelectionListener() {				
				@Override
				public void widgetSelected(SelectionEvent e) {
					RecursionSpecificationHandler.this.enableRoutineNameFilter();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		}
		
		radioButtons[3].addSelectionListener(new SelectionListener() {				
			@Override
			public void widgetSelected(SelectionEvent e) {
				RecursionSpecificationHandler.this.disableRoutineNameFilter();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
			
		viewer.getTable().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RecursionSpecificationHandler.this.handleSelectionChanged();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RecursionSpecificationHandler.this.addNewValue();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		removeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RecursionSpecificationHandler.this.removeSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		removeButton.setEnabled(false);
	}
	
	private void enableRoutineNameFilter() {
		this.viewer.getTable().setEnabled(false);
		this.addButton.setEnabled(false);
		this.removeButton.setEnabled(false);
	}

	private void disableRoutineNameFilter() {
		this.viewer.getTable().setEnabled(true);
		this.addButton.setEnabled(true);
		this.removeButton.setEnabled(this.viewer.getTable().getSelectionCount() > 0);
	}
	
	private void handleSelectionChanged() {
		boolean futureEnabled = this.viewer.getTable().getSelectionCount() > 0;
		boolean currentEnabled = this.removeButton.getEnabled();
		if (currentEnabled != futureEnabled) {
			this.removeButton.setEnabled(futureEnabled);
		}
	}
	
	private void addNewValue() {
		Shell shell = this.viewer.getTable().getParent().getShell();
		IInputValidator v = new IInputValidator() {			
			@Override
			public String isValid(String newText) {
				if ((newText == null) || (newText.length() == 0)) {
					return "Empty values are not accepted.";
				} else {
					return null;
				}
			}
		};
		InputDialog d = new InputDialog(shell, "Enter Value", "Enter Value", "", v);
		d.open();
		String value = d.getValue();
		if (value != null) {
			NameFilter filter = new NameFilter(value, NameFilterType.INCLUDE);
			this.filters.add(filter);
			this.viewer.refresh();
		}
	}
	
	private void removeSelected() {
		int[] indices = this.viewer.getTable().getSelectionIndices();
		Arrays.sort(indices);
		for (int i=indices.length-1; i>=0; --i) {
			this.filters.remove(i);
		}
		this.viewer.refresh();
	}
	
	public static int getRecursionDepthFromStore(IProject project, String prefix) {
		try {
			QualifiedName propertyName = getRecursionDepthQualifiedName(prefix);
	       	String recursionDepth = project.getPersistentProperty(propertyName);
	       	if ((recursionDepth == null) || recursionDepth.isEmpty()) {
	       		return 0;
	       	} else {
	       		return Integer.parseInt(recursionDepth);
	       	}
	    } catch (CoreException e) {
        	return 0;
        }
	}
	
	public static List<NameFilter> getFiltersFromStore(IProject project, String prefix) {
		try {
			QualifiedName propertyName = getNameFiltersQuaifiedName(prefix);
        	String filterStringsTotal = project.getPersistentProperty(propertyName);
            if (filterStringsTotal == null) {
            	return new ArrayList<NameFilter>();
            } else {
            	String[] filterStrings = filterStringsTotal.split(" ");
            	List<NameFilter> result = new ArrayList<NameFilter>();
            	for (int i=0; i<filterStrings.length/2; ++i) {
            		String typeAsString = filterStrings[i*2];
            		NameFilterType type = NameFilterType.valueOf(typeAsString);
            		String value = filterStrings[i*2+1];
            		NameFilter rnf = new NameFilter(value, type);
            		result.add(rnf);
            	}
            	return result;
            }
        } catch (CoreException e) {
        	return new ArrayList<NameFilter>();
        }
	}
        
	public void initialize(IProject project) {
		int recursionDepth = getRecursionDepthFromStore(project, this.propertyNamePrefix);
		this.radioButtons[recursionDepth].setSelection(true);
		this.filters = getFiltersFromStore(project, this.propertyNamePrefix);
		this.viewer.setInput(this.filters);
		this.viewer.getTable().setEnabled(recursionDepth == 3);
	}

	private String getRecursionDepth() {
		for (int i=0; i<4; ++i) {
			if (this.radioButtons[i].getSelection()) {
				return String.valueOf(i);
			}
		}
		return "0";
	}
	
	private String getNameFiltersAsString() {
		if ((this.filters == null) || (this.filters.size() == 0)) {
			return null;
		} else {			
			String result = this.filters.get(0).toString();
			for (int i=1; i<this.filters.size(); ++i) {
				result = result + " " + this.filters.get(i).toString();
			}
			return result;
		}
	}
	
	public void accept(IProject project) {
        try {
        	String recursionDepth = this.getRecursionDepth();
			QualifiedName rdPropertyName = getRecursionDepthQualifiedName(this.propertyNamePrefix);
        	project.setPersistentProperty(rdPropertyName, recursionDepth);
    		String filters = this.getNameFiltersAsString();
			QualifiedName rnPropertyName = getNameFiltersQuaifiedName(this.propertyNamePrefix);
			project.setPersistentProperty(rnPropertyName, filters);
         } catch (CoreException e) {
         }		
	}
}
