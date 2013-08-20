package gov.va.med.iss.connection.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class VistaPreferencePage extends PropertyPage implements IWorkbenchPreferencePage {
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		contents.setLayout(layout);
		SWTHelper.setGridData(contents, SWT.FILL, true, SWT.FILL, true);
		return contents;
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}