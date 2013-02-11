package gov.va.med.iss.mdebugger.views;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyAdapter;
import java.util.EventListener;

public class MDebuggerKeyListener extends KeyAdapter implements EventListener,KeyListener {

	private String str = "";
	private String str1 = "";

	public void keyPressed(KeyEvent e) {
		str1 = e.toString();
		if ( !(e.character == '\0')) {
			int threadval = MDebuggerReadCommand.threadToken;
			while ( !(MDebuggerReadCommand.threadToken == 1)) {
				threadval = MDebuggerReadCommand.threadToken;
				//if (threadToken == 0) {
				if (threadval == 0) {
					MDebuggerReadCommand.threadToken = 1;
					try {
						Thread.sleep(20);
					} catch (Exception except) {
						
					}
				}
			}
			str = MDebuggerReadCommand.newText;
			str = str + e.character;
			MDebuggerReadCommand.newText = str;
			MDebuggerReadCommand.threadToken = 0;
			MDebuggerReadCommand.checkChars();
		}
	}
	
	public void keyTyped(KeyEvent e) {
		str = e.toString();
/*
		int id = e.getID();
		if (id == KeyEvent.KEY_TYPED) {
			str = str + e.getKeyChar();
		}
		else {
			int keyCode = e.getKeyCode();
			str = str + "["+ keyCode +"("+KeyEvent.getKeyText(keyCode)+")]";
		}
*/
	}
	
	public void keyReleased(KeyEvent e) {
		str = e.toString();
/*
		KeyEvent.
		int id = e.getID();
		if (id == KeyEvent.KEY_TYPED) {
			str = str + e.getKeyChar();
		}
		else {
			int keyCode = e.getKeyCode();
			str = str + "["+ keyCode +"("+KeyEvent.getKeyText(keyCode)+")]";
		}
*/
	}

}
