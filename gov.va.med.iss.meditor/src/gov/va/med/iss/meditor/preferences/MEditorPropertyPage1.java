/*
 * Created on Aug 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import gov.va.med.iss.meditor.MEditorBasicData;

/**
 * @author VHAISFIVEYJ
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MEditorPropertyPage1 extends PropertyPage implements
		IWorkbenchPropertyPage {

	/**
	 * 
	 */
	public MEditorPropertyPage1() {
		super();
		// TODO Auto-generated constructor stub
	}
	Text textField;
	Button updateButton;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		panel.setLayout(layout);
		
		Label label = new Label(panel, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Enter the Unit Test routine name if there is one,"
				+" and indicate whether the entry in the Routine File should be updated.");
		
		textField = new Text(panel, SWT.BORDER);
		textField.setLayoutData(new GridData());
		textField.setText(getUnitTestNameProperty());
		
		updateButton = new Button(panel, SWT.CHECK);
		updateButton.setLayoutData(new GridData());
		updateButton.setText("Update Entry in Routine File when saving the routine");
		updateButton.setSelection(getUpdateRoutineFileProperty());
		return panel;
	}
	
	public String getUnitTestNamePropertyValue(IResource resource){
		try {
			String value = resource.getPersistentProperty(MEditorBasicData.UNIT_TEST_PROPKEY);
			if (value == null)
				return "";
			return value;
		} catch (CoreException e) {
//			MEditorLog.logError(e);
			return e.getMessage();
		}
	}
	
	protected String getUnitTestNameProperty() {
		return getUnitTestNamePropertyValue((IResource) getElement().getAdapter(IResource.class));
	}
	
	public void setUnitTestNamePropertyValue(IResource resource, String unitTestName) {
		String value = unitTestName;
		if (value.equals(""))
			value = null;
		try {
			resource.setPersistentProperty(MEditorBasicData.UNIT_TEST_PROPKEY, value);
		} catch (CoreException e) {
//			MEditorLog.logError();
		}
		
	}
	
	protected boolean getReadOnlyProperty() {
		return getReadOnlyPropertyValue((IResource) getElement().getAdapter(IResource.class));
	}
	
	public boolean getReadOnlyPropertyValue(IResource resource) {
		try {
			String value = resource.getPersistentProperty(MEditorBasicData.READ_ONLY_PROPKEY);
			if (value == null)
				return false;
			else if (value == "1")
				return true;
			else
				return false;
		} catch (CoreException e) {
//			MEditorLog.logError(e);
			return false; // e.getMessage();
		}
	}

	public void setReadOnlyPropertyValue(IResource resource, boolean readOnlyState) {
		boolean value = readOnlyState;
		String strValue = "";
		if (! value )
			strValue = null;
		else
			strValue = "1";
		try {
			resource.setPersistentProperty(MEditorBasicData.READ_ONLY_PROPKEY, strValue);
		} catch (CoreException e) {
//			MEditorLog.logError();
		}
		
	}

	public void setUnitTestNameProperty(String unitTestName) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		setUnitTestNamePropertyValue(resource, unitTestName);
	}

	public boolean getUpdateRoutineFilePropertyValue(IResource resource){
		try {
			String value = resource.getPersistentProperty(MEditorBasicData.UPDATE_ROUTINE_FILE_PROPKEY);
			if (value == null)
				return true;
			return (value.compareTo("true") == 0) ? true : false;
		} catch (CoreException e) {
//			MEditorLog.logError(e);
//			return e.getMessage();
			return true;
		}
	}
	
	protected boolean getUpdateRoutineFileProperty() {
		return getUpdateRoutineFilePropertyValue((IResource) getElement().getAdapter(IResource.class));
	}
	
	public void setUpdateRoutineFilePropertyValue(IResource resource, boolean value) {
		String newvalue;
		if (value)
			newvalue = "true";
		else
			newvalue = "false";
		
		try {
			resource.setPersistentProperty(MEditorBasicData.UPDATE_ROUTINE_FILE_PROPKEY, newvalue);
		} catch (CoreException e) {
//			MEditorLog.logError();
		}
		
	}
	
	public void setUpdateRoutineFileProperty(boolean value) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		setUpdateRoutineFilePropertyValue(resource, value);
	}

	public boolean performOk() {
		setUnitTestNameProperty(textField.getText());
		setUpdateRoutineFileProperty(updateButton.getSelection());
		return super.performOk();
	}

}
