package gov.va.med.iss.mdebugger;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MDebuggerDialog extends Dialog {

	private boolean result = false;
	public String dbCommand = "";
	public ArrayList initList = null;
	private boolean clearInits = false;
	
	private static ArrayList dropList = null;
		

	public MDebuggerDialog (Shell parent, int style) {
		super (parent, style);
		initList = new ArrayList();
		if (dropList == null) {
			dropList = new ArrayList();
		}
	}
		
	public MDebuggerDialog (Shell parent) {
		this (parent, 0); // your default style bits go here (not the Shell's style bits)
	}

	public String open () {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("Start M Debugger"); // getText());
	    // ------------------------
	    FormLayout layout= new FormLayout();
	    shell.setLayout (layout);

	    final Button button1 = new Button(shell, SWT.CHECK);
	    Label label1 = new Label(shell,SWT.LEFT);
	    Label label2 = new Label(shell,SWT.LEFT);
//	    final Text text2 = new Text(shell, SWT.LEFT);
	    final Combo text2 = new Combo(shell, SWT.DROP_DOWN);
	    final Button btnOK = new Button(shell, SWT.PUSH);
	    final Button btnCancel = new Button(shell, SWT.PUSH);
	    button1.setText("Initialize variables before starting debugger"); 
	    
	    label1.setText("Enter Code to Debug: ");
	    label2.setText("e.g., 'S X=$$NOW^XLFDT()' or 'D TAG^ROUTINE(4,3)'");
	    text2.setText("");
	    if (dropList.size() > 0) {
	    	for (int i=0 ; i < dropList.size(); i++) {
	    		text2.add((String)dropList.get(i));
	    	}
	    }
	    btnOK.setText("&OK");
	    btnCancel.setText("&Cancel");

	    FormData data1 = new FormData();
	    data1.top = new FormAttachment(0,5);
	    data1 .left    = new FormAttachment(0,5);
	    data1 .right   = new FormAttachment(25,0);
	    label1.setLayoutData(data1);
	    
	    FormData data1a = new FormData();
	    data1a.top = new FormAttachment(label1,5);
	    data1a.left = new FormAttachment(10,10);
	    label2.setLayoutData(data1a);

	    FormData data2 = new FormData();
	    data2.top = new FormAttachment(0,5);
	    data2.left     = new FormAttachment(label1,5);
	    data2.right    = new FormAttachment(90,-5);
	    text2.setLayoutData(data2);
	    text2.setFocus();

	    FormData data3 = new FormData();
	    data3.top      = new FormAttachment(label2,5);
	    data3.left     = new FormAttachment(0,5);
	    button1.setLayoutData(data3);
	    button1.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    		if (e.getSource() == button1){
	    			if (button1.getSelection() == true) {
	    				MInitializationDialog mInitDialog = new MInitializationDialog(getParent());
	    				initList = (ArrayList)mInitDialog.open();
	    			}
	    		}   
	    	}
	    });
	    
	    FormData data4 = new FormData();
	    data4.top = new FormAttachment(button1,10);
	    data4.bottom = new FormAttachment(100,-5);
	    data4.left = new FormAttachment(30,0);
	    data4.right = new FormAttachment(45,0);
	    btnOK.setLayoutData(data4);
	    btnOK.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnOK){
	    		   result = true;
	    		   dbCommand = text2.getText();
	    		   int index = dropList.indexOf(dbCommand);
	    		   if (index > -1) {
	    			   dropList.remove(index);
	    		   }
	    		   dropList.add(0,dbCommand);
	    		   shell.dispose();
	    	   }   
	    	}
	    	});
	    
	    FormData data5 = new FormData();
	    data5.top = new FormAttachment(button1,10);
	    data5.bottom = new FormAttachment(100,-5);
	    data5.left = new FormAttachment(55,0);
	    data5.right = new FormAttachment(70,0);
	    btnCancel.setLayoutData(data5);
	    btnCancel.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnCancel){
	    		   result = false;
	    		   dbCommand = "";
	    		   shell.dispose();
	    	   }   
	    	}
	    	});	    
	    shell.setDefaultButton(btnOK);
	    shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		if (!(initList == null)) {
			for (int i=0; i<initList.size(); i++) {
				System.out.println((String)initList.get(i));
			}
		}
		return dbCommand;
	}
	
	public boolean getClearInits() {
		return clearInits;
	}
	
	public ArrayList getInitList() {
		return initList;
	}
}
