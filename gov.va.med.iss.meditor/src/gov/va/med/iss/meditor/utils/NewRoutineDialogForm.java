/*
 * Created on Aug 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;
/*
import org.eclipse.core.resources.IContainer;

import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.StringBuffer;
import java.util.HashMap;
*/
import java.lang.String;
import org.eclipse.swt.widgets.Shell;
/*
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
*/
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Font;
// import org.eclipse.swt.layout.GridLayout;
// import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;


/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewRoutineDialogForm {
	
	private String routineName;
	private boolean boolValue = true;
	private Button cancelButton;
	private Button okButton;
	private String resultStr;
	
	public String askNewRoutine(String routineName) {
		this.routineName = routineName;
/*
 		dialog = new JDialog((JFrame)null,"Routine "+routineName + " Not Found!!", true);
		Component component = createComponents();
		dialog.getContentPane().add(component);
		dialog.setSize(700,400);
		dialog.getRootPane().setDefaultButton(cancelButton);
		dialog.show();
*/
		open();
		return resultStr;
	}

/*	
	private static final int TEXT_FIELD_COLUMNS = 60;
	private static final int UNIT_TEST_FIELD_COLUMNS = 8;
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the global name(s) that are to be listed ";
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
*/
	private static final String NO_ROUTINE_TEXT = "The specified routine was not found---Fill in the fields below to create a new routine";
	private static final String GLOBALNAME_BUTTON_TEXT = "OK";
	private static final String GLOBALNAME_TOOLTIP = "Press after entering the requested information to create a new routine";
	private static final String GLOBALSAVE_BUTTON_TEXT = "Cancel";
	private static final String GLOBALSAVE_TOOLTIP = "Press to cancel the operation";
	Text firstLineTextField;
	Text secondLineTextField;
	Text unitTestTextField;
	Label warningLabel;
	Button updateRoutineFileEntryCheckBox;
	Text globalNameNamespaceTextField;
/*	
	private JPanel mainPanel;
	public Component createComponents() {

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
*/
	private void open() {
		final Shell shell = new Shell(MEditorUtilities.getIWorkbenchWindow().getShell(),SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText("Routine "+routineName + " Not Found!!");
		shell.setLocation(0,0);
		shell.setSize(650,300);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);

		
		Label warningLabel = new Label(shell, SWT.NO_FOCUS | SWT.CENTER);
		warningLabel.setText(NO_ROUTINE_TEXT);
		warningLabel.setLocation(10,10);
		warningLabel.setSize(630,20);
	
		Label rpcListLabel = new Label(shell, SWT.CENTER);
		rpcListLabel.setText("initials and description for first line");
		rpcListLabel.setLocation(10,40);
		rpcListLabel.setSize(630,20);
		rpcListLabel.setFont(font);

		firstLineTextField = new Text(shell,SWT.BORDER);
		firstLineTextField.setLocation(10,60);
		firstLineTextField.setSize(630,20);
		firstLineTextField.setFont(font);
		
		Label secondLineLabel = new Label(shell, SWT.CENTER);
		secondLineLabel.setText("Enter 2nd line data - Version#(with one decimal place);PackageName;PatchInfo(if applicable);DateOfRelease");
		secondLineLabel.setLocation(10,100);
		secondLineLabel.setSize(630,20);
		secondLineLabel.setFont(font);

		secondLineTextField = new Text(shell, SWT.BORDER);
		secondLineTextField.setLocation(10,130);
		secondLineTextField.setSize(630,20);
		secondLineTextField.setFont(font);				
		
		Label unitTestRoutineLabel = new Label(shell, SWT.RIGHT);
		unitTestRoutineLabel.setText("Enter a Unit Test routine (optional)");
		unitTestRoutineLabel.setLocation(10,160);
		unitTestRoutineLabel.setSize(270,20);

		unitTestTextField = new Text(shell, SWT.BORDER);
		unitTestTextField.setLocation(285,160);
		unitTestTextField.setSize(90,20);
		unitTestTextField.setFont(font);
						
		
		Button updateCheckBox = new Button(shell, SWT.CHECK);
		updateCheckBox.setText("Update Entry in Routine File on Save");
		updateCheckBox.setLocation(400,160);
		updateCheckBox.setSize(200,20);
		updateCheckBox.setSelection(true);

		okButton = new Button(shell,SWT.PUSH);
		okButton.setText(GLOBALNAME_BUTTON_TEXT);
		okButton.setToolTipText(GLOBALNAME_TOOLTIP);
		okButton.setLocation(195,210);
		okButton.setSize(75,25);

		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(GLOBALSAVE_BUTTON_TEXT);
		cancelButton.setToolTipText(GLOBALSAVE_TOOLTIP);
		cancelButton.setLocation(320,210);
		cancelButton.setSize(75,25);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == okButton)
					doOkSelect();
				else
					doCancelSelect();
				shell.setVisible(false);
				shell.close();
			}
		};
		
		okButton.addListener(SWT.Selection, listener);
		cancelButton.addListener(SWT.Selection, listener);
		
		shell.open();
		Display display = MEditorUtilities.getIWorkbenchWindow().getShell().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	public void doOkSelect() {
		// for some reason can't read the state of the checkbox to set boolValue, put in work around instead.
		resultStr = firstLineTextField.getText()+"~^~"+secondLineTextField.getText()+"~^~"+unitTestTextField.getText()+"~^~";
		if (boolValue)
			resultStr = resultStr + "1";
		else
			resultStr = resultStr + "0";
	}
	
	public void doCancelSelect() {
		resultStr = "";
	}
/*	
	private void okDefaultButton() {
		dialog.getRootPane().setDefaultButton(okButton);
	}
	
    /** Handle the key typed event from the text field. */
/*
	public void keyTyped(KeyEvent e) {
        okDefaultButton();
    }

    /** Handle the key pressed event from the text field. */
/*
	public void keyPressed(KeyEvent e) {
    }

    /** Handle the key released event from the text field. */
/*
	public void keyReleased(KeyEvent e) {
    }
*/


}
