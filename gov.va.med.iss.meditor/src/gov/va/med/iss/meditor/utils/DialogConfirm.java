/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import gov.va.med.iss.meditor.MEditorPlugin;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Swing Dialog to display an error, informational message, help, or post-sign-in text to user,
 * and collect their response (OK or CANCEL, depending on type of message).
 * @see VistaLoginModule
 * @see CallbackHandlerSwing
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
final class DialogConfirm extends Dialog {

	private static final String OK_BUTTON_LABEL = "&OK";
//	private static final char OK_BUTTON_MNEMONIC = 'O';
	private static final String OK_BUTTON_TOOLTIP = "Sends OK confirmation to server";

	private static final String MESSAGE_AREA_TOOLTIP = "Message displayed by this dialog.";

	private static final String CANCEL_BUTTON_LABEL = "&Cancel";
//	private static final char CANCEL_BUTTON_MNEMONIC = 'C';
	private static final String CANCEL_BUTTON_TOOLTIP = "Sends cancel request to server";

	private static final String ERROR_LABEL = "&Error:";
//	private static final char ERROR_MNEMONIC = 'E';
//	private static final String ERROR_LOGO = "./images/error.gif";
	private static final String ERROR_LOGO_TOOLTIP = "Error logo";

	private static final String POST_TEXT_LABEL = "&Post-sign-in messages:";
//	private static final char POST_TEXT_MNEMONIC = 'P';
//	private static final String POST_TEXT_LOGO = "./images/yes1a.gif";
	private static final String POST_TEXT_LOGO_TOOLTIP = "Informational logo";

	private static final String SUCCESS_LABEL = "&Information:";
//	private static final char SUCCESS_MNEMONIC = 'I';
//	private static final String SUCCESS_LOGO = "./images/yes1a.gif";
	private static final String SUCCESS_LOGO_TOOLTIP = "Informational logo";

	private static final String HELP_LABEL = "&Help:";
//	private static final char HELP_MNEMONIC = 'H';
//	private static final String HELP_LOGO = "./images/helpbook07.gif";
	private static final String HELP_LOGO_TOOLTIP = "Help logo";

	private int messageMode;
	private CallbackConfirm ccCbh;

	private Button okButton;
	private Button cancelButton;
	private Button logoLabel;
	private Image logoImage;
	private Label messageTypeLabel;
	private Text messageTextArea;
	private Timer timer;

	/**
	 * Create a modal Swing dialog to present an error message to the user
	 * @param ccCbh CallbackConfirm to retrieve information from and place result in
	 */
	static void showDialogConfirm(CallbackConfirm ccCbh) {
		DialogConfirm dialog = new DialogConfirm(ccCbh);
		dialog.open();
	}
	
	DialogConfirm(Shell parent, int style) {
		super(parent, style);
	}
	
	DialogConfirm(Shell parent) {
		this(parent, 0);
	}

	private DialogConfirm(CallbackConfirm ccCbh) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),0);
		this.ccCbh = ccCbh;
		messageMode = ccCbh.getMessageMode();
	}

	private void doTimeout() {
		ccCbh.setSelectedOption(CallbackConfirm.KEYPRESS_TIMEOUT);
	}

	/**
	 * Provides a focus traversal policy for this dialog
	 */
/*
	class DialogConfirmFocusTraversalPolicy extends FocusTraversalPolicy {
*/
		/**
		 * get the next component in the focus traversal
		 * @param focusCycleRoot the root of the focus cycle
		 * @param aComponent currently focused component
		 * @return returns the next component in the (forward) cycle
		 */
/*
	public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {

			if (aComponent.equals(messageTextArea)) {
				return okButton;
			} else if (aComponent.equals(okButton)) {
				if (cancelButton != null) {
					return cancelButton;
				} else {
					return messageTextArea;
				}
			} else if (aComponent.equals(cancelButton)) {
				return messageTextArea;
			}
			return okButton;
		}

		/**
		 * get the previous (reverse direction) component in the focus traversal cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @param aComponent currently focused component
		 * @return returns the next component in the (reverse) cycle
		 */
