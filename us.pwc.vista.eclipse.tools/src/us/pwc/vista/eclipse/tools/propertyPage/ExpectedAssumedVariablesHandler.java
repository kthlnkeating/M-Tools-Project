package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import us.pwc.vista.eclipse.tools.VistAToolsPlugin;

public class ExpectedAssumedVariablesHandler {
	private static final QualifiedName EXPECTED_ASSUMED_VARS = new QualifiedName(VistAToolsPlugin.PLUGIN_ID, "expectedassumedvars");

	private List list;
	private Button removeButton;

	public ExpectedAssumedVariablesHandler(List list, Button addButton, Button removeButton) {
		this.list = list;
		this.removeButton = removeButton;
		this.attachListeners(list, addButton, removeButton);		
	}
	
	private void attachListeners(List list, Button addButton, Button removeButton) {
		list.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpectedAssumedVariablesHandler.this.handleSelectionChanged();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpectedAssumedVariablesHandler.this.addNewValue();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		removeButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpectedAssumedVariablesHandler.this.removeSelected();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		removeButton.setEnabled(false);
	}

	private void handleSelectionChanged() {
		boolean futureEnabled = this.list.getSelectionCount() > 0;
		boolean currentEnabled = this.removeButton.getEnabled();
		if (currentEnabled != futureEnabled) {
			this.removeButton.setEnabled(futureEnabled);
		}
	}
	
	private void addNewValue() {
		Shell shell = this.list.getParent().getShell();
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
			this.list.add(value);
		}
	}
	
	private void removeSelected() {
		int[] indices = this.list.getSelectionIndices();
		this.list.remove(indices);
	}
	
	public static String[] getVarsFromStore(IProject project) {
		try {
        	String vars = project.getPersistentProperty(EXPECTED_ASSUMED_VARS);
            if (vars == null) {
            	return new String[0];
            } else {
            	return vars.split(" ");
            }
        } catch (CoreException e) {
        	return new String[0];
        }
	}
        
	public void initialize(IProject project) {
		String[] vars = getVarsFromStore(project);
		this.list.setItems(vars);
	}

	public String getVarsAsString() {
		String[] items = this.list.getItems();
		if ((items == null) || (items.length == 0)) {
			return null;
		} else {
			String values = items[0];
			for (int i=1; i<items.length; ++i) {
				values = values + " " + items[i];
			}
			return values;
		}
	}
	
	public void accept(IProject project) {
		String vars = this.getVarsAsString();
        try {
        	project.setPersistentProperty(EXPECTED_ASSUMED_VARS, vars);
         } catch (CoreException e) {
         }		
	}
}
