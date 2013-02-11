package gov.va.med.iss.mdebugger.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IContributionItem;
import gov.va.med.iss.mdebugger.MDebuggerPlugin;
import gov.va.med.iss.mdebugger.util.MDebuggerSteps;

/**
 * This class provides for a listing of the current stack
 * the debugged process.
 * <p>
 */

public class CurrentStackView extends ViewPart {
	public static TableViewer viewer;
	private Action runCommand;
	private Action stepOver1Command;
	private Action stepNextLineCommand;
	private Action stepIntoCommand;
	private Action stepOutCommand;
	private Action terminateCommand;
	private Action doubleClickAction;
	
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
			return null;
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public CurrentStackView() {
	}
	
	static public void updateView(String input) {
		if (! (viewer == null)) {
			IContentProvider mContentProvider = viewer.getContentProvider();
			((MDebuggerContentProvider)mContentProvider).setDocument(input);
			viewer.refresh();
		}
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new MDebuggerContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());
		viewer.getControl().addKeyListener(new MDebuggerKeyListener());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CurrentStackView.this.fillContextMenu(manager);
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
		IContributionItem[] contributionItems;
		contributionItems = manager.getItems();
		manager.add(runCommand);
		manager.add(new Separator());
		manager.add(stepOver1Command);
		manager.add(stepNextLineCommand);
		manager.add(stepOutCommand);
		manager.add(stepIntoCommand);
		manager.add(terminateCommand);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(runCommand);
		manager.add(stepOver1Command);
		manager.add(stepNextLineCommand);
		manager.add(stepOutCommand);
		manager.add(stepIntoCommand);
		manager.add(terminateCommand);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(runCommand);
		manager.add(stepOver1Command);
		manager.add(stepNextLineCommand);
		manager.add(stepIntoCommand);
		manager.add(stepOutCommand);
		manager.add(terminateCommand);
	}

	private void makeActions() {
		runCommand = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("RUN");
			}
		};
		runCommand.setText("Run");
		runCommand.setToolTipText("Run until finished or interrupted by a watch or breakpoint");
		runCommand.setImageDescriptor(MDebuggerPlugin.IMG_RUN_COMMAND);
		runCommand.setActionDefinitionId("gov.va.med.iss.debugger.run.resume");
		
		stepOver1Command = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("STEP");
			}
		};
		stepOver1Command.setText("Step Over One Command");
		stepOver1Command.setToolTipText("Step Over One Command");
		stepOver1Command.setImageDescriptor(MDebuggerPlugin.IMG_STEP_OVER1);

		stepNextLineCommand = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("STEPLINE");
			}
		};
		stepNextLineCommand.setText("Step Line");
		stepNextLineCommand.setToolTipText("Run until reaches next line or interrupted by a watch or breakpoint");
		stepNextLineCommand.setImageDescriptor(MDebuggerPlugin.IMG_STEP_LINE);
		stepNextLineCommand.setActionDefinitionId("gov.va.med.iss.debugger.run.stepline");
		
		stepIntoCommand = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("STEPINTO");
			}
		};
		stepIntoCommand.setText("Step Into");
		stepIntoCommand.setToolTipText("Step INTO the next level of DO or $SELECT command");
		stepIntoCommand.setImageDescriptor(MDebuggerPlugin.IMG_STEP_INTO);
		stepIntoCommand.setActionDefinitionId("gov.va.med.iss.debugger.run.stepinto");
		
		stepOutCommand = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("STEPOUT");
			}
		};
		stepOutCommand.setText("Step Out (return)");
		stepOutCommand.setToolTipText("Run until exited the current stack level or interrupted by a watch or breakpoint");
		stepOutCommand.setImageDescriptor(MDebuggerPlugin.IMG_STEP_OUT);
		stepOutCommand.setActionDefinitionId("gov.va.med.iss.debugger.run.stepout");
		
		terminateCommand = new Action() {
			public void run() {
				MDebuggerSteps.stepDebug("TERMINATE");
			}
		};
		terminateCommand.setText("Terminate");
		terminateCommand.setToolTipText("Terminate processing immediately");
		terminateCommand.setImageDescriptor(MDebuggerPlugin.IMG_TERMINATE);
		terminateCommand.setActionDefinitionId("gov.va.med.iss.debugger.run.terminate");
		

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
			"Current Stack View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}