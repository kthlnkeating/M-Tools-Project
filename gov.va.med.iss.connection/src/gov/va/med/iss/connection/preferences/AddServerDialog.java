/**
 * 
 */
package gov.va.med.iss.connection.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author vhaisfiveyj
 * 
 * class to Add a server to the list of server preferences
 *
 */
public class AddServerDialog {
	
	private String serverName = "";
	private String serverAddress = "";
	private String serverPort = "";
	@SuppressWarnings("unused")
    private boolean isCMS = false;
    private String cmsProjectName = "";
	private boolean result = false;

	/**
	 * 
	 */
	public AddServerDialog() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public boolean getServerData(Shell shell) {
		open(shell);
		if (serverName.compareTo("") == 0)
			result = false;
		else if (serverAddress.compareTo("") == 0)
			result = false;
		else if (serverPort.compareTo("") == 0)
			result = false;
		return result;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	public String getServerPort() {
		return serverPort;
	}
	
    public boolean getIsCMS() {
    	if (cmsProjectName.compareTo("") == 0) {
    		return false;
    	}
        return true;
    }
    
    public String getCMSProjectName() {
        return cmsProjectName;
    }
	
	private Button cancelButton;
	private Button okButton;
	

	
	@SuppressWarnings("unused")
	private static final int TEXT_FIELD_COLUMNS = 60;
	@SuppressWarnings("unused")
	private static final int UNIT_TEST_FIELD_COLUMNS = 8;
	@SuppressWarnings("unused")
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the global name(s) that are to be listed ";
	@SuppressWarnings("unused")
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	@SuppressWarnings("unused")
	private static final String NO_ROUTINE_TEXT = "The specified routine was not found---Fill in the fields below to create a new routine";
	private static final String GLOBALNAME_BUTTON_TEXT = "OK";
	private static final String GLOBALNAME_TOOLTIP = "Press after entering the requested information to add a new server";
	private static final String GLOBALSAVE_BUTTON_TEXT = "Cancel";
	private static final String GLOBALSAVE_TOOLTIP = "Press to cancel the operation";
	@SuppressWarnings("unused")
    private static final String CMSBUTTON_TOOLTIP = "Check this box if the routines for this server are to be maintained in a version control system (e.g., ClearCase)";
	Text serverNameTextField;
	Text serverAddressTextField;
	Text serverPortTextField;
	Label warningLabel;
	Button updateRoutineFileEntryCheckBox;
	Text globalNameNamespaceTextField;
//    Button cmsButton;
    Text cmsProjectTextField;
	private void open(Shell parentShell) {
		final Shell shell = new Shell(parentShell,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText("Add a new server to server list");
		shell.setLocation(0,0);
		shell.setSize(400,380);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);

		
		Label serverNameLabel = new Label(shell, SWT.LEFT);
		serverNameLabel.setText("Server &Name:");
		serverNameLabel.setLocation(10,40);
		serverNameLabel.setSize(100,20);
		serverNameLabel.setFont(font);
		serverNameLabel.setToolTipText("Enter a name to be used to refer to this server.");

		serverNameTextField = new Text(shell,SWT.BORDER);
		serverNameTextField.setLocation(140,40);
		serverNameTextField.setSize(100,20);
		serverNameTextField.setFont(font);
		serverNameTextField.setToolTipText("Enter a name to be used to refer to this server.");
		
		Label serverAddressLabel = new Label(shell, SWT.LEFT);
		serverAddressLabel.setText("Server &Address: ");
		serverAddressLabel.setLocation(10,100);
		serverAddressLabel.setSize(120,20);
		serverAddressLabel.setFont(font);
		serverAddressLabel.setToolTipText("Enter address name or ip address");

		serverAddressTextField = new Text(shell, SWT.BORDER);
		serverAddressTextField.setLocation(140,100);
		serverAddressTextField.setSize(215,20);
		serverAddressTextField.setFont(font);
		serverAddressTextField.setToolTipText("Enter address name or ip address");
		
		Label serverPortLabel = new Label(shell, SWT.LEFT);
		serverPortLabel.setText("Server &Port: ");
		serverPortLabel.setLocation(10,160);
		serverPortLabel.setSize(100,20);
		serverPortLabel.setFont(font);
		serverPortLabel.setToolTipText("Enter port number for this server");

		serverPortTextField = new Text(shell, SWT.BORDER);
		serverPortTextField.setLocation(140,160);
		serverPortTextField.setSize(100,20);
		serverPortTextField.setFont(font);
		serverPortTextField.setToolTipText("Enter port number for this server");
/*		
        cmsButton = new Button(shell,SWT.CHECK);
        cmsButton.setText("&use Version Control");
        cmsButton.setToolTipText(CMSBUTTON_TOOLTIP);
        cmsButton.setFont(font);
        cmsButton.setLocation(10,220);
        cmsButton.setSize(200,20);
*/        
        Label labelCMSProject = new Label(shell, SWT.LEFT);
        labelCMSProject.setText("&Version Control Project Name:");
        labelCMSProject.setLocation(10,240);
        labelCMSProject.setSize(240,20);
        labelCMSProject.setFont(font);
        
        cmsProjectTextField = new Text(shell,SWT.BORDER); 
        cmsProjectTextField.setLocation(140,260);
        cmsProjectTextField.setSize(330,20);
        cmsProjectTextField.setFont(new Font(shell.getDisplay(),"Courier New",10,SWT.NONE));
        cmsProjectTextField.setToolTipText("Enter Project Name to be used for Version Control");

		okButton = new Button(shell,SWT.PUSH);
		okButton.setText(GLOBALNAME_BUTTON_TEXT);
		okButton.setToolTipText(GLOBALNAME_TOOLTIP);
		okButton.setLocation(50,300);
		okButton.setSize(75,25);

		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(GLOBALSAVE_BUTTON_TEXT);
		cancelButton.setToolTipText(GLOBALSAVE_TOOLTIP);
		cancelButton.setLocation(200,300);
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
		Display display = shell.getDisplay(); //MEditorUtilities.getIWorkbenchWindow().getShell().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	public void doOkSelect() {
		serverName = serverNameTextField.getText();
		serverAddress = serverAddressTextField.getText();
		serverPort = serverPortTextField.getText();
		cmsProjectName = cmsProjectTextField.getText();
		result = true;
	}
	
	public void doCancelSelect() {
		result = false;
	}

}
