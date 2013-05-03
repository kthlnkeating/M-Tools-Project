package gov.va.mumps.debug.ui.custom.variables;

import gov.va.mumps.debug.core.MDebugConstants;
import gov.va.mumps.debug.core.model.MDebugTarget;
import gov.va.mumps.debug.xtdebug.vo.VariableVO;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class VariablesView extends ViewPart implements ISelectionListener {

	private TableViewer viewer;
	private VariableNameFilter viewFilter;

	// Composite container;

	@Override
	public void createPartControl(Composite parent) {
		
		//container = new Composite(parent, SWT.NO_BACKGROUND | SWT.NO_SCROLL);
		viewFilter = new VariableNameFilter();
		
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		parent.setLayoutData(gridData);
		
//	    Label searchLabel = new Label(parent, SWT.NONE);
//	    searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		searchText.addKeyListener(new KeyAdapter() {
			@Override
		      public void keyReleased(KeyEvent ke) {
		          viewFilter.setPattern(searchText.getText());
		          viewer.refresh();
		        }
		});

	    createViewer(parent); 
	}

	private Viewer createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider
		// viewer.setInput(ModelProvider.INSTANCE.getPersons());
		// Make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// Set the sorter for the table

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		// gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		viewer.addFilter(viewFilter);
		return viewer;
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent) {
		String[] titles = { "Name", "Value" };
		int[] bounds = { 120, 150 };

		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VariableVO v = (VariableVO) element;
				return v.getName();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				VariableVO v = (VariableVO) element;
				return v.getValue();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			int colNumber) {
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(IDebugUIConstants.ID_DEBUG_VIEW, this);
		super.dispose();
	}

	@Override
	public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
		System.out.println("1");
		IAdaptable adaptable = DebugUITools.getDebugContext();
		Object input = null;
		if (adaptable != null) {
			IDebugElement element = (IDebugElement) adaptable
					.getAdapter(IDebugElement.class);
			if (element != null) {
				System.out.println("2");
				if (element.getModelIdentifier().equals(
						MDebugConstants.M_DEBUG_MODEL)) {
					if (element.getDebugTarget() instanceof MDebugTarget) {
						input = ((MDebugTarget) element.getDebugTarget())
								.getAllVariables();
						;
					}
				}
			}
		}
		viewer.setInput(input);
	}

}
