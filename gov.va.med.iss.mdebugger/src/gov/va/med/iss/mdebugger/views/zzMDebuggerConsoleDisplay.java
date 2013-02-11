package gov.va.med.iss.mdebugger.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.*;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import gov.va.med.iss.mdebugger.MDebuggerPlugin;
import gov.va.med.iss.mdebugger.util.MDebuggerSteps;
import gov.va.med.iss.mdebugger.views.MDebuggerKeyListener;

public class zzMDebuggerConsoleDisplay extends ViewPart {
		private static TextConsoleViewer viewer;
		private static MessageConsole console;
		private static MessageConsoleStream out;
		private static Document doc;
		private static IDocument iDoc;
		private static String xxxx;

		// text containing previous lines displayed
		public static String strval = "";
		
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
		public zzMDebuggerConsoleDisplay() {
		}
				
		static public void updateView(String input) {
			if (! (viewer == null)) {
/*
				IDocument doc1 = viewer.getDocument();
				doc1.set("My Time.");
				if (doc1 instanceof IDocument) {
					xxxx = "is idoc";
				}
				else {
					xxxx = "is NOT idoc";
				}
				xxxx = viewer.getDocument().get();
				viewer.getDocument().set(strval + input);
//				console.clearConsole();
//				viewer.refresh();
//				out = console.newMessageStream();
//				out.print(strval + input);
//				viewer.getDocument().set(strval + input);
//				IContentProvider mContentProvider = viewer.getContentProvider();
//				((MDebuggerContentProvider)mContentProvider).setDocument(strval + input + '\n');
				viewer.refresh();
*/
			}
		}

		/**
		 * This is a callback that will allow us
		 * to create the viewer and initialize it.
		 */
		public void createPartControl(Composite parent) {
			//viewer = new TextViewer(parent,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			console = new MessageConsole("MDebugger", null);
/*			try {
			doc = new Document();
			iDoc = (IDocument)doc;
			iDoc.set("");
			} catch (Exception e) {
				MDebuggerReadCommand.xxxx = e.getMessage();
			}
*/
			viewer = new TextConsoleViewer(parent, console);
//			viewer.setDocument(doc);
			out = console.newMessageStream();
//			viewer.setContentProvider(new MDebuggerContentProvider());
//			viewer.setLabelProvider(new ViewLabelProvider());
//			viewer.setSorter(null);
//			viewer.setInput(getViewSite());
			viewer.getControl().addKeyListener(new MDebuggerKeyListener());
		}

		private void showMessage(String message) {
			MessageDialog.openInformation(
				viewer.getControl().getShell(),
				"MDebugger Console Display",
				message);
		}

		/**
		 * Passing the focus request to the viewer's control.
		 */
		public void setFocus() {
			viewer.getControl().setFocus();
		}
}