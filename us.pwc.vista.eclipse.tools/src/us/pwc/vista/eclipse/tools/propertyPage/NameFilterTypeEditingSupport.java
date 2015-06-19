package us.pwc.vista.eclipse.tools.propertyPage;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public final class NameFilterTypeEditingSupport extends EditingSupport {
    private ComboBoxViewerCellEditor cellEditor;
    
    public NameFilterTypeEditingSupport(ColumnViewer viewer) {
        super(viewer);
        cellEditor = new ComboBoxViewerCellEditor((Composite) this.getViewer().getControl(), SWT.READ_ONLY);
        cellEditor.setLabelProvider(new LabelProvider());
        cellEditor.setContentProvider(new ArrayContentProvider());
        NameFilterType[] types = NameFilterType.values();
        String[] displayTypes = new String[types.length];
        for (int i=0; i<types.length; ++i) {
        	String typeAsString = types[i].toString();
        	displayTypes[i] = typeAsString.charAt(0) + typeAsString.substring(1).toLowerCase();
        }       
        cellEditor.setInput(displayTypes);
    }
    
    @Override
    protected CellEditor getCellEditor(Object element) {
        return this.cellEditor;
    }
    
    @Override
    protected boolean canEdit(Object element) {
        return true;
    }
    
    @Override
    protected Object getValue(Object element) {
    	NameFilter filter = (NameFilter) element;
    	NameFilterType type = filter.getType();
    	String typeAsString = type.toString();
    	return typeAsString.charAt(0) + typeAsString.substring(1).toLowerCase();
    }
    
    @Override
    protected void setValue(Object element, Object value) {
    	NameFilter filter = (NameFilter) element;
    	String newValue = ((String) value).toUpperCase();
    	NameFilterType newType = NameFilterType.valueOf(newValue);
    	if (newType != filter.getType())  {
    		filter.setType(newType);
    		this.getViewer().refresh();
        }
    }    
}
