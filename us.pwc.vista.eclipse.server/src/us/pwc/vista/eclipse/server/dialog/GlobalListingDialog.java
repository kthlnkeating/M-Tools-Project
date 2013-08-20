package us.pwc.vista.eclipse.server.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;

import us.pwc.vista.eclipse.core.helper.SWTHelper;
import us.pwc.vista.eclipse.server.Messages;
import us.pwc.vista.eclipse.server.VistAServerPlugin;

/**
 * Loads globals from server. 
 */
public class GlobalListingDialog extends Dialog {
	private static final String BOUNDS = "bounds"; //$NON-NLS-1$
	private static final String MAIN = "main"; //$NON-NLS-1$
	private static final String GLOBAL_HISTORY = "globalHistory"; //$NON-NLS-1$
	
	private static final int GLOBAL_HISTORY_SIZE = 10;
	
	private String title;
	
	private Combo global;

	private Button normalButton;
	private Button dataOnlyButton;
	private Button setupCopyButton;
	
	private Text searchText;
	private Button caseSensitive;
	private Button searchDataOnly;
	
	private GlobalListingData data;
	
	public GlobalListingDialog(Shell parentShell, String title) {
		super(parentShell);
		this.title = title;
	}

	@Override
	public void create() {
		super.create();

		IDialogSettings settings = VistAServerPlugin.getDefault().getDialogSettings(this, MAIN);	
		this.updateGlobalHistory(settings);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(this.title);
	}
		
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite panel = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		panel.setLayout(layout);
		SWTHelper.setGridData(panel, SWT.FILL, true, SWT.FILL, true);

		Composite inputPanel = createInputPanel(panel);
		SWTHelper.setGridData(inputPanel, SWT.FILL, true, SWT.TOP, false);

		Composite returnTypeGroup = createReturnTypeGroup(panel);
		SWTHelper.setGridData(returnTypeGroup, SWT.FILL, true, SWT.FILL, false);

		Composite optionsGroup = createTextSearchGroup(panel);
		SWTHelper.setGridData(optionsGroup, SWT.FILL, true, SWT.FILL, true);

