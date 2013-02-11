package gov.va.med.iss.mdebugger.views;


import gov.va.med.iss.mdebugger.views.BreakpointView.NameSorter;
import gov.va.med.iss.mdebugger.views.BreakpointView.ViewLabelProvider;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class InitializationValuesView extends ViewPart {
	private static TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	private static Text txtEdit;
	private static String lastInput = "";
	private static String stringVal = "";
	private static Text txtValue;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return null; //PlatformUI.getWorkbench().
					//getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public InitializationValuesView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		final Composite parent1 = parent;
		Composite comp1 = new Composite(parent1,SWT.NONE); //.V_SCROLL | SWT.H_SCROLL);
		txtEdit = new Text(parent1,SWT.NONE); //SWT.H_SCROLL|SWT.V_SCROLL);
		Label lblEdit = new Label(parent1,SWT.LEFT);
		final Button btnAdd = new Button(parent1,SWT.PUSH);
		FormLayout layout = new FormLayout();
		parent1.setLayout(layout);
		FormData comp1Data = new FormData();
		comp1Data.top = new FormAttachment(0);
		comp1Data.bottom = new FormAttachment(94);
		comp1Data.left = new FormAttachment(0);
		comp1Data.right = new FormAttachment(100);
		comp1.setLayoutData(comp1Data);
		
		lblEdit.setText("Enter Variable Name: ");
		FormData lblEditData = new FormData();
		lblEditData.top = new FormAttachment(comp1);
		lblEditData.bottom = new FormAttachment(100);
		lblEditData.left = new FormAttachment(0);
		lblEditData.right = new FormAttachment(0,105); //(18);
		lblEdit.setLayoutData(lblEditData);
		lblEdit.setToolTipText("Enter the Variable Name for the variable to be initialized.");
		
		FormData textData = new FormData();
		textData.top = new FormAttachment(comp1);
		textData.bottom = new FormAttachment(100);
		textData.left = new FormAttachment(lblEdit);
		textData.right = new FormAttachment(40);
		txtEdit.setLayoutData(textData);
		txtEdit.setEditable(true);
		
		Label lblValue = new Label(parent1,SWT.LEFT);
		FormData lblValueData = new FormData();
		lblValueData.top = new FormAttachment(comp1);
		lblValueData.bottom = new FormAttachment(100);
		lblValueData.left = new FormAttachment(txtEdit);
		lblValueData.right = new FormAttachment(txtEdit,180); //(50);
		lblValue.setLayoutData(lblValueData);
		lblValue.setToolTipText("Enter initialization VALUE for variable");
		lblValue.setText("Enter Value: ");
		
		txtValue = new Text(parent1,SWT.NONE);
		FormData txtValueData = new FormData();
		txtValueData.top = new FormAttachment(comp1);
		txtValueData.bottom = new FormAttachment(100);
		txtValueData.left = new FormAttachment(lblValue);
		txtValueData.right = new FormAttachment(90);
		txtValue.setLayoutData(txtValueData);
		
		FormData btnData = new FormData();
		btnData.top = new FormAttachment(comp1);
		btnData.bottom = new FormAttachment(100);
		btnData.left = new FormAttachment(txtValue);
		btnData.right = new FormAttachment(100);
		btnAdd.setLayoutData(btnData);
		btnAdd.setText("&Add");
		btnAdd.setEnabled(true);
		btnAdd.setVisible(true);
	    btnAdd.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    	   if (e.getSource() == btnAdd){
	    	   		if (txtEdit.getText().compareTo("") != 0 ) {
	    	   			setUp();
	    	   			}
	    	   }   
	    	}
	    	});
		
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		comp1.setLayout(fillLayout);
		viewer = new TableViewer(comp1, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new MDebuggerContentProvider()); // MDebuggerContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		
/*
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
*/
	}
	static public void updateView(String input) {
		lastInput = input;
		if (! (viewer == null)) {
			MDebuggerContentProvider mContentProvider = (MDebuggerContentProvider)viewer.getContentProvider();
			mContentProvider.setDocument(input);
			viewer.refresh();
		}
	}

	
	private static void setUp() {
		stringVal = stringVal + txtEdit.getText() + "  =  " + txtValue.getText() + "\n";
		updateView(stringVal);
		txtEdit.setText("");
		txtValue.setText("");
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				InitializationValuesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Current Stack",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}