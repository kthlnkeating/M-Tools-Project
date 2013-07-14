package gov.va.med.foundations.security.vistalink;

import java.awt.Frame;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Swing Dialog to collect user input for a "change verify code" event
 * @see VistaLoginModule
 * @see CallbackHandlerSwing
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class DialogChangeVcForm extends Dialog {

	private static final String DEFAULT_TITLE = "Change VISTA Verify Code";

	private static final String PANEL_TITLE = "You need to enter a new verify code.";
	@SuppressWarnings("unused")
	private static final String PANEL_ACCESSIBLE_DESCRIPTION = "This dialog is prompting you to change your verify code.";

	private static final String OLD_VERIFY_LABEL = "Ol&d Verify Code: ";
	@SuppressWarnings("unused")
	private static final char OLD_VERIFY_MNEMONIC = 'D';
	private static final String OLD_VERIFY_TOOLTIP = "Enter Current (Old) Verify Code";

	private static final String NEW_VERIFY_LABEL = "&New Verify Code: ";
	@SuppressWarnings("unused")
	private static final char NEW_VERIFY_MNEMONIC = 'N';
	private static final String NEW_VERIFY_TOOLTIP = "Enter New Verify Code";

	private static final String NEW_VERIFY_CHECK_LABEL = "Confirm New Veri&fy Code: ";
	@SuppressWarnings("unused")
	private static final char NEW_VERIFY_CHECK_MNEMONIC = 'F';
	private static final String NEW_VERIFY_CHECK_TOOLTIP = "Re-Enter New Verify Code";

	private static final String OK_BUTTON_LABEL = "&OK";
	@SuppressWarnings("unused")
	private static final char OK_BUTTON_MNEMONIC = 'O';
	private static final String OK_BUTTON_TOOLTIP = "Submits Request to Server to Change Verify Code";

	private static final String CANCEL_BUTTON_LABEL = "&Cancel";
	@SuppressWarnings("unused")
	private static final char CANCEL_BUTTON_MNEMONIC = 'C';
	private static final String CANCEL_BUTTON_TOOLTIP = "Cancels the Change Verify Code request";

	private static final String HELP_BUTTON_LABEL = "&Help";
	@SuppressWarnings("unused")
	private static final char HELP_BUTTON_MNEMONIC = 'H';
	private static final String HELP_BUTTON_TOOLTIP = "Get Help on Changing Verify Code";

	private static final String HELP_MSG_1 = "Enter a new verify code and then confirm it.";

	private Text oldVerify;
	private Text newVerify;
	private Text newVerifyCheck;
	private Button help;
	private Button ok;
	private Button cancel;
	@SuppressWarnings("unused")
	private List lstDivisions;
	@SuppressWarnings("unused")
	private Label lblList;
	private Label oldVerifyLabel;
	private Label newVerifyLabel;
	private Label newVerifyCheckLabel;
	private CallbackChangeVc cvcCbh;
	@SuppressWarnings("unused")
	private Frame parentFrame;
	private Shell thisShell;
	@SuppressWarnings("unused")
	private Font font;
	private Timer timer;
	
	DialogChangeVcForm(Shell parent, int style) {
		super(parent,style);
	}
	
	DialogChangeVcForm(Shell parent) {
		this(parent, 0);
	}

	public DialogChangeVcForm(CallbackChangeVc cbCVc) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),0);
		cvcCbh = cbCVc;
	}

	public DialogChangeVcForm(Frame parent, CallbackChangeVc cbCVc) {
		this(cbCVc);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Display display = new Display();
		CallbackChangeVc cbCVc = new CallbackChangeVc("String Message","String Help",600,false);
		DialogChangeVcForm.showVistaAVChangeVC(cbCVc);
	}

	/**
	 * Create a modal Swing dialog to collect "change verify code" information from the user
	 * @param parent parent frame
	 * @param cvcCbh callback to retrieve information from and place result in
	 */
	static void showVistaAVChangeVC(CallbackChangeVc cvcCbh) {
		DialogChangeVcForm dialog = new DialogChangeVcForm(cvcCbh);
		dialog.open();
	}
	private void doTimeout() {
		this.cvcCbh.setOldVerifyCode(null);
		this.cvcCbh.setNewVerifyCode(null);
		this.cvcCbh.setNewVerifyCodeCheck(null);
		this.cvcCbh.setSelectedOption(CallbackChangeVc.KEYPRESS_TIMEOUT);
		thisShell.setVisible(false);
		thisShell.dispose();
	}

	/**
	 * Provides a focus traversal policy for this dialog
	 */
