/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

// import org.eclipse.core.resources.IContainer;

/*
import gov.va.med.foundations.adapter.cci.VistaLinkConnection;
import gov.va.med.foundations.rpc.RpcRequest;
import gov.va.med.foundations.rpc.RpcRequestFactory;
import gov.va.med.foundations.rpc.RpcResponse;
import gov.va.med.iss.connection.actions.VistaConnection;
*/
import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/*
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
//import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
*/

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalNameDialog implements KeyListener {
	
	JDialog dialog;
	String selectedGlobalName = "-1";
	private String dialogType;
	private JButton globalNameCancelButton;
	private JButton globalNameButton;
	private boolean isCopy = false;
	private boolean isDataOnly = false;
	private String searchText = "";
	private boolean isSearchDataOnly = true;
	private boolean isSearchCaseSensitive = true;
	
	public String getGlobalName(String dialogType) {
/*
this.dialogType = dialogType;
		dialog = new JDialog((JFrame)null,"Global Name Input", true);
		Component component = createComponents();
		dialog.getContentPane().add(component);
		dialog.setSize(500,200);
		dialog.getRootPane().setDefaultButton(globalNameCancelButton);
		dialog.show();
		return selectedGlobalName;
*/
		GlobalNameDialogForm dialog = new GlobalNameDialogForm();
		String result = dialog.getGlobalName(dialogType);
		this.isCopy = dialog.isCopy;
		this.isDataOnly = dialog.isDataOnly;
		this.searchText = dialog.searchText;
		this.isSearchDataOnly = dialog.searchDataOnly;
		this.isSearchCaseSensitive = dialog.searchCaseSensitive;
		return result;
	}

	private static final int TEXT_FIELD_COLUMNS = 8;
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the global name(s) that are to be listed ";
	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	private static final String GLOBALNAME_LABEL_TEXT = "Global Name: ";
	private static final String GLOBALNAME_BUTTON_TEXT = "OK";
	private static final String GLOBALNAME_TOOLTIP = "Press after entering the desired global name";
	private static final String GLOBALSAVE_BUTTON_TEXT = "Cancel";
	private static final String GLOBALSAVE_TOOLTIP = "Press to cancel the operation";
	private static final String GLOBALCOPY_TOOLTIP = "Check to have the list presented in a form that can be copied and pasted into another account.";
	private JCheckBox checkboxCopy = null;
	JTextField globalNameNamespaceTextField;
	JCheckBox capsCheck;
	
	private JPanel mainPanel;
	public Component createComponents() {

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		//create Panel for Connection controls
		mainPanel.add(createConnectionPanel(), BorderLayout.NORTH);
		
		return mainPanel;
	}
	
	public JPanel createConnectionPanel() {

		JPanel myPanel = new JPanel();
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		myPanel.setLayout(myLayout);

		JLabel rpcListLabel = new JLabel();
		rpcListLabel.setText(GLOBALNAME_LABEL_TEXT);
		rpcListLabel.setFocusable(true);
		rpcListLabel.setLabelFor(globalNameNamespaceTextField);
		rpcListLabel.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				globalNameNamespaceTextField.requestFocusInWindow();
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		myPanel.add(rpcListLabel,c);

		globalNameNamespaceTextField = new JTextField();
		globalNameNamespaceTextField.setColumns(TEXT_FIELD_COLUMNS);
		globalNameNamespaceTextField.setBorder(BorderFactory.createEtchedBorder());
		if (dialogType.compareTo("GL") == 0)
			globalNameNamespaceTextField.setToolTipText(GLOBALNAME_TEXTFIELD_TOOLTIP2);
		else
			globalNameNamespaceTextField.setToolTipText(GLOBALNAME_TEXTFIELD_TOOLTIP1);
		globalNameNamespaceTextField.addKeyListener(this);
						
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		myPanel.add(globalNameNamespaceTextField,c);
		
		if (dialogType.compareTo("GL") == 0) {
			checkboxCopy = new JCheckBox();
			checkboxCopy.setText("Setup for copying");
			checkboxCopy.setSelected(false);
			checkboxCopy.setToolTipText(GLOBALCOPY_TOOLTIP);
			c.gridx = 0;
			c.gridy = 1;
			myPanel.add(checkboxCopy,c);
		}
		
		JTextField textServer = new JTextField();
		textServer.setText(ConnectionUtilities.getServer());
		textServer.setEditable(false);
		c.gridy = 3;
		c.gridx = 1;
		myPanel.add(textServer,c);
		
		
		JLabel label1 = new JLabel();
		label1.setText("Server: ");
		label1.setLabelFor(textServer);
		c.gridx = 0;
		c.gridy = 3;
		myPanel.add(label1,c);
		
			JTextField textPort = new JTextField();
			textPort.setText(ConnectionUtilities.getPort());
			textPort.setEditable(false);
			c.gridx = 1;
			c.gridy = 4;
			myPanel.add(textPort,c);
			
			JLabel label2 = new JLabel();
			label2.setText("Port:   ");
			label2.setLabelFor(textPort);
			c.gridx = 0;
			c.gridy = 4;
			myPanel.add(label2,c);
			
			capsCheck = new JCheckBox();
			capsCheck.setText("Set name to ALL caps.");
			capsCheck.setSelected(true);
			c.gridx = 0;
			c.gridy = 6;
			myPanel.add(capsCheck,c);
			
			globalNameButton = new JButton();
			globalNameButton.setText(GLOBALNAME_BUTTON_TEXT);
			globalNameButton.setToolTipText(GLOBALNAME_TOOLTIP);
//			globalNameButton.setMnemonic(GET_MNEMONIC);
			globalNameButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doGlobalNameOkSelect();
				}
			});
			c.gridx = 0;
			c.gridy = 10;
			c.gridheight = 2;
			myPanel.add(globalNameButton,c);

			globalNameCancelButton = new JButton();
			globalNameCancelButton.setText(GLOBALSAVE_BUTTON_TEXT);
			globalNameCancelButton.setToolTipText(GLOBALSAVE_TOOLTIP);
//			globalNameCancelButton.setMnemonic(GET_MNEMONIC);
			globalNameCancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doGlobalNameCancel();
				}
			});
			c.gridx = 1;
			c.gridy = 10;
			c.gridheight = 2;
			myPanel.add(globalNameCancelButton,c);
			
		return myPanel;
	}
	
	public void doGlobalNameOkSelect() {
		selectedGlobalName = globalNameNamespaceTextField.getText();
		if (dialogType.compareTo("GL") == 0)
			isCopy = checkboxCopy.isSelected();
		if (capsCheck.isSelected())
			selectedGlobalName = selectedGlobalName.toUpperCase();
		dialog.dispose();
	}
	
	public void doGlobalNameCancel() {
		dialog.dispose();
		selectedGlobalName = "-1";
	}

	/** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
   		dialog.getRootPane().setDefaultButton(globalNameButton);
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
    }
    
    public boolean isDataOnly() {
    	return isDataOnly;
    }
    
    public boolean isCopy() {
    	return isCopy;
    }
    
    public String getSearchText() {
    	return searchText;
    }
    
    public boolean isSearchDataOnly() {
    	return isSearchDataOnly;
    }
    
    public boolean isSearchCaseSensitive() {
    	return isSearchCaseSensitive;
    }

}
