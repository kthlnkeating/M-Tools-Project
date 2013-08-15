package us.pwc.vista.eclipse.server.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.statushandlers.StatusManager;

import us.pwc.vista.eclipse.core.helper.SWTHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;

public class RoutineLoadPage extends PropertyPage implements IWorkbenchPropertyPage {
	private Button askScheme;
	private Button projectRootScheme;
	private Button namespaceSpecifiedScheme;

	private Button addServerNameSubfolder;
	private Label addNamespaceCharsSubfolderLabel;
	private Text addNamespaceCharsSubfolder;
	
	public RoutineLoadPage() {
		super();
	}
	
	private void addDescription(Composite contents, int horizontalSpan) {
		SWTHelper.addLabel(contents, Messages.RLP_TITLE_SCHEME, horizontalSpan);		
	}
	
	private void addCore(Composite contents) {
		this.askScheme = SWTHelper.createRadioBox(contents, Messages.RLP_ASK, 1);
		SWTHelper.addEmptyLabel(contents, 2);		

		this.projectRootScheme = SWTHelper.createRadioBox(contents, Messages.RLP_PROJECT_ROOT, 2);
		SWTHelper.addEmptyLabel(contents, 1);

		SWTHelper.addEmptyLabel(contents, 1);
		this.addServerNameSubfolder = SWTHelper.createCheckButton(contents, Messages.RLP_SERVER_NAME_SUBFOLDER);
		SWTHelper.addEmptyLabel(contents, 1);

		SWTHelper.addEmptyLabel(contents, 1);
		Label label = new Label(contents, SWT.LEFT);
		label.setText(Messages.RLP_NAMESPACE_SUBFOLDER);
		SWTHelper.setGridData(label, SWT.LEFT, false, SWT.CENTER, false);
		this.addNamespaceCharsSubfolderLabel = label;
		
		Text text = new Text(contents, SWT.BORDER);
		SWTHelper.setGridData(text, SWT.NONE, true, SWT.CENTER, false);
		this.addNamespaceCharsSubfolder = text;
		
		this.namespaceSpecifiedScheme = SWTHelper.createRadioBox(contents, Messages.RLP_NAMESPACE_SPECIFIED, 2);
		SWTHelper.addEmptyLabel(contents, 1);				
	}
		
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		contents.setLayout(gl);
		
		addDescription(contents, 3);
		addCore(contents);
		
		if (initialize()) {
			attachListeners();
		}
		
		return contents;
    }
		
	private boolean initialize() {
		IAdaptable adaptable = this.getElement();
		IProject project = (IProject) adaptable.getAdapter(IProject.class);
		try {
			initializeControls(project);
			return true;
		} catch (CoreException coreException) {
			disableAll();
			return false;
		}		
	}

	private void initializeControls(IProject project) throws CoreException {	
		NewFileFolderScheme locationScheme = VistAServerPrefs.getNewFileFolderScheme(project);
		switch (locationScheme) {
		case PROJECT_ROOT:
			this.projectRootScheme.setSelection(true);		
			break;
		case NAMESPACE_SPECIFIED:
			this.namespaceSpecifiedScheme.setSelection(true);
		default:
			this.askScheme.setSelection(true);
		}
		
		this.addServerNameSubfolder.setSelection(VistAServerPrefs.getAddServerNameSubfolder(project));
		int numChars = VistAServerPrefs.getAddNamespaceCharsSubfolder(project);
		this.addNamespaceCharsSubfolder.setText(String.valueOf(numChars));

		handleAddToRootChanged(locationScheme == NewFileFolderScheme.PROJECT_ROOT);
	}
	
	private void disableAll() {		
		this.askScheme.setEnabled(false);
		this.projectRootScheme.setEnabled(false);
		this.namespaceSpecifiedScheme.setEnabled(false);
		this.addServerNameSubfolder.setEnabled(false);
		this.addNamespaceCharsSubfolderLabel.setEnabled(false);
		this.addNamespaceCharsSubfolder.setEnabled(false);
	}
	
	private void handleAddToRootChanged(boolean b) {
		this.addServerNameSubfolder.setEnabled(b);
		this.addNamespaceCharsSubfolderLabel.setEnabled(b);
		this.addNamespaceCharsSubfolder.setEnabled(b);
	}
	
	private void attachListeners() {
		this.projectRootScheme.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = RoutineLoadPage.this.projectRootScheme.getSelection();
				RoutineLoadPage.this.handleAddToRootChanged(selection);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		this.addNamespaceCharsSubfolder.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				Text source = (Text) e.getSource();
				String value = source.getText();
				if (value.isEmpty()) {
					RoutineLoadPage.this.setErrorMessage(Messages.RLP_NUMBER_REQUIRED);
					RoutineLoadPage.this.setValid(false);			
				} else {
					try {
						int n = Integer.parseInt(value);
						if ((n < 0) || (n>4)) {
							RoutineLoadPage.this.setErrorMessage(Messages.RLP_NUMBER_RANGE);
							RoutineLoadPage.this.setValid(false);										
						} else {
							RoutineLoadPage.this.setErrorMessage(null);
							RoutineLoadPage.this.setValid(true);																							
						}
					} catch (Throwable t) {
						RoutineLoadPage.this.setErrorMessage(Messages.RLP_INVALID_NUMBER);
						RoutineLoadPage.this.setValid(false);																
					}
				}
			}
		});

	}

	@Override
	public boolean performOk() {
		try {
			IAdaptable adaptable = this.getElement();
			IProject project = (IProject) adaptable.getAdapter(IProject.class);
			if (this.projectRootScheme.getSelection()) {
				VistAServerPrefs.setNewFileFolderScheme(project, NewFileFolderScheme.PROJECT_ROOT);
			} else if (this.namespaceSpecifiedScheme.getSelection()) {
				VistAServerPrefs.setNewFileFolderScheme(project, NewFileFolderScheme.NAMESPACE_SPECIFIED);
			} else {
				VistAServerPrefs.setNewFileFolderScheme(project, NewFileFolderScheme.ASK);				
			}
			VistAServerPrefs.setAddServerNameSubfolder(project, this.addServerNameSubfolder.getSelection());
			int numChars = Integer.parseInt(this.addNamespaceCharsSubfolder.getText());
			VistAServerPrefs.setAddNamespaceCharsSubfolder(project, numChars);
		} catch (CoreException coreException) {
			StatusManager.getManager().handle(coreException, VistAServerPlugin.PLUGIN_ID);
		}
		return super.performOk();
	}	
}
