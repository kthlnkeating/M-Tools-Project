package gov.va.med.iss.mdebugger;

/**
 * This class is used to provide the time-out functionality for READ
 * commands.  A separate thread is generated, and after sleeping for
 * the time-out period, the instance checks for whether it was cancelled
 * (at the completion of a normal read process), and if not, it sets a
 * flag indicating that the process timed out, then runs a method that
 * checks for a time-out and causes the read value (or in this case the
 * timed out value -- usually a null string, but -1 for a star read) to
 * be returned to the server.
 * 
 * @author vhaisfiveyj
 *
 */
public class MDebuggerReadWait implements Runnable {
	
	private boolean cancelled = false;
	private long dTimeMilliseconds;

	public void run() {
		try {
			Thread.sleep(dTimeMilliseconds);
		} catch (Exception e) {
		}
		if (!cancelled) {
			if (!(MDebuggerReadCommand.isReadCompleted)) { //--jspivey: this was called using an async thread from a ui prior.
				try {
					MDebuggerReadCommand.isTimerFired = true;
					MDebuggerReadCommand.checkChars();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void cancel() {
		cancelled = true;
	}
	public void setTimeOut(long timeOutMilliseconds) {
		dTimeMilliseconds = timeOutMilliseconds;
	}

}
