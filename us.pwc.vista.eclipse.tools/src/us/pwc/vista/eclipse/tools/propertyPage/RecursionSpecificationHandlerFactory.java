package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import us.pwc.vista.eclipse.core.helper.SWTHelper;

public class RecursionSpecificationHandlerFactory {
	private static void createFilterTableColumns(TableViewer viewer) {
		TableViewerColumn filterColumn = SWTHelper.createTableViewerColumn(viewer, "Name Regular Expression", 175);
		filterColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NameFilter filter = (NameFilter) element;
				return filter.getValue();
			}
		});

		TableViewerColumn typeColumn = SWTHelper.createTableViewerColumn(viewer, "Type", 75);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NameFilter filter = (NameFilter) element;
				String rawType = filter.getType().toString();
				return rawType.charAt(0) + rawType.substring(1).toLowerCase();
			}
		});
		NameFilterTypeEditingSupport editingSupport = new NameFilterTypeEditingSupport(viewer);
		typeColumn.setEditingSupport(editingSupport);
	}
	
	private static TableViewer addFilterTable(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Table table = viewer.getTable();
		table.setFont(parent.getFont());
		
		createFilterTableColumns(viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setHeaderVisible(true);
		
		GridData gd = new GridData(GridData.FILL_BOTH); 
		gd.horizontalSpan = 1;
		gd.heightHint = 100;
		gd.widthHint = 50;
		table.setLayoutData(gd);
		
		return viewer;
	}
	
	private static Button[] addRadioButtons(Composite parent) {
		Button[] buttons = new Button[4];
		
		buttons[0] = SWTHelper.createRadioBox(parent, "Only Entry Tag", 1);
		SWTHelper.addEmptyLabel(parent, 2);
		buttons[1] = SWTHelper.createRadioBox(parent, "Entry Tag and Fallthrough Tags", 1);
		SWTHelper.addEmptyLabel(parent, 2);
		buttons[2] = SWTHelper.createRadioBox(parent, "All Tags Within Routine", 1);
		SWTHelper.addEmptyLabel(parent, 2);
		buttons[3] = SWTHelper.createRadioBox(parent, "Full Recursion With Routine Filter", 1);
		
		return buttons;
	}
	
	public static RecursionSpecificationHandler getInstance(Composite parent, String prefix) {
		SWTHelper.addLabel(parent, "Specify which execution lines should be analyzed:", 3);
		Button[] radioButtons = addRadioButtons(parent);
		
		TableViewer viewer = addFilterTable(parent);
		Button[] buttons = SWTHelper.createButtons(parent, new String[]{"Add", "Remove"});
		SWTHelper.addEmptyLabel(parent, 3);
				
		RecursionSpecificationHandler result = new RecursionSpecificationHandler(prefix, radioButtons, viewer, buttons[0], buttons[1]);
		return result;				
	}
}
