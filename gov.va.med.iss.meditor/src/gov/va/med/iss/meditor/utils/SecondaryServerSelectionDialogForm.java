package gov.va.med.iss.meditor.utils;

import gov.va.med.iss.connection.dialogs.CheckListTable;
//import gov.va.med.iss.connection.utilities.ConnectionUtilities;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;

public class SecondaryServerSelectionDialogForm extends Dialog {

//		private Label lblQuestion;
//		private Text txtResponse;
		private Button btnOK;
		private Button btnCancel;
/*
		private Button btnUpperCase;
		private Button btnReadOnly;
		private Label lblServer;
		private Label lblPort;
		private Text txtServer;
		private Text txtPort;
		private Label lblDirectory;
		private Text txtDirectory;
		private boolean isShowReadOnly = false;
		private boolean isShowDirectory = false;
*/
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
		
		public SecondaryServerSelectionDialogData open(String[] dataArray) {
			data = new SecondaryServerSelectionDialogData();
			final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
					SWT.APPLICATION_MODAL);
//			Display d = shell.getDisplay();
			shell.setText("Select Secondary Servers to SAVE TO:");
			shell.setSize(600,500); //(375,200);
			GridLayout g1 = new GridLayout();
			g1.numColumns = 2;
			shell.setLayout(g1);
			
		    int widths[] = {100,200,50};
		    String headings[] = {"Name","Address","Port"};
		    final CheckListTable clTable = new CheckListTable(shell, SWT.NONE);

		    t = clTable.createCheckListTable(widths,headings,dataArray);
			btnOK = new Button(shell, SWT.PUSH);
			btnOK.setText("OK");
			btnOK.setLocation(80,300);
			btnOK.setSize(55,25);
			shell.setDefaultButton(btnOK);
			
			btnCancel = new Button(shell, SWT.PUSH);
			btnCancel.setText("Cancel");
			btnCancel.setLocation(165,300);
			btnCancel.setSize(55,25);
			
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					SecondaryServerSelectionDialogData.setButtonResponse(event.widget == btnOK);
					if (event.widget == btnOK) {
						SecondaryServerSelectionDialogData.setServerList(clTable.getAllElements());
						String[] array = clTable.getCheckedElements();
						SecondaryServerSelectionDialogData.setCheckedList(array);
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