		return panel;
	}

	private Composite createInputPanel(Composite parent) {
		Composite panel= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		panel.setLayout(layout);

		Label globalNameLabel = new Label(panel, SWT.LEFT);
		globalNameLabel.setText(Messages.DLG_GLOBAL_LISTING_GLNAME);
		SWTHelper.setGridData(globalNameLabel, SWT.LEFT, false, SWT.CENTER, false);

		this.global= new Combo(panel, SWT.DROP_DOWN | SWT.BORDER);
		SWTHelper.setGridData(this.global, SWT.FILL, true, SWT.CENTER, false);

		return panel;
	}

	private Composite createReturnTypeGroup(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		panel.setLayout(layout);

		Group group= new Group(panel, SWT.NULL | SWT.SHADOW_ETCHED_IN);
		group.setText(Messages.DLG_GLOBAL_LISTING_RTNTYP);
		GridLayout groupLayout= new GridLayout();
		groupLayout.numColumns= 3;
		group.setLayout(groupLayout);
		SWTHelper.setGridData(group, SWT.FILL, true, SWT.FILL, true);

		this.normalButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this.normalButton.setText(Messages.DLG_GLOBAL_LISTING_RTNTYP_0);
		this.normalButton.setToolTipText(Messages.DLG_GLOBAL_LISTING_RTNTYP_0_TT);
		SWTHelper.setGridData(this.normalButton, SWT.LEFT, false, SWT.CENTER, false);

		this.dataOnlyButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this.dataOnlyButton.setText(Messages.DLG_GLOBAL_LISTING_RTNTYP_1);
		this.dataOnlyButton.setToolTipText(Messages.DLG_GLOBAL_LISTING_RTNTYP_1_TT);
		SWTHelper.setGridData(this.dataOnlyButton, SWT.LEFT, false, SWT.CENTER, false);

		this.setupCopyButton = new Button(group, SWT.RADIO | SWT.LEFT);
		this.setupCopyButton.setText(Messages.DLG_GLOBAL_LISTING_RTNTYP_2);
		this.setupCopyButton.setToolTipText(Messages.DLG_GLOBAL_LISTING_RTNTYP_2_TT);
		SWTHelper.setGridData(this.setupCopyButton, SWT.LEFT, false, SWT.CENTER, false);

		this.normalButton.setSelection(true);
		this.dataOnlyButton.setSelection(false);
		this.setupCopyButton.setSelection(false);

		return panel;
	}

	private Composite createTextSearchGroup(Composite parent) {
		Composite panel= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		panel.setLayout(layout);

		Group group= new Group(panel, SWT.SHADOW_NONE);
		group.setText(Messages.DLG_GLOBAL_LISTING_TXTSRC);
		GridLayout groupLayout= new GridLayout();
		groupLayout.numColumns = 2;
		group.setLayout(groupLayout);
		SWTHelper.setGridData(group, SWT.FILL, true, SWT.FILL, true);

		createSearchText(group);
		createCaseSensitive(group);
		createTextSearchOptions(group);		
		
		return panel;
	}
	
	private void createSearchText(Composite parent) {
		Label searchTextLabel = new Label(parent, SWT.LEFT);
		searchTextLabel.setText(Messages.DLG_GLOBAL_LISTING_TXTSRC_T);
		SWTHelper.setGridData(searchTextLabel, SWT.LEFT, false, SWT.CENTER, false);

		this.searchText = new Text(parent, SWT.BORDER);
		this.searchText.setToolTipText(Messages.DLG_GLOBAL_LISTING_TXTSRC_TT);
		SWTHelper.setGridData(this.searchText, SWT.FILL, true, SWT.CENTER, false);
	}

	private void createCaseSensitive(Composite parent) {
		this.caseSensitive = new Button(parent, SWT.CHECK | SWT.LEFT);
		this.caseSensitive.setText(Messages.DLG_GLOBAL_LISTING_TXTSRC_0);
		GridData gd = SWTHelper.setGridData(this.caseSensitive, SWT.LEFT, false, SWT.CENTER, false);
		gd.horizontalSpan= 2;		
	}
	
	private void createTextSearchOptions(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		panel.setLayout(layout);
		GridData gd = SWTHelper.setGridData(panel, SWT.FILL, true, SWT.FILL, true);
		gd.horizontalSpan= 2;

		this.searchDataOnly = new Button(panel, SWT.RADIO | SWT.LEFT);
		this.searchDataOnly.setText(Messages.DLG_GLOBAL_LISTING_TXTSRC_1);		
		SWTHelper.setGridData(this.searchDataOnly, SWT.LEFT, false, SWT.CENTER, false);

		Button b2 = new Button(panel, SWT.RADIO | SWT.LEFT);
		b2.setText(Messages.DLG_GLOBAL_LISTING_TXTSRC_2);		
		SWTHelper.setGridData(b2, SWT.LEFT, false, SWT.CENTER, false);

		this.searchDataOnly.setSelection(true);
		b2.setSelection(false);
	}

	private void updateGlobalHistory(IDialogSettings settings) {
		this.global.removeAll();
		String[] previousValues = settings.getArray(GLOBAL_HISTORY);
		if (previousValues != null) {
			for (String value : previousValues) {
				this.global.add(value);
			}
		}
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public boolean close() {
		if (this.getReturnCode() == OK) {
			storeData();
		}
		storeSettings();
		return super.close();
	}
	
	private String[] comboToHistory() {
		String text = this.global.getText();
		String[] items = this.global.getItems();
		if (text.isEmpty()) {
			return items;
		} else {
			int expectedSize = items.length + 1; 	
			List<String> result = new ArrayList<String>(expectedSize);
			Set<String> uniqueEnforcer = new HashSet<String>(expectedSize);
			result.add(text);
			uniqueEnforcer.add(text);
			int count = 1;
			for (String item : items) {
				if (! uniqueEnforcer.contains(item)) {
					result.add(item);
					uniqueEnforcer.add(item);
					++count;
					if (count > GLOBAL_HISTORY_SIZE) break;
				}
			}
			return result.toArray(new String[0]);
		}
	}

	private void storeSettings() {
		if (this.getReturnCode() == OK) {
			IDialogSettings settings = VistAServerPlugin.getDefault().getDialogSettings(this, MAIN);		
			String[] globalHistory = this.comboToHistory();
			settings.put(GLOBAL_HISTORY, globalHistory);
		}
	}

	private void storeData() {
		this.data = new GlobalListingData();
		this.data.globalName = this.global.getText();
		this.data.setupCopySelected = this.setupCopyButton.getSelection();	
		this.data.dataOnlySelected = this.dataOnlyButton.getSelection();
		this.data.searchText = this.searchText.getText();
		this.data.isSearchDataOnly = this.searchDataOnly.getSelection();
		this.data.isCaseSensitive = this.caseSensitive.getSelection();
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		return VistAServerPlugin.getDefault().getDialogSettings(this, BOUNDS);
	}
	
	public GlobalListingData getData() {
		return this.data;
	}
}
