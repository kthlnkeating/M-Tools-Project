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
*/
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.lang.String;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
/*
import javax.swing.event.ChangeListener;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
*/

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NewRoutineDialog implements KeyListener {
	
//	private String routineName;
	private boolean boolValue = true;
	private JButton cancelButton;
	private JButton okButton;
	private JDialog dialog;
	private String resultStr;
	
	public String askNewRoutine(String routineName) {
//		this.routineName = routineName;
		dialog = new JDialog((JFrame)null,"Routine "+routineName + " Not Found!!", true);
		Component component = createComponents();
		dialog.getContentPane().add(component);
		dialog.setSize(700,400);
		dialog.getRootPane().setDefaultButton(cancelButton);
		dialog.show();
		
		return resultStr;
	}

	
	private static final int TEXT_FIELD_COLUMNS = 60;
	private static final int UNIT_TEST_FIELD_COLUMNS = 8;
//	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the global name(s) that are to be listed ";
//	private static final String GLOBALNAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	private static final String NO_ROUTINE_TEXT = "The specified routine was not found---Fill in the fields below to create a new routine";
	private static final String GLOBALNAME_BUTTON_TEXT = "OK";
	private static final String GLOBALNAME_TOOLTIP = "Press after entering the requested information to create a new routine";
	private static final String GLOBALSAVE_BUTTON_TEXT = "Cancel";
	private static final String GLOBALSAVE_TOOLTIP = "Press to cancel the operation";
	JTextField firstLineTextField;
	JTextField secondLineTextField;
	JTextField unitTestTextField;
	JCheckBox updateRoutineFileEntryCheckBox;
	JTextField globalNameNamespaceTextField;
	
	private JPanel mainPanel;
	public Component createComponents() {

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		//create Panel for Connection controls
		mainPanel.add(createTopOfPanel(), BorderLayout.NORTH);
		mainPanel.add(createMiddleOfPanel(), BorderLayout.CENTER);
		mainPanel.add(createBottomOfPanel(), BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	public JPanel createTopOfPanel() {

		JPanel myPanel = new JPanel();
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		myPanel.setLayout(myLayout);

		JLabel warningLabel = new JLabel();
		warningLabel.setText(NO_ROUTINE_TEXT);
		warningLabel.setFocusable(false);

		c.gridx = 0;
		c.gridy = 0;
		myPanel.add(warningLabel,c);

		return myPanel;
	}
	
	public JPanel createMiddleOfPanel() {
		JPanel myPanel = new JPanel();
//		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel rpcListLabel = new JLabel();
		rpcListLabel.setText("initials and description for first line");
		rpcListLabel.setFocusable(true);
		rpcListLabel.setLabelFor(firstLineTextField);
		rpcListLabel.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				firstLineTextField.requestFocusInWindow();
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		myPanel.add(rpcListLabel,c);

		firstLineTextField = new JTextField();
		firstLineTextField.setColumns(TEXT_FIELD_COLUMNS);
		firstLineTextField.setBorder(BorderFactory.createEtchedBorder());
		firstLineTextField.addKeyListener(this);				
		c.gridx = 0;
		c.gridy = 1;
		myPanel.add(firstLineTextField,c);
		
		JLabel secondLineLabel = new JLabel();
		secondLineLabel.setText("Enter 2nd line data - Version#(with one decimal place);PackageName;PatchInfo(if applicable);DateOfRelease");
		secondLineLabel.setFocusable(true);
		secondLineLabel.setLabelFor(secondLineTextField);
		secondLineLabel.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				secondLineTextField.requestFocusInWindow();
			}
		});
		c.gridx = 0;
		c.gridy = 3;
		myPanel.add(secondLineLabel,c);

		secondLineTextField = new JTextField();
		secondLineTextField.setColumns(TEXT_FIELD_COLUMNS);
		secondLineTextField.setBorder(BorderFactory.createEtchedBorder());
		secondLineTextField.addKeyListener(this);				
		c.gridx = 0;
		c.gridy = 4;
		c.gridheight = 2;
		myPanel.add(secondLineTextField,c);
		
		JLabel unitTestRoutineLabel = new JLabel();
		unitTestRoutineLabel.setText("Enter a Unit Test routine (optional)");
		unitTestRoutineLabel.setFocusable(true);
		unitTestRoutineLabel.setLabelFor(unitTestTextField);
		unitTestRoutineLabel.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				unitTestTextField.requestFocusInWindow();
			}
		});
		c.gridx = 0;
		c.gridy = 6;
		myPanel.add(unitTestRoutineLabel,c);

		unitTestTextField = new JTextField();
		unitTestTextField.setColumns(UNIT_TEST_FIELD_COLUMNS);
		unitTestTextField.setBorder(BorderFactory.createEtchedBorder());
		unitTestTextField.addKeyListener(this);
						
		c.gridx = 1;
		c.gridy = 6;
		myPanel.add(unitTestTextField,c);
		
		JCheckBox updateCheckBox = new JCheckBox("Update Entry in Routine File on Save", true);
		updateCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolValue = boolValue ? false : true;
				okDefaultButton();
			}
		});
		c.gridx = 0;
		c.gridy = 9;
		c.gridheight = 2;
		myPanel.add(updateCheckBox,c);

		return myPanel;
	}
		
	private JPanel createBottomOfPanel() {
		JPanel myPanel = new JPanel();
//		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		okButton = new JButton();
			okButton.setText(GLOBALNAME_BUTTON_TEXT);
			okButton.setToolTipText(GLOBALNAME_TOOLTIP);
//			okButton.setMnemonic(GET_MNEMONIC);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doOkSelect();
				}
			});
			c.gridx = 1;
			c.gridy = 4;
			c.gridheight = 2;
			myPanel.add(okButton,c);

			cancelButton = new JButton();
			cancelButton.setText(GLOBALSAVE_BUTTON_TEXT);
			cancelButton.setToolTipText(GLOBALSAVE_TOOLTIP);
//			cancelButton.setMnemonic(GET_MNEMONIC);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doCancelSelect();
				}
			});
			c.gridx = 3;
			c.gridy = 4;
			c.gridheight = 2;
			myPanel.add(cancelButton,c);
			
		return myPanel;
	}
	
	public void doOkSelect() {
		// for some reason can't read the state of the checkbox to set boolValue, put in work around instead.
		resultStr = firstLineTextField.getText()+"~^~"+secondLineTextField.getText()+"~^~"+unitTestTextField.getText()+"~^~";
		if (boolValue)
			resultStr = resultStr + "1";
		else
			resultStr = resultStr + "0";
		dialog.dispose();
	}
	
	public void doCancelSelect() {
		dialog.dispose();
		resultStr = "";
	}
	
	private void okDefaultButton() {
		dialog.getRootPane().setDefaultButton(okButton);
	}
	
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        okDefaultButton();
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
    }



}
