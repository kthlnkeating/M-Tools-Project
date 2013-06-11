package gov.va.med.iss.meditor.utils;

import gov.va.med.iss.connection.actions.VistaConnection;
import gov.va.med.iss.connection.utilities.ConnectionUtilities;
import gov.va.med.iss.connection.utilities.MPiece;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//TODO: this is wrong. it extends dialog but it doesn't actually make use of any of its SWT invoked methods. It's basically just a plain java object which adds SWT listeners, but never disposes them
//This dialog should follow the examples here: http://www.vogella.com/articles/EclipseDialogs/article.html
//but also, there is 1 mistake in that tutorial in that it doesn't cleanup the listeners it adds. Whenver an SWT widget is created, there is an dispose method which will be called
//when the widget is closed (permanently). this is an ideal place to remove listeners.
public class RoutineNameDialogForm extends Dialog {
	private Label lblQuestion;
	private Text txtResponse;
	private Button btnOK;
	private Button btnCancel;
//	private Button btnUpperCase;
//	private Button btnReadOnly;
	private Label lblServer;
	private Label lblPort;
	private Text txtServer;
	private Text txtPort;
	private Text txtProject;
	private Label lblDirectory;
	private Combo comboDirectory;
//	private boolean isShowReadOnly = false;
	private boolean isShowDirectory = false;
	private boolean isMultSave = false;
	private RoutineNameDialogData data;
	private static ArrayList dropList = null;
	private Button btnDirectory = null;


	public RoutineNameDialogForm(Shell parent, int style) {
		super(parent, style);
	}
	
