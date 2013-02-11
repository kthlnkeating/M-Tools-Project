/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.iss.meditor.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;

//import javax.swing.BorderFactory;
import javax.swing.JButton;
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
public class WarningDialog {
	JDialog dialog;
	boolean returnValue;
	private String routineName ="";
	
	public boolean warningDialog(String title) {
		routineName = title;
		dialog = new JDialog((JFrame)null,"** WARNING **", true);
		Component component = createComponents();
		dialog.getContentPane().add(component);
		dialog.setSize(600,200);
		dialog.getRootPane().setDefaultButton(routineNameCancelButton);
		dialog.show();
		return returnValue;
	}
/*
	private static final int TEXT_FIELD_COLUMNS = 8;
	private static final String ROUTINENAME_TEXTFIELD_TOOLTIP1 = "Enter the first part of the routine name(s) that are to be listed ";
	private static final String ROUTINENAME_TEXTFIELD_TOOLTIP2 = "Enter the beginning of the nodes to be displayed [must have name and '(' at least]";
	private static final String ROUTINENAME_LABEL_TEXT = "Routine Name: ";
*/
	private static final String ROUTINENAME_BUTTON_TEXT = "OK";
	private static final String ROUTINENAME_TOOLTIP = "Press after entering the desired global name";
	private static final String CANCEL_BUTTON_TEXT = "Cancel";
	private static final String CANCEL_TOOLTIP = "Press to cancel the operation";
	JTextField routineNameNamespaceTextField;
	JButton routineNameButton;
	JButton routineNameCancelButton;
	
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
			
			JLabel label1 = new JLabel();
			label1.setText(routineName);
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 4;
			myPanel.add(label1,c);
			
			JLabel label2 = new JLabel();
			label2.setText("Click Cancel to quit now, or OK to continue loading the version on the server");
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 4;
			myPanel.add(label2,c);
/*	
			JLabel rpcListLabel = new JLabel();
			rpcListLabel.setText(ROUTINENAME_LABEL_TEXT);
			rpcListLabel.setFocusable(true);
			rpcListLabel.setLabelFor(routineNameNamespaceTextField);
			rpcListLabel.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
				}
				public void focusGained(FocusEvent e) {
					routineNameNamespaceTextField.requestFocusInWindow();
				}
			});
			c.gridx = 0;
			c.gridy = 0;
			myPanel.add(rpcListLabel,c);
	
			routineNameNamespaceTextField = new JTextField();
			routineNameNamespaceTextField.setColumns(TEXT_FIELD_COLUMNS);
			routineNameNamespaceTextField.setBorder(BorderFactory.createEtchedBorder());
			routineNameNamespaceTextField.setToolTipText(ROUTINENAME_TEXTFIELD_TOOLTIP1);
							
			c.gridx = 4;
			c.gridy = 1;
			c.gridheight = 2;
			myPanel.add(routineNameNamespaceTextField,c);
*/			
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
				c.gridy = 4;
				myPanel.add(routineNameButton,c);
	
				routineNameCancelButton = new JButton();
				routineNameCancelButton.setText(CANCEL_BUTTON_TEXT);
				routineNameCancelButton.setToolTipText(CANCEL_TOOLTIP);
	//			routineNameCancelButton.setMnemonic(GET_MNEMONIC);
				routineNameCancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						doRoutineNameCancel();
					}
				});
				c.gridx = 0;
				c.gridy = 5;
				myPanel.add(routineNameCancelButton,c);
				
			return myPanel;
		}
	
	public void doRoutineNameOkSelect() {
		returnValue = true;
		dialog.dispose();
	}
	
	public void doRoutineNameCancel() {
		dialog.dispose();
		returnValue = false;
	}


}