/*
		public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {

			if (aComponent.equals(cancelButton)) {
				return okButton;
			} else if (aComponent.equals(okButton)) {
				return messageTextArea;
			} else if (aComponent.equals(messageTextArea)) {
				if (cancelButton != null) {
					return cancelButton;
				} else {
					return okButton;
				}
			}
			return okButton;
		}

		/**
		 * gets the default component to focus on
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the default component in the focus cycle
		 */
/*
		public Component getDefaultComponent(Container focusCycleRoot) {
			return okButton;
		}
		/**
		 * gets the last component in the focus cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the last component in the focus cycle
		 */
/*
		public Component getLastComponent(Container focusCycleRoot) {
			return cancelButton;
		}
		/**
		 * gets the first component in the focus cycle
		 * @param focusCycleRoot the root of the focus cycle
		 * @return the first component in the focus cycle
		 */
/*
		public Component getFirstComponent(Container focusCycleRoot) {
			return okButton;
		}
	}
	
*/	
	private void open() {
		final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		shell.setText(ccCbh.getWindowTitle());
		shell.setSize(450,250);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);

		if (this.messageMode == CallbackConfirm.ERROR_MESSAGE) {
//	        logoImage = new Image(shell.getDisplay(),DialogConfirm.class.getResourceAsStream(ERROR_LOGO));
			logoImage = MEditorPlugin.IMG_ERROR.createImage(shell.getDisplay());
			logoLabel = new Button(shell,SWT.BORDER);
			logoLabel.setImage(logoImage);
			logoLabel.setEnabled(false);
			logoLabel.setToolTipText(ERROR_LOGO_TOOLTIP);
			messageTypeLabel = new Label(shell,SWT.NO_FOCUS);
			messageTypeLabel.setText(ERROR_LABEL);
		} else if (this.messageMode == CallbackConfirm.INFORMATION_MESSAGE) {
//	        logoImage = new Image(shell.getDisplay(),DialogConfirm.class.getResourceAsStream(SUCCESS_LOGO));
			logoImage = MEditorPlugin.IMG_SUCCESS.createImage(shell.getDisplay());
			logoLabel = new Button(shell,SWT.BORDER);
			logoLabel.setImage(logoImage);
			logoLabel.setEnabled(false);
			logoLabel.setToolTipText(SUCCESS_LOGO_TOOLTIP);
			messageTypeLabel = new Label(shell, SWT.NO_FOCUS);
			messageTypeLabel.setText(SUCCESS_LABEL);
		} else if (this.messageMode == CallbackConfirm.HELP_MESSAGE) {
//	        logoImage = new Image(shell.getDisplay(),DialogConfirm.class.getResourceAsStream(HELP_LOGO));
			logoImage = MEditorPlugin.IMG_HELP.createImage(shell.getDisplay());
			logoLabel = new Button(shell,SWT.BORDER);
			logoLabel.setImage(logoImage);
			logoLabel.setEnabled(false);
			logoLabel.setToolTipText(HELP_LOGO_TOOLTIP);
			messageTypeLabel = new Label(shell,SWT.NO_FOCUS);
			messageTypeLabel.setText(HELP_LABEL);
		} else if (this.messageMode == CallbackConfirm.POST_TEXT_MESSAGE) {
//	        logoImage = new Image(shell.getDisplay(),DialogConfirm.class.getResourceAsStream(POST_TEXT_LOGO));
			logoImage = MEditorPlugin.IMG_POST_TEXT.createImage(shell.getDisplay());
			logoLabel = new Button(shell,SWT.BORDER);
			logoLabel.setImage(logoImage);
			logoLabel.setEnabled(false);
			logoLabel.setToolTipText(POST_TEXT_LOGO_TOOLTIP);
			messageTypeLabel = new Label(shell, SWT.NO_FOCUS);
			messageTypeLabel.setText(POST_TEXT_LABEL);
		}

		logoLabel.setLocation(10,5);
		logoLabel.setSize(30,30);
		messageTypeLabel.setLocation(50,20);
		messageTypeLabel.setSize(300,20);
		messageTypeLabel.setFont(font);
		
		// create the text
		StringBuffer sb = new StringBuffer();
		Vector vectorMsgText = ccCbh.getDisplayMessages();

		for (int i = 0; i < vectorMsgText.size(); i++) {
			sb.append((String) vectorMsgText.get(i));
			sb.append("\n");
		}
		messageTextArea = new Text(shell,SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		messageTextArea.setText(sb.toString());
		messageTextArea.setEditable(false);
		messageTextArea.setToolTipText(MESSAGE_AREA_TOOLTIP);
		messageTextArea.setLocation(10,70);
		messageTextArea.setSize(420,100);
//		messageTextArea.getAccessibleContext().setAccessibleName(MESSAGE_AREA_TOOLTIP);
/*

		messageTextArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				messageScrollPane.setBorder(focusBorder);
			}
			public void focusLost(FocusEvent e) {
				messageScrollPane.setBorder(noFocusBorder);
			}
		});
*/
		// make it so message window isn't tiny
		if (messageTextArea.getLineCount() < 3) {
			messageTextArea.append("\n\n");
		}

		okButton = new Button(shell, SWT.PUSH);
		okButton.setText(OK_BUTTON_LABEL);
		okButton.setLocation(25,150);
		okButton.setSize(75,25);
		okButton.setFont(font);
		okButton.setToolTipText(OK_BUTTON_TOOLTIP);
		shell.setDefaultButton(okButton);
		okButton.setLocation(185,190);
		okButton.setSize(75,25);
		
		// set the accessible name of the OK button based on the dialog contents
		StringBuffer sb1 = new StringBuffer();
		sb1.append(okButton.getText());
		sb1.append(" button: ");
		sb1.append(messageTypeLabel.getText());
		sb1.append(messageTextArea.getText());
//		okButton.getAccessibleContext().setAccessibleName(sb.toString());

		// if it's an error message, they can CANCEL. If information, can't cancel.
		if (this.messageMode == CallbackConfirm.ERROR_MESSAGE) {
			cancelButton = new Button(shell, SWT.PUSH);
			cancelButton.setText(CANCEL_BUTTON_LABEL);
			cancelButton.setLocation(125,150);
			cancelButton.setSize(75,25);
			cancelButton.setFont(font);
			cancelButton.setToolTipText(CANCEL_BUTTON_TOOLTIP);
			okButton.setLocation(100,190);
			cancelButton.setLocation(250,190);
			cancelButton.setSize(75,25);
		}
		
/*
		getAccessibleContext().setAccessibleDescription(PANEL_ACCESSIBLE_DESCRIPTION);
*/

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.widget == okButton) {
					okBtnSelected();
				}
				else if (event.widget == cancelButton) {
					cancelBtnSelected();
				}
				else {
					otherExit();
				}
				timer.cancel();
				shell.close();
			}
		};
		
		okButton.addListener(SWT.Selection, listener);
		
		// if it's an error message, they can CANCEL. If information, can't cancel.
		if (this.messageMode == CallbackConfirm.ERROR_MESSAGE) {
			cancelButton.addListener(SWT.Selection, listener);
		}
		
		
		int delay = 1000 * ccCbh.getTimeoutInSeconds();
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
		ccCbh.setSelectedOption(CallbackConfirm.KEYPRESS_OK);
	}
	
	private void cancelBtnSelected() {
		ccCbh.setSelectedOption(CallbackConfirm.KEYPRESS_CANCEL);
	}
	
	private void otherExit() {
		ccCbh.setSelectedOption(CallbackConfirm.KEYPRESS_TIMEOUT);
	}

}