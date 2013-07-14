/*
 * Created on Aug 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author vhaisfiveyj
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoutineNameDialog implements KeyListener {
	
	JDialog dialog;
	String selectedRoutineName = "";
	private boolean isShowReadOnly = false;
	private boolean readOnlyValue = false;
	
	public String getRoutineName(boolean showReadOnly) {
		isShowReadOnly = showReadOnly;
		String strVal = getRoutineName();
		if (showReadOnly)
			if (readOnlyValue)
				strVal = strVal + "~^~1";
		return strVal;
	}
	
	public String getRoutineName() {
		dialog = new JDialog((JFrame)null,"Routine Name Input", true);
		Component component = createComponents();
//		Component component = createConnectionPanel();  // createComponents();
		dialog.getContentPane().add(component);
		dialog.setSize(500,200);
		dialog.getRootPane().setDefaultButton(routineNameCancelButton);
		dialog.setVisible(true);
		return selectedRoutineName;
	}

	private static final int TEXT_FIELD_COLUMNS = 8;
	private static final String ROUTINENAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the routine name(s) that are to be listed ";
//	private static final String ROUTINENAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	private static final String ROUTINENAME_LABEL_TEXT = "Routine Name: ";
	private static final String ROUTINENAME_BUTTON_TEXT = "OK";
	private static final String ROUTINENAME_TOOLTIP = "Press after entering the desired global name";
	private static final String ROUTINESAVE_BUTTON_TEXT = "Cancel";
	private static final String ROUTINESAVE_TOOLTIP = "Press to cancel the operation";
	JTextField routineNameTextField;
	JButton routineNameButton;
	JButton routineNameCancelButton;
	JCheckBox readOnlyCheck;
	
	private JPanel mainPanel;
	public Component createComponents() {

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		//create Panel for Connection controls
//		mainPanel.add(createConnectionPanel());
		mainPanel.add(createConnectionPanel(), BorderLayout.NORTH);
		
		return mainPanel;
	}
	
	JCheckBox capsCheck;
	
	public JPanel createConnectionPanel() {

		JPanel myPanel = new JPanel();
		GridBagLayout myLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		myPanel.setLayout(myLayout);

		JLabel rpcListLabel = new JLabel();
		rpcListLabel.setText(ROUTINENAME_LABEL_TEXT);
		rpcListLabel.setFocusable(true);
		rpcListLabel.setLabelFor(routineNameTextField);
		rpcListLabel.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
			}
			public void focusGained(FocusEvent e) {
				routineNameTextField.requestFocusInWindow();
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		myPanel.add(rpcListLabel,c);

		routineNameTextField = new JTextField();
		routineNameTextField.setColumns(TEXT_FIELD_COLUMNS);
		routineNameTextField.setBorder(BorderFactory.createEtchedBorder());
		routineNameTextField.setToolTipText(ROUTINENAME_TEXTFIELD_TOOLTIP1);
		routineNameTextField.addKeyListener(this);
		
						
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		myPanel.add(routineNameTextField,c);
		
			routineNameButton = new JButton();
			routineNameButton.setText(ROUTINENAME_BUTTON_TEXT);
			routineNameButton.setToolTipText(ROUTINENAME_TOOLTIP);
//			routineNameButton.setMnemonic(GET_MNEMONIC);
			routineNameButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doRoutineNameOkSelect();
				}
			});
			c.gridx = 0;
			c.gridy = 9;
			c.gridheight = 2;
			myPanel.add(routineNameButton,c);

			routineNameCancelButton = new JButton();
			routineNameCancelButton.setText(ROUTINESAVE_BUTTON_TEXT);
			routineNameCancelButton.setToolTipText(ROUTINESAVE_TOOLTIP);
//			routineNameCancelButton.setMnemonic(GET_MNEMONIC);
			routineNameCancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doRoutineNameCancel();
				}
			});
			c.gridx = 1;
			c.gridy = 9;
			c.gridheight = 2;
			myPanel.add(routineNameCancelButton,c);
			
			JTextField textServer = new JTextField();
			textServer.setText(ConnectionUtilities.getServer());
			textServer.setEditable(false);
			c.gridy = 1;
			c.gridx = 1;
			myPanel.add(textServer,c);
			
			JLabel label1 = new JLabel();
			label1.setText("Server: ");
			label1.setLabelFor(textServer);
			c.gridx = 0;
			c.gridy = 1;
			myPanel.add(label1,c);
			
			JTextField textPort = new JTextField();
			textPort.setText(ConnectionUtilities.getPort());
			textPort.setEditable(false);
			c.gridx = 1;
			c.gridy = 3;
			myPanel.add(textPort,c);
			
			JLabel label2 = new JLabel();
			label2.setText("Port:   ");
			label2.setLabelFor(textPort);
			c.gridx = 0;
			c.gridy = 3;
			myPanel.add(label2,c);
			
            if (!(ConnectionUtilities.getProject().compareTo("") == 0)) {
                JTextField textProject = new JTextField();
                textProject.setText(ConnectionUtilities.getProject());
                textProject.setEditable(false);
                c.gridx = 5;
                c.gridy = 3;
                myPanel.add(textProject,c);
                
                JLabel label3 = new JLabel();
                label3.setText("Project: ");
                label3.setLabelFor(textProject);
                c.gridx = 4;
                c.gridy = 3;
                myPanel.add(label3,c);
            }
            
			capsCheck = new JCheckBox();
			capsCheck.setText("Set name to ALL caps.");
			capsCheck.setSelected(true);
			c.gridx = 0;
			c.gridy = 5;
			myPanel.add(capsCheck,c);
			
			if (isShowReadOnly) {
				readOnlyCheck = new JCheckBox();
				readOnlyCheck.setText("Load as Read-Only");
				readOnlyCheck.setSelected(false);
				c.gridx = 0;
				c.gridy = 7;
				myPanel.add(readOnlyCheck,c);
			}
			
		return myPanel;
	}
	
	public void doRoutineNameOkSelect() {
		selectedRoutineName = routineNameTextField.getText();
		if (capsCheck.isSelected())
			selectedRoutineName = selectedRoutineName.toUpperCase();
		if (isShowReadOnly)
			if (readOnlyCheck.isSelected())
				readOnlyValue = true;
		dialog.dispose();
	}
	
	public void doRoutineNameCancel() {
		dialog.dispose();
		selectedRoutineName = "";
	}

	/** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
   		dialog.getRootPane().setDefaultButton(routineNameButton);
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
    }

}