	public RoutineNameDialogForm(Shell parent) {
		this(parent, 0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public RoutineNameDialogData openMultiple() {
//		isShowReadOnly = false;
		isShowDirectory = true;
		return open();
	}
	
//	public RoutineNameDialogData open(boolean showReadOnly) {
//		//isShowReadOnly = showReadOnly;
//		return open();
//	}
	
	// 091201
	
	public RoutineNameDialogData openMultSave() {
		isMultSave = true;
		return openMultiple();
	}
	public RoutineNameDialogData open() {
		data = new RoutineNameDialogData();
		final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		if (isMultSave) {
			shell.setText("Multiple Routine Save");
		}
		else if (isShowDirectory) {
			shell.setText("Multiple Routine Load");
		}
		else {
			shell.setText("Load Routine");
		}
		if (isShowDirectory) {
			shell.setSize(600,200);
		}
		else {
			shell.setSize(300,200);
		}
		
		lblQuestion = new Label(shell, SWT.NONE);
		lblQuestion.setLocation(25,10);
		lblQuestion.setSize(80,20);
		lblQuestion.setText("Routine Name(s): ");
		if (!isShowDirectory) {
			lblQuestion.setText("Routine Name: ");
		}
		
		txtResponse = new Text(shell, SWT.BORDER);
		txtResponse.setLocation(115,10);
		txtResponse.setSize(100,20);
		if ((!isMultSave) && isShowDirectory){
			txtResponse.setToolTipText("Enter the name(s) of routines to be loaded from the selected server.  Names may include '*' to specify matches, or you may enter a comma separated list of names.");
		}
		else if (isMultSave){
			txtResponse.setToolTipText("Enter the name(s) of routines to be saved to the selected server.  Names may include '*' to specify matches, or you may enter a comma separated list of names.");
		}
		else {
			txtResponse.setToolTipText("Enter the name the routines to be loaded from the selected server.");
		}
		
		lblServer = new Label(shell, SWT.NONE);
		lblServer.setText("Server: ");
		lblServer.setLocation(25,30);
		lblServer.setSize(60,20);
		
		txtServer = new Text(shell, SWT.NONE);
		txtServer.setText(ConnectionUtilities.getServer());
		txtServer.setLocation(90,30);
		txtServer.setSize(200,20);
		txtServer.setEditable(false);
		
		lblPort = new Label(shell, SWT.NONE);
		lblPort.setText("Port: ");
		lblPort.setLocation(25,50);
		lblPort.setSize(60,20);
		
		txtPort = new Text(shell, SWT.NONE);
		txtPort.setText(ConnectionUtilities.getPort());
		txtPort.setLocation(90,50);
		txtPort.setSize(40,20);
		txtPort.setEditable(false);
		
		if (!(ConnectionUtilities.getProject().compareTo("") == 0)) {
			Label lblProject = new Label(shell, SWT.NONE);
			lblProject.setText("Project: ");
			lblProject.setLocation(140,50);
			lblProject.setSize(40,20);
			
			txtProject = new Text(shell, SWT.NONE);
			txtProject.setText(ConnectionUtilities.getProject());
			txtProject.setLocation(180,50);
			txtProject.setSize(100,20);
			txtProject.setEditable(false);
		}
		
		
//		btnUpperCase = new Button(shell, SWT.CHECK);
//		btnUpperCase.setText("Set name to ALL Caps");
//		btnUpperCase.setLocation(25,70);
//		btnUpperCase.setSize(150,25);
//		btnUpperCase.setSelection(true);
//		
//		if (isShowReadOnly) {
//			btnReadOnly = new Button(shell, SWT.CHECK);
//			btnReadOnly.setText("Load as Read-Only");
//			btnReadOnly.setLocation(25,95);
//			btnReadOnly.setSize(150,25);
//		}
		else if (isShowDirectory) {
			lblDirectory = new Label(shell, SWT.NONE);
			if (isMultSave) {
				lblDirectory.setText("Load from: ");
				lblDirectory.setToolTipText("Enter the full directory name for the directory in which the routine(s) will be found.");
			}
			else {
				lblDirectory.setText("Save to: ");
			}
			
			lblDirectory.setLocation(25,98);
			lblDirectory.setSize(50,25);
// JLI 110718 start
			btnDirectory = new Button(shell, SWT.PUSH);
			btnDirectory.setLocation(535,95);
			btnDirectory.setSize(30,20);
			btnDirectory.setText("...");
// JLI 110718 end			
			comboDirectory = new Combo(shell, SWT.DROP_DOWN);
			comboDirectory.setText("");
			comboDirectory.setLocation(80,95);
//			comboDirectory.setSize(475,25);
			comboDirectory.setSize(450,25);
			try {
				String currentServer = VistaConnection.getPrimaryServerID();
				String server = MPiece.getPiece(currentServer,";",1);
		        String location;
		        if (MPiece.getPiece(currentServer,";",4).compareTo("") != 0) {
		            location = MEditorUtilities.getProject(MPiece.getPiece(currentServer,";",4)).getLocation().toString();
		            comboDirectory.setText(location);
					if (! (new File(location).exists())) {
						new File(location).mkdirs();
					}
		        }
			} catch (Exception e) {
			}
			if (dropList == null) {
				dropList = new ArrayList();
			}
			for (int i=0 ; i<dropList.size(); i++ ) {
				comboDirectory.add((String) dropList.get(i));
			}
		}
		
		btnOK = new Button(shell, SWT.PUSH);
		btnOK.setText("OK");
		btnOK.setLocation(80,130);
		btnOK.setSize(55,25);
		shell.setDefaultButton(btnOK);
		
		btnCancel = new Button(shell, SWT.PUSH);
		btnCancel.setText("Cancel");
		btnCancel.setLocation(165,130);
		btnCancel.setSize(55,25);
		
//		if (isShowReadOnly) {
//			shell.setTabList(new Control[] {
//				txtResponse,
//				btnOK, btnCancel });
//		}
		if (isShowDirectory) {
			shell.setTabList(new Control[] {
					txtResponse, comboDirectory,
					btnOK, btnCancel });
		}
		else {
			shell.setTabList(new Control[] {
					txtResponse,
					btnOK, btnCancel });
		}
		
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				data.setButtonResponse(event.widget == btnOK);
				data.setTextResponse(txtResponse.getText());
//				data.setUpperCase(btnUpperCase.getSelection());
//				if (isShowReadOnly) {
//					data.setReadOnly(btnReadOnly.getSelection());
//				}
				if (isShowDirectory) {
					data.setDirectory(comboDirectory.getText());
					String result = comboDirectory.getText();
					boolean isFound = false;
					for (int i=0; i<dropList.size(); i++) {
						if (result.compareTo((String)dropList.get(i)) == 0) {
							isFound = true;
						}
					}
					if (!isFound) {
						dropList.add(result);
					}
				}
				shell.setVisible(false);
				shell.close();
			}
		};
		
		Listener directoryListener = new Listener(){
			public void handleEvent(Event event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);
				comboDirectory.setText(dlg.open());
			}
		};
		
		btnOK.addListener(SWT.Selection, listener); //TODO: remove this listener later, cannot just keep adding these or it makes the main UI thread to slow as it iterates over all the listeners, dead ones included
		btnCancel.addListener(SWT.Selection, listener);
		if (btnDirectory != null) {
			btnDirectory.addListener(SWT.Selection, directoryListener);
		}
		
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		return data;
	}

}
