package gov.va.med.foundations.security.vistalink;

import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.widgets.Dialog;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

import gov.va.med.iss.connection.VLConnectionPlugin;

public class DialogLogonForm extends Dialog {
	private static Label lblAnnouncements;
	private static Text txtAnnouncements;
	private static Button btnOK;
	private static Button btnCancel;
	private static Button btnChangeVerify;
	private static Label lblAccessCode;
	private static Label lblVerifyCode;
	private static Text txtAccessCode;
	private static Text txtVerifyCode;
//	private static DialogLogonData data;
	private static Image vaImage;
	private static Label btnLogo;
	private static Button btnSection508;
	private static Label lblServerLabel;
	private static Label lblServerInfo;
	private static Timer timer;
	private static CallbackLogon avCbh;
	
	private static final String DEFAULT_TITLE = "VistA Sign On";

	private static final String ACCESS_LABEL = "&Access Code: ";
	private static final char ACCESS_MNEMONIC = KeyEvent.VK_A;
	private static final String ACCESS_TOOLTIP = "Enter your VistA access code";

	private static final String VERIFY_LABEL = "&Verify Code: ";
	private static final char VERIFY_MNEMONIC = KeyEvent.VK_V;
	private static final String VERIFY_TOOLTIP = "Enter your VistA Verify code";

	private static final String OK_BUTTON_LABEL = "&OK";
	private static final char OK_BUTTON_MNEMONIC = KeyEvent.VK_O;
	private static final String OK_BUTTON_TOOLTIP = "Submits your login request to the server";

	private static final String CANCEL_BUTTON_LABEL = "&Cancel";
	private static final char CANCEL_BUTTON_MNEMONIC = KeyEvent.VK_C;
	private static final String CANCEL_BUTTON_TOOLTIP = "Cancels your login request";

	private static final String CVC_CHECKBOX_LABEL = "Chang&e Verify Code";
	private static final char CVC_CHECKBOX_MNEMONIC = KeyEvent.VK_E;
	private static final String CVC_CHECKBOX_TOOLTIP = "Ask to change your verify code";

	private static final String VA_LOGO = "./images/VAlogo.gif";
	private static final String VA_LOGO_TOOLTIP = "VA Logo";

	private static final String SERVER_LABEL = "Server: ";
	private static final String VOLUME_LABEL = "Volume: ";
	private static final String UCI_LABEL = "U C I: ";
	private static final String DEVICE_LABEL = "Device: ";

	private static final String SERVER_INFO_LABEL = "&Server Information: ";
	private static final String SERVER_INFO_TOOLTIP = "Information about the M server in use for the current connection";
	private static final char SERVER_INFO_MNEMONIC = KeyEvent.VK_S;

	private static final String BUTTON_508_TEXT = "Section &508 Information";
	private static final String BUTTON_508_TOOLTIP = "Display Section 508 compliance information for this application";
	private static final int BUTTON_508_MNEMONIC = KeyEvent.VK_5;
	private static final String[] TEXT_508_DISCLAIMER =
		{
			"V H A’s Office of Information, Health System Design & Development staff have made every",
			" effort during the design, development and testing of this login screen to ensure full",
			"accessibility to all users in compliance with Section 508 of the Rehabilitation Act of 1973, as",
			"amended. Please send any comments, questions or concerns regarding the accessibility of this",
			"login module to s d d migration @ m e d dot v a dot gov [sddmigration@med.va.gov]." };

	private static final String JTEXTAREA_TOOLTIP = "System Announcements";
	private static final String JTEXTAREA_LABEL = "System A&nnouncements:";
	private static final char JTEXTAREA_MNEMONIC = KeyEvent.VK_N;

	private static final int CODE_FIELD_COLUMNS = 13;
	
	private CallbackLogon callbackAV = null;


	public DialogLogonForm(Shell parent, int style) {
		super(parent, style);
	}
	
	public DialogLogonForm(Shell parent) {
		this(parent, 0);
	}

