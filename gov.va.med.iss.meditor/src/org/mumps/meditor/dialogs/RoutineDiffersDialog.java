package org.mumps.meditor.dialogs;

import gov.va.med.iss.meditor.utils.MEditorUtilities;
import gov.va.med.iss.meditor.utils.RoutineCompare;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class RoutineDiffersDialog extends Dialog implements Listener {
	
	//SWT components
	private Button viewDiffButton;
	private Label messageLabel;

	//Object state
	private String file1;
	private String file2;
	private String routineName;
	private String dialogMessage;
	private String comparingTo;
	
	public RoutineDiffersDialog(Shell parentShell, String dialogMessage, String comparingTo, String routineName, String file1, String file2) {
		super(parentShell);
		
		this.dialogMessage = dialogMessage;
		this.comparingTo = comparingTo;
		this.routineName = routineName;
		this.file1 = file1;
		this.file2 = file2;
	}
	
	@Override
	   protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText("MEditor Routine Compare");
	   }

	@Override
	protected Control createDialogArea(Composite parent) {
		//Composite composite = (Composite) super.createDialogArea(parent);
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		top.setLayout(layout);

		GridData gridData1 = new GridData();
		messageLabel = new Label(top, SWT.WRAP);
		messageLabel.setText(dialogMessage);
		gridData1 = new GridData(GridData.FILL_BOTH);
		gridData1.widthHint = 400;
		messageLabel.setLayoutData(gridData1);
		
		return top;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		viewDiffButton = new Button(parent, SWT.PUSH);
		viewDiffButton.setText("View differences");
		viewDiffButton.addListener(SWT.Selection, this);
		super.createButtonsForButtonBar(parent);
		parent.setLayout(new GridLayout(3, false));
	}

	@Override
	public void handleEvent(Event arg0) {
		try {
			RoutineCompare.compareRoutines(file1,file2,comparingTo,routineName); //TODO: replace "" with messageInHTML
		} catch (Exception e) {
			MessageDialog.openInformation(
					MEditorUtilities.getIWorkbenchWindow().getShell(),
					"M-Editor Plug-in",
					"Error encountered while comparing versions on server "+e.getMessage());
		}
	}
	
}
