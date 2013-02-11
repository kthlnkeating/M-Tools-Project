package gov.va.med.iss.mdebugger;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import java.util.ArrayList;
import gov.va.med.iss.mdebugger.util.DebuggerUtils;

public class MInitializationDialog extends Dialog {
	
	private ArrayList initList = null;
	private boolean clearInits = false;

	
	public MInitializationDialog(Shell parent, int style) {
		super (parent, style);
	}
		
	public MInitializationDialog(Shell parent) {
		this (parent, 0); // your default style bits go here (not the Shell's style bits)
	}
	
	public Object open () {
		final Object tempObj = null;
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setText("Variable Initialization"); // getText());
		shell.setSize(400,600);
		shell.setMinimumSize(400,400);
	    // ------------------------
	    FormLayout layout= new FormLayout();
	    shell.setLayout (layout);

	    final Button btnAdd = new Button(shell, SWT.PUSH);
	    final Button btnClear = new Button(shell,SWT.PUSH);
	    final Button btnUpper = new Button(shell,SWT.CHECK);
	    Label lblClear = new Label(shell,SWT.LEFT);
	    Label label1 = new Label(shell,SWT.LEFT);
	    Label label2 = new Label(shell,SWT.LEFT);
	    final Text text1 = new Text(shell,SWT.LEFT);
	    final List list1 = new List(shell, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
	    final Text text2 = new Text(shell,SWT.LEFT);
	    final Button btnOK = new Button(shell, SWT.PUSH);
	    final Button btnCancel = new Button(shell, SWT.PUSH);
	    btnAdd.setText("&Add Variable");
	    btnUpper.setText("All variables &uppercase");
	    btnUpper.setSelection(true);
	    btnClear.setText("C&lear");
	    lblClear.setText("Press to remove current values");
	    
	    label1.setText("Variable: ");
	    text1.setText("");
	    label2.setText("Value for Variable");
	    text2.setText("");
	    btnOK.setText("&OK");
	    btnCancel.setText("&Cancel");
	    
	    FormData data0a = new FormData();
	    data0a.top = new FormAttachment(0,5);
	    data0a.left = new FormAttachment(0,5);
	    btnClear.setLayoutData(data0a);
	    btnClear.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnClear){
	    		   list1.removeAll();
	    		   clearInits = true;
	    	   }   
	    	}
	    	});	    
	    
	    FormData data0b = new FormData();
	    data0b.top = new FormAttachment(0,10);
	    data0b.left = new FormAttachment(btnClear,10);
	    lblClear.setLayoutData(data0b);

	    FormData data1 = new FormData();
	    data1.top = new FormAttachment(btnClear,5);
	    data1 .left    = new FormAttachment(0,5);
	    data1 .right   = new FormAttachment(25,0);
	    label1.setLayoutData(data1);
	    
	    FormData data1b = new FormData();
	    data1b.top = new FormAttachment(btnClear,5);
	    data1b.left = new FormAttachment(label1,10);
	    data1b.right = new FormAttachment(100,-5);
	    label2.setLayoutData(data1b);
	    
	    FormData data1a = new FormData();
	    data1a.top = new FormAttachment(label1,5);
	    data1a.left = new FormAttachment(0,5);
	    text1.setLayoutData(data1a);
	    text1.setFocus();

	    FormData data2 = new FormData();
	    data2.top = new FormAttachment(label2,5);
	    data2.left     = new FormAttachment(text1,10);
	    data2.right    = new FormAttachment(100,-5);
	    text2.setLayoutData(data2);

	    FormData data3 = new FormData();
	    data3.top      = new FormAttachment(text1,5);
	    data3.left     = new FormAttachment(0,5);
	    btnAdd.setLayoutData(data3);
	    btnAdd.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnAdd){
	    		   if (btnUpper.getSelection()) {
	    			   text1.setText(text1.getText().toUpperCase());
	    		   }
	    		   String text2str = text2.getText();
	    		   text2.setText(DebuggerUtils.checkQuotes(text2.getText()));
	    		   list1.add(text1.getText()+"="+text2.getText());
	    		   text1.setText("");
	    		   text2.setText("");
	    	   }   
	    	}
	    	});
	    
	    FormData data3a = new FormData();
	    data3a.top = new FormAttachment(text1,10);
	    data3a.left = new FormAttachment(btnAdd,10);
	    btnUpper.setLayoutData(data3a);
	    
	    FormData data2a = new FormData();
	    data2a.top = new FormAttachment(btnAdd,10);
	    data2a.left = new FormAttachment(0,5);
	    data2a.right = new FormAttachment(100,-5);
	    data2a.bottom = new FormAttachment(100,-50);
	    list1.setLayoutData(data2a);
	    
	    FormData data4 = new FormData();
	    data4.top = new FormAttachment(list1,10);
	    data4.bottom = new FormAttachment(100,-5);
	    data4.left = new FormAttachment(30,0);
	    data4.right = new FormAttachment(45,0);
	    btnOK.setLayoutData(data4);
	    btnOK.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnOK){
	    		   initList = new ArrayList();
	    		   for (int i=0 ; i<list1.getItemCount(); i++) {
	    			   initList.add(i,list1.getItem(i));
	    		   }
	    		   shell.dispose();
	    	   }   
	    	}
	    	});	    

	    
	    FormData data5 = new FormData();
	    data5.top = new FormAttachment(list1,10);
	    data5.bottom = new FormAttachment(100,-5);
	    data5.left = new FormAttachment(55,0);
	    data5.right = new FormAttachment(70,0);
	    btnCancel.setLayoutData(data5);
	    btnCancel.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnCancel){
	    		   initList = null;
	    		   shell.dispose();
	    	   }   
	    	}
	    	});	    
	    
	    shell.pack();
	    shell.setTabList(new Control[] {text1,text2,btnAdd,text1} );
	    shell.setDefaultButton(btnOK);
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		return initList;
	}
}
