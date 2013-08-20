package gov.va.med.iss.connection.preferences;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class ConnectionPreferencePage extends PropertyPage implements IWorkbenchPreferencePage {	
	private IWorkbench workbench;
	
	private List servers;
	
	private Button addButton;
	private Button removeButton;
	private Button moveUpButton;
	private Button moveDownButton;
	
	@Override
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		contents.setLayout(gl);
		
		SWTHelper.addLabel(contents, "Use the 'Add' button to add a new server.", 2);
		SWTHelper.addLabel(contents, "Enter a *BRIEF* name for the server, the port number and IP address or URL.", 2);
		SWTHelper.addLabel(contents, "Name is used as a property to assign a server to a project.", 2);
		SWTHelper.addLabel(contents, "Servers:", 2);

		this.addList(contents);
		Button[] buttons = SWTHelper.createButtons(contents, new String[]{"Add", "Remove", "Move Down", "Move Up"});
		this.addButton = buttons[0];
		this.removeButton = buttons[1];
		this.moveDownButton = buttons[2];
		this.moveUpButton = buttons[3];
				
		this.initialize();
		this.attachListeners(buttons, parent.getShell());
		return contents;
	}
	
	private void addList(Composite parent) {
		this.servers = new List(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		this.servers.setFont(parent.getFont());
        GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
        gd.grabExcessHorizontalSpace = true;
        this.servers.setLayoutData(gd);
	}
	
	private void initialize() {
		java.util.List<ServerData> serverDataList = VistAConnectionPrefs.getServers();
		this.servers.removeAll();
		for (ServerData serverData : serverDataList) {
			String s = serverData.toString();
			this.servers.add(s);
		}
		this.disableSelectionBasedButtons();
	}

	@Override
	public boolean performOk() {
		java.util.List<ServerData> dataList = new ArrayList<ServerData>();
		int count = this.servers.getItemCount();
		for (int i=0; i<count; ++i) {
			String s = this.servers.getItem(i);
			ServerData sd = ServerData.valueOf(s);
			dataList.add(sd);
		}
		VistAConnectionPrefs.setServers(dataList);
		return super.performOk();
	}
	
	private void attachListeners(Button[] buttons, final Shell shell) {
	    this.servers.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConnectionPreferencePage.this.handleSelectionChanged();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	    this.addButton.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		ConnectionPreferencePage.this.handleAddServer();
	    	}
	      });

	   this.removeButton.addListener(SWT.Selection, new Listener() {
		   @Override
		   public void handleEvent(Event e) {
	    		ConnectionPreferencePage.this.handleRemoveServer();
	        }
	    });
	    
	    this.moveDownButton.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		ConnectionPreferencePage.this.handleMoveDownServer();
	    	}
	    });	    

	    this.moveUpButton.addListener(SWT.Selection, new Listener() {
	    	@Override
	    	public void handleEvent(Event e) {
	    		ConnectionPreferencePage.this.handleMoveUpServer();
	    	}
	    });	    
	}
	
	private void handleSelectionChanged() {
		int index = this.servers.getSelectionIndex();
		if (index < 0) {
			this.disableSelectionBasedButtons();
		} else {
			this.removeButton.setEnabled(true);
			this.moveUpButton.setEnabled(index > 0);
			this.moveDownButton.setEnabled(index < this.servers.getItemCount()-1);
		}		
	}
	
	private void disableSelectionBasedButtons() {
		this.removeButton.setEnabled(false);
		this.moveUpButton.setEnabled(false);
		this.moveDownButton.setEnabled(false);		
	}
	
	protected void handleAddServer() {		
		Shell shell = this.workbench.getActiveWorkbenchWindow().getShell();
		AddServerDialog dialog = new AddServerDialog(shell, "Add Server");
		int result = dialog.open();
		if (result == AddServerDialog.OK) {
			ServerData data = dialog.getData();
			if (data.isComplete()) {
				this.servers.add(data.toString());
			}
		}
	}
	
	private void handleRemoveServer() {
		int index = this.servers.getSelectionIndex();
		if (index > -1) {
			this.servers.remove(index);
		}		
	}
	
	private void handleMoveUpServer() {
		int index = this.servers.getSelectionIndex();	        	
		if (index > 0) {
			this.moveServerLocation(index, index-1);
		}		
	}
	
	private void handleMoveDownServer() {
		int index = this.servers.getSelectionIndex();	        	
		if (index < this.servers.getItemCount() - 1) {
			this.moveServerLocation(index, index+1);
		}		
	}
	
	private void moveServerLocation(int oldIndex, int newIndex) {
		String value = this.servers.getItem(oldIndex);
		this.servers.remove(oldIndex);
		this.servers.add(value, newIndex);
		this.servers.select(newIndex);
		this.handleSelectionChanged();
	}
}