	public DialogLogonForm(CallbackLogon cbAV) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),0);
		avCbh = cbAV;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
//	public DialogLogonData open() {
	public static void showVistaAVGetAV(CallbackLogon cbAV) {
		avCbh = cbAV;
//		data = new DialogLogonData();
		final Shell shell = new Shell(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.DIALOG_TRIM |
				SWT.APPLICATION_MODAL);
		shell.setText(DEFAULT_TITLE);
		shell.setSize(700,500);
		Font font = new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD);

		lblAnnouncements = new Label(shell,SWT.NO_FOCUS);
		lblAnnouncements.setLocation(15,10);
		lblAnnouncements.setSize(150,20);
		lblAnnouncements.setFont(font);
		lblAnnouncements.setText(JTEXTAREA_LABEL);
/*
		StringBuffer sb1 = new StringBuffer(); 
		Vector introText = avCbh.getSetupAndIntroTextInfo().getIntroductoryTextLines();
		for (int lineCount = 0; lineCount < introText.size(); lineCount++) {
			sb1.append((String) introText.get(lineCount));
			sb1.append("\n");
		}
*/
		txtAnnouncements = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		txtAnnouncements.setLocation(15,30);
		txtAnnouncements.setSize(665,290);
		txtAnnouncements.setFont(new Font(shell.getDisplay(),"Courier New",10,SWT.BOLD));
		txtAnnouncements.setFont(font);
//		txtAnnouncements.setText(sb1.toString());
		String introText = avCbh.getSetupAndIntroTextInfo().getIntroductoryText();
		while (introText.indexOf("<BR>") > -1) {
			int loc = introText.indexOf("<BR>");
			introText = introText.substring(0,loc)+"\n"+introText.substring(loc+4);
		}
		txtAnnouncements.setText(introText); //(data.getMessageDisplayText());
		txtAnnouncements.setEditable(false);
		
		lblAccessCode = new Label(shell, SWT.NONE);
		lblAccessCode.setText(ACCESS_LABEL);
		lblAccessCode.setLocation(355,345);
		lblAccessCode.setSize(105,20);
		lblAccessCode.setFont(font);
		
		txtAccessCode = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		txtAccessCode.setText("");
		txtAccessCode.setLocation(470,340);
		txtAccessCode.setSize(120,20);
		txtAccessCode.setFont(font);
		txtAccessCode.setFocus();
		txtAccessCode.setToolTipText(ACCESS_TOOLTIP);
		
		lblVerifyCode = new Label(shell, SWT.NONE);
		lblVerifyCode.setText(VERIFY_LABEL);
		lblVerifyCode.setLocation(355,375);
		lblVerifyCode.setSize(105,20);
		lblVerifyCode.setFont(font);
		
		txtVerifyCode = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		txtVerifyCode.setText("");
		txtVerifyCode.setLocation(470,370);
		txtVerifyCode.setSize(120,20);
		txtVerifyCode.setFont(font);
		txtVerifyCode.setToolTipText(VERIFY_TOOLTIP);
		
