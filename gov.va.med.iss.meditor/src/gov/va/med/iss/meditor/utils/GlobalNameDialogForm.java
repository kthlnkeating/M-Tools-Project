package gov.va.med.iss.meditor.utils;

/*
 * 080604 Increased width of Global Name field to display more characters.
 *        Modified Global Name field from a Text component to Combo.
 *        Added drop down list to Global Name field.
 */


// import java.awt.Component;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GlobalNameDialogForm {
	
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the global name(s) that are to be listed ";
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	private static final String GLOBALNAME_LABEL_TEXT = "&Global Name: ";
/*
	private static final String GLOBALNAME_BUTTON_TEXT = "&OK";
	private static final String GLOBALNAME_TOOLTIP = "Press after entering the desired global name";
	private static final String GLOBALSAVE_BUTTON_TEXT = "&Cancel";
	private static final String GLOBALSAVE_TOOLTIP = "Press to cancel the operation";
*/
	private static final String GLOBALCOPY_TOOLTIP = "Check to have the list presented in a form that can be copied and pasted into another account.";
	private static ArrayList<String> dropList = null;

	private Label lblQuestion;
	private Combo comboResponse;
	private Button btnOK;
	private Button btnCancel;
	private Button btnUpperCase;
	private Button checkboxCopy;
	private Button checkboxNormal;
	private Button checkboxDataOnly;
	private Label lblServer;
	private Label lblPort;
	private Text txtServer;
	private Text txtPort;
	private String dialogType;
	private String selectedGlobalName;
	private Group groupListType;
	private Group grouptxtSearchText;
	private Label lblSearchText;
	private Text txtSearchText;
	private Button radioSearchDataOnly;
	private Button radioSearchDataAndNodes;
	private Button chkboxSearchCaseSensitive;
	public boolean isCopy = false;
	public boolean isDataOnly = false;
	public String searchText = "";
	public boolean searchDataOnly = true;
	public boolean searchCaseSensitive = true;
	
	public GlobalNameDialogForm() {
		super ();
		if (dropList == null) {
			dropList = new ArrayList<String>();
		}

	}
	
	public String getGlobalName(String dialogType) {
		selectedGlobalName = "-1";
		this.dialogType = dialogType;
		open();
		return selectedGlobalName;
	}
