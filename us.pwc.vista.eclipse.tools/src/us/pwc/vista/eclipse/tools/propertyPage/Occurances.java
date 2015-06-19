package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class Occurances extends PropertyPage implements IWorkbenchPropertyPage {
	private OccuranceTypesHandler occurancesTypesHandler;

	public Occurances() {
		super();
	}

	private void addOccuranceTypes(Composite parent) {		
		SWTHelper.addLabel(parent, "Specify occurance types:", 4);

		String[] titles = new String[]{"Write", "Read", "Indirection", "Intrinsic Text", "Naked Global", "Execute", "Exclusive Kill", "Goto"};
		Button[] buttons = SWTHelper.createCheckButtons(parent, titles);
		this.occurancesTypesHandler = new OccuranceTypesHandler(buttons);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(4, false);
		contents.setLayout(gl);
		
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		this.addOccuranceTypes(contents);
		this.occurancesTypesHandler.initialize(project);
		
		return contents;
    }

	@Override
	public boolean performOk() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		this.occurancesTypesHandler.accept(project);
		return super.performOk();
	}
 }