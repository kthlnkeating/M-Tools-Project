package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class AssumedVariables extends PropertyPage implements IWorkbenchPropertyPage {
	private ExpectedAssumedVariablesHandler expectedAssumedVariablesHandler;
	private RecursionSpecificationHandler recursionSpecificationHandler;

	public AssumedVariables() {
		super();
	}

	private List addExpectedVarsList(Composite parent) {
		List list = new List(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		list.setFont(parent.getFont());
		
		GridData gd = new GridData(GridData.FILL_BOTH); 
		gd.horizontalSpan = 2;
		gd.heightHint = 100;
		gd.widthHint = 50;
		gd.horizontalAlignment = SWT.FILL;
		list.setLayoutData(gd);
		
		return list;
	}
	
	private void initializeExpectedVarsHandler(List expectedVarsList, Button addVar, Button removeVars) {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		this.expectedAssumedVariablesHandler = new ExpectedAssumedVariablesHandler(expectedVarsList, addVar, removeVars);
		this.expectedAssumedVariablesHandler.initialize(project);		
	}
	
	private void addExpectedVarsSection(Composite parent) {		
		SWTHelper.addLabel(parent, "Specify expected assumed variables:", 3);
		List expectedVarsList = this.addExpectedVarsList(parent);
		Button[] buttons = SWTHelper.createButtons(parent, new String[]{"Add", "Remove"});
		
		this.initializeExpectedVarsHandler(expectedVarsList, buttons[0], buttons[1]);		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		contents.setLayout(gl);
		
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		this.recursionSpecificationHandler = RecursionSpecificationHandlerFactory.getInstance(contents, RecursionSpecificationHandler.AV_PREFIX);
		this.recursionSpecificationHandler.initialize(project);
		
		this.addExpectedVarsSection(contents);
		
		return contents;
    }

	@Override
	public boolean performOk() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class); 
		this.expectedAssumedVariablesHandler.accept(project);
		this.recursionSpecificationHandler.accept(project);
		return super.performOk();
	}
 }