/*
	public GlobalNameDialogForm(Shell parent, int style) {
		super(parent, style);
	}
	
	public GlobalNameDialogForm(Shell parent) {
		this(parent, 0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	private void open() {
		final Shell shell = new Shell(MEditorUtilities.getIWorkbenchWindow().getShell(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		int sizeOffset = 105;
		shell.setText("Global Name Dialog");
		if (dialogType.compareTo("GL") == 0) {
			sizeOffset = 0;
			shell.setText("Global Listing Dialog");
		}
		shell.setSize(300,180);  // ,280-sizeOffset);
		
		lblQuestion = new Label(shell, SWT.RIGHT);
		lblQuestion.setLocation(10,10);
		lblQuestion.setSize(80,20);
		lblQuestion.setText(GLOBALNAME_LABEL_TEXT);
		
		comboResponse = new Combo(shell, SWT.DROP_DOWN);
		comboResponse.setLocation(90,10);
		comboResponse.setSize(200,20);
		comboResponse.setToolTipText(GLOBALNAME_TEXTFIELD_TOOLTIP1);
	    if (dropList.size() > 0) {
	    	for (int i=0 ; i < dropList.size(); i++) {
	    		comboResponse.add((String)dropList.get(i));
	    	}
	    }
		
		lblPort = new Label(shell, SWT.LEFT);
		lblPort.setText("Port: ");
		lblPort.setLocation(25,35); // ,173-sizeOffset);
		lblPort.setSize(25,20);
		
		txtPort = new Text(shell, SWT.LEFT);
		txtPort.setText(ConnectionUtilities.getPort());
		txtPort.setLocation(50,35);  // ,173-sizeOffset);
		txtPort.setSize(35,20);     // (200,20);
		txtPort.setEditable(false);

		lblServer = new Label(shell, SWT.LEFT);
		lblServer.setText("Server: ");
		lblServer.setLocation(95,35);  // ,155-sizeOffset);
		lblServer.setSize(40,20);
		
		txtServer = new Text(shell, SWT.LEFT);
		txtServer.setText(ConnectionUtilities.getServer());
		txtServer.setLocation(135,35);  // 90,155-sizeOffset);
		txtServer.setSize(200,20);
		txtServer.setEditable(false);

		btnUpperCase = new Button(shell, SWT.CHECK);
		btnUpperCase.setText("Set name to ALL Caps");
		btnUpperCase.setLocation(25,50);  // ,185-sizeOffset);
		btnUpperCase.setSize(150,25);
		btnUpperCase.setSelection(true);
		
		if (dialogType.compareTo("GL") == 0) {
			shell.setSize(300,285);
			comboResponse.setToolTipText(GLOBALNAME_TEXTFIELD_TOOLTIP2);
			groupListType = new Group(shell,SWT.SHADOW_ETCHED_IN);
			groupListType.setText("Return Types");
			groupListType.setLocation(40,75);
			groupListType.setSize(200,55);
			
			checkboxNormal = new Button(groupListType,SWT.RADIO);
			checkboxNormal.setText("Normal");
			checkboxNormal.setSelection(true);
			checkboxNormal.setToolTipText("Normal Listing of global nodes and data");
			checkboxNormal.setLocation(5,12);
			checkboxNormal.setSize(50,20);
			checkboxDataOnly = new Button(groupListType,SWT.RADIO);
			checkboxDataOnly.setText("Data Only");
			checkboxDataOnly.setToolTipText("List data only (no global nodes)");
			checkboxDataOnly.setLocation(80,15);
			checkboxDataOnly.setSize(100,20);
			checkboxCopy = new Button(groupListType,SWT.RADIO);
			checkboxCopy.setText("Setup for copying");
			checkboxCopy.setSelection(false);
			checkboxCopy.setToolTipText(GLOBALCOPY_TOOLTIP);
			checkboxCopy.setLocation(5,30);
			checkboxCopy.setSize(110,20);
			
			grouptxtSearchText = new Group(shell,SWT.SHADOW_ETCHED_IN);
			grouptxtSearchText.setText("Text Search");
			grouptxtSearchText.setLocation(40,135);
			grouptxtSearchText.setSize(200,72);
			
			lblSearchText = new Label(grouptxtSearchText,SWT.LEFT);
			lblSearchText.setText("Search Text:");
			lblSearchText.setLocation(10,13);
		    lblSearchText.setSize(60,20);
		    
			txtSearchText = new Text(grouptxtSearchText,SWT.BORDER);
			txtSearchText.setText("");
			txtSearchText.setLocation(90,10);
			txtSearchText.setSize(100,20);
			txtSearchText.setToolTipText("Enter text to return only those global nodes where the global nodes or data contain the entered text.");
			
			chkboxSearchCaseSensitive = new Button(grouptxtSearchText,SWT.CHECK);
			chkboxSearchCaseSensitive.setText("Case Sensitive");
			chkboxSearchCaseSensitive.setSelection(true);
			chkboxSearchCaseSensitive.setToolTipText("Check if search for text is to be CASE SENSITIVE (i.e., upper and lower case are different) if 'Search Text' is not null.");
			chkboxSearchCaseSensitive.setLocation(10,29);
			chkboxSearchCaseSensitive.setSize(90,20);

			radioSearchDataOnly = new Button(grouptxtSearchText,SWT.RADIO);
			radioSearchDataOnly.setText("Data Only");
			radioSearchDataOnly.setSelection(true);
			radioSearchDataOnly.setToolTipText("Check if search to be in data only (if 'Search Text' is not null).");
			radioSearchDataOnly.setLocation(10,49);
			radioSearchDataOnly.setSize(80,20);

			radioSearchDataAndNodes = new Button(grouptxtSearchText,SWT.RADIO);
			radioSearchDataAndNodes.setText("Data or Globals");
			radioSearchDataAndNodes.setSelection(false);
			radioSearchDataAndNodes.setToolTipText("Check if search to be in data or globals (if 'Search Text' is not null).");
			radioSearchDataAndNodes.setLocation(100,49);
			radioSearchDataAndNodes.setSize(90,20);
		}
		
		btnOK = new Button(shell, SWT.PUSH);
		btnOK.setText("&OK");
		btnOK.setLocation(80,215-sizeOffset);
		btnOK.setSize(55,25);
		shell.setDefaultButton(btnOK);
		
		btnCancel = new Button(shell, SWT.PUSH);
		btnCancel.setText("&Cancel");
		btnCancel.setLocation(165,215-sizeOffset);
		btnCancel.setSize(55,25);
		
		
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				String strInput = "";
				if (event.widget == btnOK) {
					strInput = comboResponse.getText();
					if (btnUpperCase.getSelection()) {
						strInput = strInput.toUpperCase();
					}
					selectedGlobalName = strInput;
		    		   int index = dropList.indexOf(selectedGlobalName);
		    		   if (index > -1) {
		    			   dropList.remove(index);
		    		   }
		    		   dropList.add(0,selectedGlobalName);
					if (dialogType.compareTo("GL") == 0) {
						isCopy = checkboxCopy.getSelection();
					    isDataOnly = checkboxDataOnly.getSelection();
					    searchText = txtSearchText.getText();
					    searchDataOnly = radioSearchDataOnly.getSelection();
					    searchCaseSensitive = chkboxSearchCaseSensitive.getSelection();
					}
				}
				shell.setVisible(false);
				shell.close();
			}
		};
		
		btnOK.addListener(SWT.Selection, listener);
		btnCancel.addListener(SWT.Selection, listener);
		
		shell.open();
		Display display = MEditorUtilities.getIWorkbenchWindow().getShell().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}

}
