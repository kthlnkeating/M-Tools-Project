package gov.va.med.iss.mdebugger;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import gov.va.med.iss.mdebugger.MDebuggerDialog;

public class SWTTemplate {
	  public static void main(String args[]) 
	  {
	    Display display = new Display();
	    Shell shell = new Shell(display);
	    // ------------------------
	    FormLayout layout= new FormLayout();
	    shell.setLayout (layout);

	    Button button1 = new Button(shell, SWT.CHECK);
	    Label label1 = new Label(shell,SWT.LEFT);
	    Label label2 = new Label(shell,SWT.LEFT);
	    Text text2 = new Text(shell, SWT.LEFT);
	    Button button3 = new Button(shell, SWT.PUSH);
	    button1.setText("Need to initialize variables?"); 
	    
	    label1.setText("Enter Code to Debug: ");
	    label2.setText("e.g., 'S X=$$NOW^XLFDT()' or 'D TAG^ROUTINE(3,4)'");
	    text2.setText("");
	    button3.setText("B3");

	    FormData data1 = new FormData();
	    data1.top = new FormAttachment(0,5);
	    data1 .left    = new FormAttachment(0,5);
	    data1 .right   = new FormAttachment(25,0);
	    label1.setLayoutData(data1);
	    
	    FormData data1a = new FormData();
	    data1a.top = new FormAttachment(label1,5);
	    data1a.right = new FormAttachment(90,0);
	    label2.setLayoutData(data1a);

	    FormData data2 = new FormData();
	    data2.top = new FormAttachment(0,5);
	    data2.left     = new FormAttachment(label1,5);
	    data2.right    = new FormAttachment(90,-5);
	    text2.setLayoutData(data2);

	    FormData data3 = new FormData();
	    data3.top      = new FormAttachment(label2,5);
//	    data3.bottom   = new FormAttachment(100,-5);
	    data3.right    = new FormAttachment(25,-5);
	    data3.left     = new FormAttachment(0,5);
	    button1.setLayoutData(data3);
	    
	    FormData data4 = new FormData();
	    data4.top = new FormAttachment(button1,10);
//	    data4.bottom = new FormAttachment(100,-5);
	    data4.left = new FormAttachment(0,20);
	    data4.right = new FormAttachment(45,-5);
	    button3.setLayoutData(data4);
	    // Your code comes to here.
	    // ------------------------
	    shell.pack();
	    shell.open();
	    MDebuggerDialog mdialog = new MDebuggerDialog(shell);
	    if (!(mdialog.open() == "") ){
	    	System.out.println("Value was True");
	    }
	    while( !shell.isDisposed())
	    {
	      if(!display.readAndDispatch()) 
	      display.sleep();
	    }
	    display.dispose();
	  }
}