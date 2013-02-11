package gov.va.med.iss.mdebugger;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.jface.viewers.TableViewer;

public class SashTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Display display = new Display();
		final Shell parent1 = new Shell(display);
//		ViewForm comp1 = new ViewForm(parent1,SWT.NONE);
		Composite comp1 = new Composite(parent1,SWT.NONE); //SWT.V_SCROLL | SWT.H_SCROLL);
//		List comp1 = new List(parent1,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		Sash sash = new Sash(parent1,SWT.HORIZONTAL);
		Text text = new Text(parent1,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		text.setText("This is the Text entry");
		FormLayout layout = new FormLayout();
		parent1.setLayout(layout);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
	    comp1.setLayout(fillLayout);
//	    FormLayout gridLayout = new FormLayout();
//	    comp1.setLayout(gridLayout);
		FormData comp1Data = new FormData();
		comp1Data.top = new FormAttachment(0,0);
		comp1Data.bottom = new FormAttachment(sash);
		comp1Data.left = new FormAttachment(0);
		comp1Data.right = new FormAttachment(100);
//		comp1.setLayoutData(comp1Data);
		final FormData sashData = new FormData();
		sashData.top = new FormAttachment(80);
		sashData.left = new FormAttachment(0);
		sashData.right = new FormAttachment(100);
		sash.setLayoutData(sashData);
		sash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
			if (event.detail != SWT.DRAG){
				sashData.top = new FormAttachment(0,event.y);
				parent1.layout();
			}
			}
		});

		FormData textData = new FormData();
		textData.top = new FormAttachment(sash);
		textData.bottom = new FormAttachment(100);
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		text.setLayoutData(textData);
/*		
		List list = new List(comp1,SWT.H_SCROLL|SWT.V_SCROLL);
		FormData listData = new FormData();
		listData.top = new FormAttachment(0);
		listData.bottom = new FormAttachment(100);
		listData.left = new FormAttachment(0);
		listData.right= new FormAttachment(100);
		list.setLayoutData(listData);
		list.add("This is one.");
*/
		TableViewer viewer = new TableViewer(comp1, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setSorter(null);

		parent1.pack();
		parent1.open();
		while (!parent1.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}

}