//        vaImage =new Image(shell.getDisplay(),DialogLogonForm.class.getResourceAsStream(".\\images\\VAlogo.gif"));
        vaImage = VLConnectionPlugin.IMG_VA_LOGO.createImage(shell.getDisplay());
        btnLogo = new Label(shell,SWT.NONE);
        btnLogo.setLocation(25,320);
        btnLogo.setSize(130,100);
        btnLogo.setImage(vaImage);
        
		/*
		btnReadOnly = new Button(shell, SWT.CHECK);
		btnReadOnly.setText("Load as Read-Only");
		btnReadOnly.setLocation(25,95);
		btnReadOnly.setSize(150,25);
		*/
		
		btnOK = new Button(shell, SWT.PUSH);
		btnOK.setText(OK_BUTTON_LABEL);
		btnOK.setLocation(610,340);
		btnOK.setSize(75,25);
		btnOK.setFont(font);
		btnOK.setToolTipText(OK_BUTTON_TOOLTIP);
		shell.setDefaultButton(btnOK);
		
		btnCancel = new Button(shell, SWT.PUSH);
		btnCancel.setText(CANCEL_BUTTON_LABEL);
		btnCancel.setLocation(610,370);
		btnCancel.setSize(75,25);
		btnCancel.setFont(font);
		btnCancel.setToolTipText(CANCEL_BUTTON_TOOLTIP);
		
		btnChangeVerify = new Button(shell, SWT.CHECK);
		btnChangeVerify.setText(CVC_CHECKBOX_LABEL);
		btnChangeVerify.setLocation(355,400);
		btnChangeVerify.setSize(165,25);
		btnChangeVerify.setFont(font);
		btnChangeVerify.setToolTipText(CVC_CHECKBOX_TOOLTIP);
		
		btnSection508 = new Button(shell, SWT.PUSH);
		btnSection508.setText(BUTTON_508_TEXT);
		btnSection508.setFont(font);
		btnSection508.setLocation(25,420);
		btnSection508.setSize(200,25);
		btnSection508.setToolTipText(BUTTON_508_TOOLTIP);
		
		lblServerLabel = new Label(shell, SWT.NONE);
		lblServerLabel.setText(SERVER_INFO_LABEL);
		lblServerLabel.setLocation(25,450);
		lblServerLabel.setSize(170,25);
		lblServerLabel.setFont(font);
		
		lblServerInfo = new Label(shell, SWT.BORDER);
		lblServerInfo.setLocation(200,450);
		lblServerInfo.setSize(400,20);
		
		lblServerInfo.setText("");
		VistaSetupAndIntroTextInfo setupInfo = avCbh.getSetupAndIntroTextInfo();
		StringBuffer sb = new StringBuffer(" ");
		sb.append(SERVER_LABEL);
		sb.append(setupInfo.getServerName());
		sb.append("; ");
		sb.append(VOLUME_LABEL);
		sb.append(setupInfo.getVolume());
		sb.append("; ");
		sb.append(UCI_LABEL);
		sb.append(setupInfo.getUci());
		sb.append("; ");
		sb.append(DEVICE_LABEL);
		sb.append(setupInfo.getDevice());
		lblServerInfo.setText(sb.toString());
		
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
//				data.setButtonResponse(event.widget == btnOK);
//				data.setAccessCode(txtAccessCode.getText());
//				data.setVerifyCode(txtVerifyCode.getText());
//				data.setChangeVerify(btnChangeVerify.getSelection());
				if (event.widget == btnOK) {
					okBtnSelected();
				}
				else if (event.widget == btnCancel) {
					cancelBtnSelected();
				}
				else {
					otherExit();
				}
				timer.cancel();
				shell.setVisible(false);
				shell.close();
			}
		};
		
		btnOK.addListener(SWT.Selection, listener);
		btnCancel.addListener(SWT.Selection, listener);

		int delay = 1000 * avCbh.getTimeoutInSeconds();
		TimerTask taskPerformer = new TimerTask() {
			public void run() {
				doTimeout();
			}
		};
		timer = new Timer();
		timer.schedule(taskPerformer,delay);

		
		shell.open();
		Display display = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
//		return data;
	}
	
	private static void okBtnSelected() {
		avCbh.setSelectedOption(avCbh.KEYPRESS_OK);
		avCbh.setAccessCode(txtAccessCode.getText().toCharArray());
		avCbh.setVerifyCode(txtVerifyCode.getText().toCharArray());
		avCbh.setRequestCvc(btnChangeVerify.getSelection());
	}
	
	private static void cancelBtnSelected() {
		avCbh.setSelectedOption(avCbh.KEYPRESS_CANCEL);
	}
	
	private static void otherExit() {
		avCbh.setSelectedOption(avCbh.KEYPRESS_TIMEOUT);
	}
	
	private static void doTimeout() {
		avCbh.setSelectedOption(CallbackConfirm.KEYPRESS_TIMEOUT);
	}
}