/*
	class DialogChangeVcFocusTraversalPolicy extends FocusTraversalPolicy {

		/**
		 * get the next component in the focus traversal
		 * @param focusCycleRoot the root of the focus cycle
		 * @param aComponent currently focused component
		 * @return returns the next component in the (forward) cycle
		 */
/*
	public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {

			if (aComponent.equals(oldVerify)) {
				return newVerify;
			} else if (aComponent.equals(newVerify)) {
				return newVerifyCheck;
			} else if (aComponent.equals(newVerifyCheck)) {
				return ok;
			} else if (aComponent.equals(ok)) {
				return cancel;
			} else if (aComponent.equals(cancel)) {
				return help;
			} else if (aComponent.equals(help)) {
				return oldVerify;

				// Now for the "outside normal tab cycle" cases //

			} else if (aComponent.equals(oldVerifyLabel)) {
				return oldVerify;
			} else if (aComponent.equals(newVerifyLabel)) {
				return newVerify;
			} else if (aComponent.equals(newVerifyCheckLabel)) {
				return newVerifyCheck;
			}
			return oldVerify;
		}

		/**
		 * get the previous (reverse direction) component in the focus traversal cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @param aComponent currently focused component
		 * @return returns the next component in the (reverse) cycle
		 */
/*
		public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {

			if (aComponent.equals(help)) {
				return cancel;
			} else if (aComponent.equals(cancel)) {
				return ok;
			} else if (aComponent.equals(ok)) {
				return newVerifyCheck;
			} else if (aComponent.equals(newVerifyCheck)) {
				return newVerify;
			} else if (aComponent.equals(newVerify)) {
				return oldVerify;
			} else if (aComponent.equals(oldVerify)) {
				return help;

				// Now for the "outside normal tab cycle" cases 

			} else if (aComponent.equals(newVerifyCheckLabel)) {
				return newVerify;
			} else if (aComponent.equals(newVerifyLabel)) {
				return oldVerify;
			} else if (aComponent.equals(oldVerifyLabel)) {
				return help;
			}
			return oldVerify;
		}

		/**
		 * gets the default component to focus on
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the default component in the focus cycle
		 */
/*
		public Component getDefaultComponent(Container focusCycleRoot) {
			return oldVerify;
		}
		
		/**
		 * gets the last component in the focus cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the last component in the focus cycle
		 */
/*
		public Component getLastComponent(Container focusCycleRoot) {
			return help;
		}
		
		/**
		 * gets the first component in the focus cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the first component in the focus cycle
		 */
