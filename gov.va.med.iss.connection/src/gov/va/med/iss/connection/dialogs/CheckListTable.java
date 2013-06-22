/**
 * 
 */
package gov.va.med.iss.connection.dialogs;

import gov.va.med.iss.connection.utilities.MPiece;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


/**
 * @author VHAISFIVEYJ
 *
 */
public class CheckListTable {
	
	static private Composite theParent;
	static private int theStyle;
	static private Table theTable;
	static private int currentIndex;
	static private int arrayLength;
	static private int widthLength;
	static private Table table;
	static private Object array[];

	/**
	 * @param parent
	 * @param style
	 */
	public CheckListTable(Composite parent, int style) {
		super();
		// TODO Auto-generated constructor stub
		theParent = parent;
		theStyle = style;
	}
	
	public CheckListTable() {
		super();
	}
	
	private Table setTable(Composite parent, int style) {
		return new Table(parent, style | SWT.VIRTUAL | SWT.CHECK);
	}
	
	static Display d;
	static Shell s;
	
	public static void main(String[] args) {
		d = new Display();
		s = new Shell(d);
		s.setSize(375, 300);
	    
	    s.setText("Select Servers to copy to:");
	    GridLayout gl = new GridLayout();
	    gl.numColumns = 2;
	    s.setLayout(gl);
	    
	    int widths[] = {100,100,50,200};
	    String headings[] = {"Name","Address","Port","Project"};
	    String dataArray[] = {"0;abc;def;ghi","1;ghi;jkl;mno"};
	    CheckListTable clTable = new CheckListTable(s,SWT.NONE);

	    table = clTable.createCheckListTable(widths,headings,dataArray);

	    s.open();
	    while (!s.isDisposed()) {
	      if (!d.readAndDispatch())
	        d.sleep();
	    }
	    d.dispose();
	    
	    clTable.setServerList(array);
	}

	public Table createCheckListTable(int widths[], String headers[], String[] dataArray) {
		
	    final Table t = new Table(theParent, theStyle | SWT.BORDER | SWT.MULTI |SWT.CHECK 
	         | SWT.FULL_SELECTION);
	    final GridData gd = new GridData(GridData.FILL_BOTH);
	    gd.horizontalSpan = 2;
	    t.setLayoutData(gd);
	    t.setHeaderVisible(true);
	    for (int i=0; i<widths.length; i++) {
	    	TableColumn tc = new TableColumn(t,SWT.LEFT);
	    	tc.setText(headers[i]);
	    	tc.setWidth(widths[i]);
	    }
	    
	    // arrayLength = dataArray.length;
	    arrayLength = 0;
	    for (int i=0; i<dataArray.length; i++) {
	    	if (dataArray[i] != null)
	    		arrayLength++;
	    }

	    for (int i=0; i<arrayLength; i++) {
	    	widthLength = widths.length;
	    	TableItem item = new TableItem(t,SWT.NONE);
	    	String[] data1 = new String[widthLength];
	    	for (int j=1; j<=widthLength ; j++) {
	    		data1[j-1] = MPiece.getPiece(dataArray[i],";",j+1);
	    	}
	    	item.setText(data1);
	    	if (MPiece.getPiece(dataArray[i],";",1).compareTo("1") == 0) {
	    		item.setChecked(true);
	    	}
	    }
		table = t;
		return t;
	}
	
	public String[] getState() {
		String[] str;
		str = new String[arrayLength];
		for (int i=0; i<arrayLength; i++) {
			
		}
		return str;
	}
	
	/*
	 * based on code on 
	 * http://kickjava.com/src/org/eclipse/jface/viewers/CheckboxTableViewer.java.htm
	 */
	 public String[] getAllElements() {
         TableItem[] children = getTable().getItems();
         String[] v = new String[children.length];

         for (int i = 0; i < children.length; i++) {
             TableItem item = children[i];
             String str = "0";
             if (item.getChecked()) {
            	 str = "1";
             }
             for (int j=0; j<widthLength; j++) {
             	 if (! (item.getText(j) == null)) {
             		 str = str +";"+ item.getText(j);
             	 }
             	 else {
             		 str = str + ";";
             	 }
             }
             v[i] = str;
         }
         return v;
     }
	
	 public String[] getCheckedElements() {
         TableItem[] children = getTable().getItems();
         // get count of checked elements, so don't have null values
         int numChecked = 0;
         for (int i = 0; i < children.length; i++) {
        	 TableItem item = children[i];
        	 if (item.getChecked())
        		 numChecked++;
         }
         // set up and fill array with checked entries
         String[] v = new String[numChecked];
         int number = 0;
         for (int i = 0; i < children.length; i++) {
             TableItem item = children[i];
             String str = "";
             if (item.getChecked()) {
            	 for (int j=0; j<widthLength; j++) {
            		 if (! (item.getText(j) == null)) {
            			 if (! (str.compareTo("") == 0) )
            				 str = str + ";";
            			 str = str + item.getText(j);
            			 }
            		 }
            	 v[number++] = str;
             }
         }
         return v;
     }
	 
	 public void uncheckAll() {
         TableItem[] children = getTable().getItems();
         for (int i = 0; i < children.length; i++) {
             TableItem item = children[i];
             item.setChecked(false);
         }
     }

	 private Table getTable() {
		 return table;
	 }

	 public void setServerList(Object[] serverArray) {
		 String[] serverList = new String[serverArray.length];
		 for (int i=0; i< serverArray.length; i++) {
			 serverList[i] = (String)serverArray[i];
		}
	}
}
