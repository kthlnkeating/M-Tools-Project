package us.pwc.vista.eclipse.core.helper;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class SWTHelper {
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
		return createCheckButton(parent, text, 1);
	}
	
	public static Button createCheckButton(Composite parent, String text, int horizontalSpan) {
		Button b = new Button(parent, SWT.CHECK);
		b.setFont(parent.getFont());
		b.setText(text);
		
		GridData gd = new GridData();		
		gd.horizontalSpan = horizontalSpan;
		b.setLayoutData(gd);	

		return b;	
	}	

	public static Button[] createButtons(Composite parent, String[] buttonNames) {
		Button[] result = new Button[buttonNames.length];
		Composite buttons = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		buttons.setLayout(layout);
		buttons.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.horizontalSpan = 1;
		gd.verticalAlignment = SWT.BEGINNING;
		buttons.setLayoutData(gd);
		int i = 0;
		for (String name : buttonNames) {
			result[i] = createButton(buttons, name);
			++i;
		}
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
		if (width > 0) {
			column.setWidth(width);
		}
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
	
	public static Label addLabel(Composite parent, String text, int horizontalSpan) {
		Label label = new Label(parent, SWT.WRAP | SWT.LEFT);
		label.setText(text);		
		GridData gd = SWTHelper.setGridData(label, SWT.FILL, true, SWT.CENTER, false);
		gd.horizontalSpan = horizontalSpan;
		return label;
	}
	
	public static GridData setGridData(Control component, int horizontalAlignment, boolean grabExcessHorizontalSpace, int verticalAlignment, boolean grabExcessVerticalSpace) {
		GridData gd = new GridData();
		gd.horizontalAlignment= horizontalAlignment;
		gd.grabExcessHorizontalSpace= grabExcessHorizontalSpace;
		gd.verticalAlignment= verticalAlignment;
		gd.grabExcessVerticalSpace= grabExcessVerticalSpace;
		component.setLayoutData(gd);
		return gd;
	}
	
	public static Composite createComposite(Composite parent, int numColumns) {
		Composite result = new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		result.setLayout(layout);
		layout.numColumns = numColumns;
		return result;
	}
	
	public static Text createLabelTextPair(Composite parent, String labelText) {
		Font font = parent.getFont();
		
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setFont(font);
	
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("");
		text.setFont(font);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);	

		return text;
	}
	
	public static Combo createLabelComboPair(Composite parent, String labelText) {
		Font font = parent.getFont();

		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setFont(font);
				
		Combo combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		combo.setFont(font);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(gd);	
		
		return combo;
	}

	public static Label createLabelLabelPair(Composite parent, String labelText) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		Label target = new Label(parent, SWT.NONE);
		target.setText("");

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		target.setLayoutData(gd);	

		return target;
	}
}
