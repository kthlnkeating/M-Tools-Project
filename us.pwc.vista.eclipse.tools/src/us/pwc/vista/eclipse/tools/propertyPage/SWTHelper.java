package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

class SWTHelper {
 	private static int getButtonWidthHint(Button button) {
 		PixelConverter pc = new PixelConverter(button);
 		int h= pc.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
 		return Math.max(h, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
 	}

	public static Button createButton(Composite parent, String text) {
		Button b = new Button(parent, SWT.PUSH);
		b.setFont(parent.getFont());
		b.setText(text);
		
		GridData gd = new GridData();		
		gd.widthHint= getButtonWidthHint(b);	
		gd.horizontalAlignment = GridData.FILL;	
		gd.verticalAlignment = SWT.BEGINNING;
		b.setLayoutData(gd);	

		return b;	
	}	

	public static Button createCheckButton(Composite parent, String text) {
		Button b = new Button(parent, SWT.CHECK);
		b.setFont(parent.getFont());
		b.setText(text);
		
		GridData gd = new GridData();		
		b.setLayoutData(gd);	

		return b;	
	}	

	public static Button[] createButtons(Composite parent) {
		Button[] result = new Button[2];
		Composite buttons = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		buttons.setLayout(layout);
		buttons.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.horizontalSpan = 1;
		gd.verticalAlignment = SWT.BEGINNING;
		buttons.setLayoutData(gd);
		
		Button newButton = createButton(buttons, "Add");		
		Button removeButton = createButton(buttons, "Remove");
		result[0] = newButton;
		result[1] = removeButton;
		return result;
	}
	
	public static Button[] createCheckButtons(Composite parent, String[] titles) {
		Button[] result = new Button[titles.length];
		for (int i=0; i<titles.length; ++i) {
			Button b = createCheckButton(parent, titles[i]);
			result[i] = b;
		}
		return result;
	}
	
	public static TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int width) {
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;		
	}
	
	public static Button createRadioBox(Composite parent, String text, int horizontalSpan) {
		Button b = new Button(parent, SWT.RADIO);
		b.setFont(parent.getFont());
		b.setText(text);
		
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		gd.horizontalAlignment  = SWT.BEGINNING;
		gd.verticalAlignment = SWT.BEGINNING;
		b.setLayoutData(gd);
		
		return b;
	}

	public static void addEmptyLabel(Composite parent, int horizontalSpan) {
		Label label = new Label(parent, SWT.NONE);
		label.setVisible(false);
		
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		label.setLayoutData(gd);
	}
	
	public static void addLabel(Composite parent, String text, int horizontalSpan) {
		Label label = new Label(parent, SWT.NONE | SWT.WRAP);
		label.setText(text);
		
		GridData gd = new GridData();
		gd.horizontalSpan = horizontalSpan;
		label.setLayoutData(gd);
	}
	

}
