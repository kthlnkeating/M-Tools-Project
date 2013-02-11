package gov.va.med.foundations.security.vistalink;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;


/**
 * Swing Dialog to collect user input for a "select division" event
 * @see VistaLoginModule
 * @see CallbackHandlerSwing
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class DialogDivisionForm extends Dialog {

	private static final String DEFAULT_TITLE = "Select Division";

	private static final String OK_BUTTON_LABEL = "&OK";
	private static final String OK_BUTTON_TOOLTIP = "Submit your division choice to the server";

	private static final String HELP_BUTTON_LABEL = "&Help";
	private static final String HELP_BUTTON_TOOLTIP = "Ask for help on this dialog";
	
	private static final String CANCEL_BUTTON_LABEL = "&Cancel";
	private static final String CANCEL_BUTTON_TOOLTIP = "Cancel the login";

	private static final String DEFAULT_LABEL = "Must Select Division to Continue Sign On!";
	private static final String DEFAULT_LABEL_TOOLTIP = "You must select a division for this sign on";

	private static final String LIST_TOOLTIP = "List of Valid Divisions to select for Sign On";
//	private static final char LIST_MNEMONIC = KeyEvent.VK_D;
	
	private static final String HELP_MSG_1 = "Select a division from the list and click OK.";
	private static final String HELP_MSG_2 = "To abort the logon click Cancel, but sign on will not be completed.";

	private List listDivisions;
	private Label mainLabel;
	private Button helpButton;
	private Button okButton;
	private Button cancelButton;
	private CallbackSelectDivision divCbh;
	private Timer timer;

	/**
	 * Create a modal Swing dialog to display a list of divisions for user to select 1 from.
	 * @param parentFrame parent frame
	 * @param divCbh callback to retrieve information from and place result in
	 */
	static void showVistaAVSwingSelectDivision(CallbackSelectDivision divCbh) {
		DialogDivisionForm dialog = new DialogDivisionForm(divCbh);
		dialog.open();
	}
	
	DialogDivisionForm(Shell parent, int style) {
		super(parent, style);
	}
	
	DialogDivisionForm(Shell parent) {
		this(parent, 0);
	}

	private DialogDivisionForm(CallbackSelectDivision divCbh) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),0);
		this.divCbh = divCbh;

	}
	
	private void open() {
		final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		shell.setText(DEFAULT_TITLE);
		shell.setSize(260,250);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);


		mainLabel = new Label(shell,SWT.NONE);
		mainLabel.setText(DEFAULT_LABEL);
		mainLabel.setLocation(10,5);;
		mainLabel.setSize(200,20);

		listDivisions = new List(shell,SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		listDivisions.setLocation(10,40);
		listDivisions.setSize(235,120);
		
		TreeMap divisionList = (TreeMap) this.divCbh.getDivisionList();
		Vector divisionListData = new Vector();
		int defaultListString = -1;

		int count = -1;
		for (Iterator it = divisionList.keySet().iterator(); it.hasNext();) {
			String divisionNumber = (String) it.next();
			VistaInstitution myDivision = (VistaInstitution) divisionList.get(divisionNumber);
			String listString = "(" + myDivision.getNumber() + ") " + myDivision.getName();
			count++;
			listDivisions.add(listString);
			// Store off the element that matches the default division
			if (myDivision.getIsDefaultLogonDivision()) {
				defaultListString = count;
			}
		}
		if (defaultListString > -1) {
			listDivisions.select(defaultListString);
		}

		// make it so message window isn't tiny

		okButton = new Button(shell, SWT.PUSH);
		okButton.setText(OK_BUTTON_LABEL);
		okButton.setLocation(45,180);
		okButton.setSize(75,25);
		okButton.setFont(font);
		okButton.setToolTipText(OK_BUTTON_TOOLTIP);
		shell.setDefaultButton(okButton);
		
		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(CANCEL_BUTTON_LABEL);
		cancelButton.setFont(font);
		cancelButton.setToolTipText(CANCEL_BUTTON_TOOLTIP);
		cancelButton.setLocation(145,180);
		cancelButton.setSize(75,25);
		
/*
		getAccessibleContext().setAccessibleDescription(PANEL_ACCESSIBLE_DESCRIPTION);
*/

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == okButton) {
					okActionPerformed();
				}
				else if (event.widget == cancelButton) {
					cancelActionPerformed();
				}
				else {
					otherExit();
				}
				timer.cancel();
				shell.setVisible(false);
				shell.close();
			}
		};
		
		okButton.addListener(SWT.Selection, listener);
		cancelButton.addListener(SWT.Selection, listener);
		
		int delay = 1000 * divCbh.getTimeoutInSeconds();
		TimerTask taskPerformer = new TimerTask() {
			public void run() {
				doTimeout();
				shell.close();
			}
		};
		timer = new Timer();
		timer.schedule(taskPerformer,delay);
		
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	private void okActionPerformed() {
		storeSelectedDivision(listDivisions.getSelectionIndex(), listDivisions.getItem(listDivisions.getSelectionIndex()));
		this.divCbh.setSelectedOption(CallbackSelectDivision.KEYPRESS_OK);
	}

	/**
	 * Parse the selected value, match it with division value passed in the callback handler
	 * @param selectedIndex currently selected index in listbox
	 * @param selectedValue currently selected value in listbox
	 * @return boolean true if matched a division in the callback handler, false if not
	 */
	private boolean storeSelectedDivision(int selectedIndex, Object selectedValue) {
		boolean returnVal = false;
		TreeMap divisionList = (TreeMap) this.divCbh.getDivisionList();
		this.divCbh.setSelectedDivisionIen("-1");
		int index = -1;
		for (Iterator it = divisionList.keySet().iterator(); it.hasNext();) {
			String divisionNumber = (String) it.next();
			index++;
			VistaInstitution myDivision = (VistaInstitution) divisionList.get(divisionNumber);
			if ((selectedIndex == index)
				&& (selectedValue.equals("(" + myDivision.getNumber() + ") " + myDivision.getName()))) {
				this.divCbh.setSelectedDivisionIen(myDivision.getIen());
				returnVal = true;
			}
		}
		return returnVal;
	}

	private void cancelActionPerformed() {
		this.divCbh.setSelectedDivisionIen("-1");
		this.divCbh.setSelectedOption(CallbackSelectDivision.KEYPRESS_CANCEL);
	}

	/**
	 * if we timeout, set the action that closed the dialog to TIMEOUT and return
	 */
	private void doTimeout() {
		this.divCbh.setSelectedDivisionIen("-1");
		this.divCbh.setSelectedOption(CallbackSelectDivision.KEYPRESS_TIMEOUT);
	}
	
	private void otherExit() {
		divCbh.setSelectedOption(divCbh.KEYPRESS_TIMEOUT);
	}

}
