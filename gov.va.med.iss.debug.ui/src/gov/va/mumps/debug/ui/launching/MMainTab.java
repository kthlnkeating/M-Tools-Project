package gov.va.mumps.debug.ui.launching;

import gov.va.mumps.debug.core.MDebugConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MMainTab extends AbstractLaunchConfigurationTab {

	private Text mCodeText;
	
	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		createVerticalSpacer(comp, 3);
		
		Label programLabel = new Label(comp, SWT.NONE);
		programLabel.setText("&M Code:");
		GridData gd = new GridData(GridData.BEGINNING);
		programLabel.setLayoutData(gd);
		programLabel.setFont(font);
		
		mCodeText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		mCodeText.setLayoutData(gd);
		mCodeText.setFont(font);
		mCodeText.setToolTipText("Ex: D TAG^ROUTINE");
		mCodeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String mCode = configuration.getAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, (String)null);
			if (mCode != null) {
				mCodeText.setText(mCode);
			}
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String mCode = mCodeText.getText().trim();

		//bug fix: convert syntax to upper case: ie: d ^ROUTINE to D ^ROUTINE. Convert anything that is not in ""'s to upper case
		StringBuilder sb = new StringBuilder(mCode);
		boolean inQuote = false;
		boolean foundQuote = false;
		for (int i = 0; i < mCode.length(); i++) {
			String charAt = mCode.charAt(i)+"";
			final String QUOTE = "\"";
			
			if (inQuote) {
				if (charAt.equals(QUOTE)) {
					if (foundQuote)
						foundQuote = false;
					else
						foundQuote = true;
				} else {
					if (foundQuote)
						inQuote = false;
					foundQuote = false;
				}
			}
			
			if (!inQuote) {
				if (charAt.matches("[a-z]"))
					sb.replace(i, i+1, charAt.toUpperCase());
				
				if (charAt.equals(QUOTE))
					inQuote = true;
			}
		}
		mCode = sb.toString();
		
		if (mCode.length() == 0) {
			mCode = null;
		}
		
		configuration.setAttribute(MDebugConstants.ATTR_M_ENTRY_TAG, mCode);
	}
	
	@Override
	public String getName() {
		return "Main";
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		//TODO: could validate this based on an RPC call to check if the routine is present?
//		String text = fProgramText.getText();
//		if (text.length() > 0) {
//			IPath path = new Path(text);
//			if (ResourcesPlugin.getWorkspace().getRoot().findMember(path) == null) {
//				setErrorMessage("Specified routine does not exist");
//				return false;
//			}
//		} else {
//			setMessage("Specify a program");
//		}
		return super.isValid(launchConfig);
	}
}
