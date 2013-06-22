package gov.va.med.iss.meditor.utils;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * User authentication dialog
 */
public class UserValidationDialog extends Dialog {
	protected Text usernameField;
	protected Text passwordField;

	protected String host;
	protected String message;
//	protected Authentication userAuthentication = null;
	/**
	 * Gets user and password from a user. May be called from any thread
	 * 
	 * @return UserAuthentication that contains the userid and the password or
	 *         <code>null</code> if the dialog has been cancelled
	 */
/*
	public static Authentication getAuthentication(final String host,
			final String message) {
		class UIOperation implements Runnable {
			public Authentication authentication;
			public void run() {
				authentication = UserValidationDialog.askForAuthentication(
						host, message);
			}
		}

		UIOperation uio = new UIOperation();
		if (Display.getCurrent() != null) {
			uio.run();
		} else {
			Display.getDefault().syncExec(uio);
		}
		return uio.authentication;
	}
*/
	/**
	 * Gets user and password from a user Must be called from UI thread
	 * 
	 * @return UserAuthentication that contains the userid and the password or
	 *         <code>null</code> if the dialog has been cancelled
	 */
/*
	protected static Authentication askForAuthentication(String host,
			String message) {
		UserValidationDialog ui = new UserValidationDialog(null, host, message); //$NON-NLS-1$
		ui.open();
		return ui.getAuthentication();
	}
*/
	/**
	 * Creates a new UserValidationDialog.
	 * 
	 * @param parentShell
	 *            parent Shell or null
	 */
	protected UserValidationDialog(Shell parentShell, String host,
			String message) {
		super(parentShell);
		this.host = host;
		this.message = message;
		setBlockOnOpen(true);
	}

	/**
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("my Text");   // (UpdateUIMessages.UserVerificationDialog_PasswordRequired); //$NON-NLS-1$
	}
	/**
	 */
	public void create() {
		super.create();
		//give focus to username field
		usernameField.selectAll();
		usernameField.setFocus();
	}
	/**
	 */
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.WRAP);
		String text = "My String Text"; // UpdateUIMessages.UserVerificationDialog_ConnectTo + host; 
		text += "\n\n" + message; //$NON-NLS-1$ //$NON-NLS-2$
		label.setText(text);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		label.setLayoutData(data);

		createUsernameFields(main);
		createPasswordFields(main);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(main,
				"org.eclipse.update.ui.UserValidationDialog"); //$NON-NLS-1$
		return main;
	}
	/**
	 * Creates the three widgets that represent the user name entry area.
	 */
	protected void createPasswordFields(Composite parent) {
		new Label(parent, SWT.NONE).setText("Text for Password");  //UpdateUIMessages.UserVerificationDialog_Password); //$NON-NLS-1$

		passwordField = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
		passwordField.setLayoutData(data);

		new Label(parent, SWT.NONE); //spacer
	}
	/**
	 * Creates the three widgets that represent the user name entry area.
	 */
	protected void createUsernameFields(Composite parent) {
		new Label(parent, SWT.NONE).setText("Text for Username");  // UpdateUIMessages.UserVerificationDialog_UserName); //$NON-NLS-1$

		usernameField = new Text(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
		usernameField.setLayoutData(data);

		new Label(parent, SWT.NONE); //spacer
	}
	/**
	 * Returns the UserAuthentication entered by the user, or null if the user
	 * canceled.
	 */
/*
	public Authentication getAuthentication() {
		return userAuthentication;
	}
*/
	/**
	 * Notifies that the ok button of this dialog has been pressed.
	 */
	protected void okPressed() {
//		userAuthentication = new Authentication(usernameField.getText(),
//				passwordField.getText());
		super.okPressed();
	}

}
