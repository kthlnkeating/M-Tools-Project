package gov.va.med.iss.connection.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class SecondaryServerSelectionDialogForm extends Dialog {

		private Button btnOK;
		private Button btnCancel;
		private Button btnDontAsk;


		private SecondaryServerSelectionDialogData data = null;

		public SecondaryServerSelectionDialogForm(Shell parent, int style) {
			super(parent, style);
		}
		
		public SecondaryServerSelectionDialogForm(Shell parent) {
			this(parent, 0);
		}
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			// TODO Auto-generated method stub

		}
		
		static Table t;
		static String functionValue;
		
		public SecondaryServerSelectionDialogData open(String function, String[] dataArray) {
			data = new SecondaryServerSelectionDialogData();
			functionValue = function;
			final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
					SWT.APPLICATION_MODAL);
			@SuppressWarnings("unused")
			Display d = shell.getDisplay();
			if (function.compareTo("SAVE") == 0)
				shell.setText("Select Secondary Servers to SAVE TO:");
			else if (function.compareTo("DISCONNECT") == 0)
				shell.setText("Select Servers to Disconnect:");
			else  // anything else
				shell.setText("Select Secondary Servers:");
			shell.setSize(475,230); //(375,200);
			GridLayout g1 = new GridLayout();
			g1.numColumns = 2;
			shell.setLayout(g1);
			
		    int widths[] = {100,200,50,100};
		    String headings[] = {"Name","Address","Port","Project"};
		    final CheckListTable clTable = new CheckListTable(shell, SWT.NONE);

		    t = clTable.createCheckListTable(widths,headings,dataArray);
		    
		    Group bottomGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		    bottomGroup.setSize(400,30);
		    bottomGroup.setLocation(10,300);
		    
			btnOK = new Button(bottomGroup, SWT.PUSH);
			btnOK.setText("&OK");
//			btnOK.setLocation(25,300);
			btnOK.setLocation(10,15);
			btnOK.setSize(55,25);
			shell.setDefaultButton(btnOK);
			
			
			btnCancel = new Button(bottomGroup, SWT.PUSH);
			btnCancel.setText("&Cancel");
//			btnCancel.setLocation(50,300);
			btnCancel.setLocation(70,15);
			btnCancel.setSize(55,25);
			
			btnDontAsk = new Button(bottomGroup, SWT.CHECK);
			btnDontAsk.setText("&Don't ask again during this session");
//			btnDontAsk.setLocation(150,300);
			btnDontAsk.setLocation(150,15);
			btnDontAsk.setSize(200,25);
			btnDontAsk.setSelection(false);
			btnDontAsk.setToolTipText("Check this box to not be prompted again while the current session is active.");
			if (! (function.compareTo("SAVE") == 0))
				btnDontAsk.setVisible(false);
			
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					SecondaryServerSelectionDialogData.setButtonResponse(event.widget == btnOK);
					if (event.widget == btnOK) {
						SecondaryServerSelectionDialogData.setServerList(clTable.getAllElements());
						String[] array = clTable.getCheckedElements();
						SecondaryServerSelectionDialogData.setCheckedList(array);
						if (functionValue.compareTo("SAVE") == 0) {
							SecondaryServerSelectionDialogData.setSavedCheckedList(array);
							SecondaryServerSelectionDialogData.setDontAsk(btnDontAsk.getSelection());
						}
							
					}
					else { // Cancel
						SecondaryServerSelectionDialogData.setServerList(clTable.getAllElements());
						clTable.uncheckAll();
						String[] array = clTable.getCheckedElements();
						SecondaryServerSelectionDialogData.setCheckedList(array);
					}
					shell.setVisible(false);
					shell.close();
				}
			};
			
			btnOK.addListener(SWT.Selection, listener);
			btnCancel.addListener(SWT.Selection, listener);
			
			shell.open();
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) display.sleep();
			}
			return data;
		}

}
