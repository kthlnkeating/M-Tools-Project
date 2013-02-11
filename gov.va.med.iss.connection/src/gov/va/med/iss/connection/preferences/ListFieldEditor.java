package gov.va.med.iss.connection.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import gov.va.med.iss.connection.preferences.ConnectionPreferencePage;

/**
 * 
 * @author vhaisfiveyj
 *
 *  class to generate a List control on a preference page
 *  
 */
public class ListFieldEditor extends FieldEditor {
	
	private String nameBase = "";
	private List stringList;
	private int currentCount = 0;
	private Composite panel1;
	private int initialCount = 0;

/*
	public ListFieldEditor() {
		super();
		// TODO Auto-generated constructor stub
	}

*/	
	/**
	 * name argument is taken as the beginning of the 
	 *   preference name, an integer value will be appended to
	 *   this value until a null string is encountered.
	 */
	public ListFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		nameBase = name;
	}

	protected void adjustForNumColumns(int numColumns) {
		// TODO Auto-generated method stub

	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// TODO Auto-generated method stub
		getLabelControl(parent);
		Composite panel = new Composite(parent,SWT.BORDER);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = false;
        panel.setLayoutData(gd);
		stringList = new List(panel,SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		stringList.setSize(300,200);
		stringList.setVisible(true);
		stringList.setEnabled(true);
		stringList.add("stringList1");
		panel1 = new Composite(parent,SWT.BORDER);
        gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        panel.setLayoutData(gd);
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		panel1.setLayout(fillLayout);
		Button buttonAdd = new Button(panel1,SWT.BORDER | SWT.PUSH);
		buttonAdd.setText("&Add");
	    buttonAdd.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          switch (e.type) {
	          case SWT.Selection:
	        	addItem(panel1.getShell());
	            break;
	          }
	        }
	      });
	    Button buttonRemove = new Button(panel1 /* getFieldEditorParent() */,SWT.BORDER | SWT.PUSH);
		buttonRemove.setText("&Remove");
	    buttonRemove.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          switch (e.type) {
	          case SWT.Selection:
	        	if (stringList.getSelectionIndex() > -1) {
	        		stringList.remove(stringList.getSelectionIndex());
	        		currentCount--;
	        	}
	            break;
	          }
	        }
	      });
		Button buttonSpace = new Button(panel1,SWT.PUSH);
		buttonSpace.setEnabled(false);
		Button buttonUp = new Button(panel1,SWT.PUSH);
		buttonUp.setText("Move &Up");
	    buttonUp.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          switch (e.type) {
	          case SWT.Selection:
	        	if (stringList.getSelectionIndex() > 0) {
	        		int index = stringList.getSelectionIndex();
	        		String value = stringList.getItem(index);
	        		stringList.remove(index);
	        		stringList.add(value,index-1);
	        		stringList.select(index-1);
	        	}
	            break;
	          }
	        }
	      });
		Button buttonDown = new Button(panel1,SWT.PUSH);
		buttonDown.setText("Move &Down");
	    buttonDown.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          switch (e.type) {
	          case SWT.Selection:
	        	if (stringList.getSelectionIndex() > -1) {
	        		if (stringList.getSelectionIndex() < (stringList.getItemCount()-1)) {
	        			int index = stringList.getSelectionIndex();
	        			String value = stringList.getItem(index);
	        			stringList.remove(index);
	        			stringList.add(value,index+1);
	        			stringList.select(index+1);
	        		}
	        	}
	            break;
	          }
	        }
	      });
	}
	
	protected void addItem(Shell shell) {
		AddServerDialog serverDialog = new AddServerDialog();
		boolean isGood = false;
		String newServer = "";
		if (serverDialog.getServerData(shell)) {
			String str = serverDialog.getServerName();
			if (str.compareTo("") != 0) {
				newServer = str + ';';
				str = serverDialog.getServerAddress();
				if (str.compareTo("") != 0) {
					newServer = newServer + str + ';';
					str = serverDialog.getServerPort();
					if (str.compareTo("") != 0) {
						newServer = newServer + str;
                        // JLI 090908 added CMSProjectName to server info
                        str = serverDialog.getCMSProjectName();
                        if (str.compareTo("") != 0) {
                            newServer = newServer + ';' + str;
                        }
                        else {
                        	newServer = newServer + ';';
                        }
						isGood = true;
					}
				}
			}
			if (isGood) {
				stringList.add(newServer);
				currentCount++;
			}
			else {
				MessageDialog.openConfirm(
						shell, //MEditorUtilities.getIWorkbenchWindow().getShell(),
						"Add Server Problem",
						"***  One or more fields were null, so nothing was added  ***");

			}
		}
	}

	protected void doLoad() {
		stringList.removeAll();
		IPreferenceStore store = getPreferenceStore();
		currentCount = 0;
		for (int i=1; ; i++) {
			String pref = store.getString(nameBase+i);
			if (pref.compareTo("") == 0) {
				break;
			}
			stringList.add(pref);
			currentCount++;
		}
		initialCount = currentCount;
	}

	/* (non-JavaDoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		stringList.removeAll();
	}

	protected void doStore() {
		for (int i=0; i<currentCount; i++) {
			getPreferenceStore().setValue(nameBase+(i+1),stringList.getItem(i));
		}
		for (int i=currentCount; i<initialCount; i++) {
			getPreferenceStore().setValue(nameBase+(i+1),"");
		}
	}

	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