/*
		public Component getFirstComponent(Container focusCycleRoot) {
			return oldVerify;
		}
	}
*/
	private void open() {
		final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		shell.setText(DEFAULT_TITLE);
		shell.setSize(375,250);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);

		Group group = new Group(shell,SWT.NONE);
		group.setText(PANEL_TITLE);
		group.setLocation(20,10);
		group.setSize(325,200);
		group.setFont(font);
		
		oldVerifyLabel = new Label(group, SWT.RIGHT);
		oldVerifyLabel.setText(OLD_VERIFY_LABEL);
		oldVerifyLabel.setLocation(10,30);
		oldVerifyLabel.setSize(105,20);
		oldVerifyLabel.setFont(font);
		
		oldVerify = new Text(group, SWT.BORDER | SWT.PASSWORD);
		oldVerify.setText("");
		oldVerify.setLocation(130,30);
		oldVerify.setSize(150,20);
		oldVerify.setFont(font);
		oldVerify.setFocus();
		oldVerify.setToolTipText(OLD_VERIFY_TOOLTIP);
		
		newVerifyLabel = new Label(group, SWT.RIGHT);
		newVerifyLabel.setText(NEW_VERIFY_LABEL);
		newVerifyLabel.setLocation(10,60);
		newVerifyLabel.setSize(105,20);
		newVerifyLabel.setFont(font);
		
		newVerify = new Text(group, SWT.BORDER | SWT.PASSWORD);
		newVerify.setText("");
		newVerify.setLocation(130,60);
		newVerify.setSize(150,20);
		newVerify.setFont(font);
		newVerify.setToolTipText(NEW_VERIFY_TOOLTIP);
		
		newVerifyCheckLabel = new Label(group, SWT.RIGHT);
		newVerifyCheckLabel.setText(NEW_VERIFY_CHECK_LABEL);
		newVerifyCheckLabel.setLocation(10,100);
		newVerifyCheckLabel.setSize(105,20);
		newVerifyCheckLabel.setFont(font);
		
		newVerifyCheck = new Text(group, SWT.BORDER | SWT.PASSWORD);
		newVerifyCheck.setText("");
		newVerifyCheck.setLocation(130,100);
		newVerifyCheck.setSize(150,20);
		newVerifyCheck.setFont(font);
		newVerifyCheck.setToolTipText(NEW_VERIFY_CHECK_TOOLTIP);
		
	
		ok = new Button(group, SWT.PUSH);
		ok.setText(OK_BUTTON_LABEL);
		ok.setLocation(25,150);
		ok.setSize(75,25);
		ok.setFont(font);
		ok.setToolTipText(OK_BUTTON_TOOLTIP);
		shell.setDefaultButton(ok);
		
		cancel = new Button(group, SWT.PUSH);
		cancel.setText(CANCEL_BUTTON_LABEL);
		cancel.setLocation(125,150);
		cancel.setSize(75,25);
		cancel.setFont(font);
		cancel.setToolTipText(CANCEL_BUTTON_TOOLTIP);
		
		help = new Button(group, SWT.PUSH);
		help.setText(HELP_BUTTON_LABEL);
		help.setLocation(225,150);
		help.setSize(75,25);
		help.setFont(font);
		help.setToolTipText(HELP_BUTTON_TOOLTIP);
/*
		getAccessibleContext().setAccessibleDescription(PANEL_ACCESSIBLE_DESCRIPTION);
*/
		// disable oldVerify if the user logged on without entering a verify code
		// (meaning that their verify code had been deleted, so, no reason to prompt for it)
		if (cvcCbh.getEnteredVerifyCodeWasNull()) {
			oldVerify.setEditable(false);
			newVerify.setFocus();
		} else {
			oldVerify.setFocus();
		}
		
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == ok) {
					okBtnSelected();
				}
				else if (event.widget == cancel) {
					cancelBtnSelected();
				}
				else {
					otherExit();
				}
				timer.cancel();
				shell.setVisible(false);
				shell.close();
			}
		};
		
		ok.addListener(SWT.Selection, listener);
		cancel.addListener(SWT.Selection, listener);
		
		Listener helpListener = new Listener() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void handleEvent(Event event) {
				Vector messageVector = new Vector();
				messageVector.add(HELP_MSG_1);
				messageVector.add("");
				messageVector.add(cvcCbh.getCvcHelpText());
				CallbackConfirm ccbh =
					new CallbackConfirm(
						messageVector,
						CallbackConfirm.HELP_MESSAGE,
						"Change Verify Code Help",
						cvcCbh.getTimeoutInSeconds());
				DialogConfirmForm.showDialogConfirm(ccbh);
				if (ccbh.getSelectedOption() == CallbackConfirm.KEYPRESS_TIMEOUT) {
					doTimeout();
				} else {
					oldVerify.setFocus();
				}
			}
		};
		help.addListener(SWT.Selection, helpListener);
		
		int delay = 1000 * cvcCbh.getTimeoutInSeconds();
		TimerTask taskPerformer = new TimerTask() {
			public void run() {
				doTimeout();
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
	
	private void okBtnSelected() {
		cvcCbh.setSelectedOption(CallbackChangeVc.KEYPRESS_OK);
		cvcCbh.setOldVerifyCode(oldVerify.getText().toCharArray());
		cvcCbh.setNewVerifyCode(newVerify.getText().toCharArray());
		cvcCbh.setNewVerifyCodeCheck(newVerifyCheck.getText().toCharArray());
	}
	
	private void cancelBtnSelected() {
		cvcCbh.setSelectedOption(CallbackChangeVc.KEYPRESS_CANCEL);
	}
	
	private void otherExit() {
		cvcCbh.setSelectedOption(CallbackChangeVc.KEYPRESS_TIMEOUT);
	}
}
