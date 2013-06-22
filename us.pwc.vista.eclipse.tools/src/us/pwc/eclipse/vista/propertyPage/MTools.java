package us.pwc.eclipse.vista.propertyPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class MTools extends PropertyPage implements IWorkbenchPropertyPage {
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		FillLayout fl = new FillLayout();
		contents.setLayout(fl);
		return contents;
    }
}
