package gov.va.med.foundations.utilities;

//import x.gov.va.med.iss.log4j.*;

import org.apache.log4j.Logger;


/**
 * This class gives an easy way to capture performance statistics and log them
 * to a log file.  Internally System.currentTimeMillis() is used.
 * 
 * Typical steps for using this class:<br> 
 * 1. Create an instance: auditTimer = new AutitTimer() <br>
 * 2. auditTimer.start() <br>   
 * 3. auditTimer.stop() <br>
 * 4. auditTimer.getTimeElapsedMillis()<br>
 * 5. 
 * 
 * get   autitTimer.start() should be called before auditTimer. stop()
 * is called.
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class AuditTimer {

	/**
	 *  Default log4j logger to be used for outputing time audit messages
	 *  if no logger is provided in the constructor.
	 *  Logger gov.va.med.foundations.utilities.AuditTimer level info.
	 */
	private static Logger defaultLogger = Logger.getLogger(AuditTimer.class);

	/**
	 *  log4j logger to be used for outputing time audit messages.
	 */
	private Logger logger = null;

	/**
	 *  Start time 
	 */
	private long startTimeMillis = 0;
	/**
	 *  Stop time
	 */
	private long stopTimeMillis = 0;
	/**
	 *  Elapsed time
	 */
	private long timeElapsedMillis = 0;

	/**
	 * Default constructor. 
	 * Default logger gov.va.med.foundations.utilities.AuditTimer will be used.
	 */
	public AuditTimer() {
		logger = defaultLogger;
	}

	/**
	 * Constructor that accepts logger to be used for output.
	 * Application can pass in their own loggers to have granual control
	 * over logging.  
	 * @param logger
	 */
	public AuditTimer(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Method start.
	 */
	public void start() {
		startTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Method stop. 
	 * If start() was not called at least once before stop() is called
	 * timeElapsedMillis is set to -1. <br>
	 * This method does not through Exception to keep client code simple.
	 * 
	 * @return long
	 */
	public long stop() {
		if (startTimeMillis == 0) {
			// don't want to through exception to keep client coding simple.
			// just return negative timeElapsedMillis  
			timeElapsedMillis = -1;
		} else {
			stopTimeMillis = System.currentTimeMillis();
			timeElapsedMillis = stopTimeMillis - startTimeMillis;
		}
		return timeElapsedMillis;
	}

	/**
	 * Method getTimeElapsedMillis.
	 * @return long
	 */
	public long getTimeElapsedMillis() {
		return timeElapsedMillis;
	}

	/**
	 * Method log the same as log(String).
	 */
	public void log() {
		log(null);
	}

	/**
	 * Method log logs a message to the log4j logger in a following format:<br>
	 * your_message elapsed_time_milliseconds
	 * 
	 * @param message
	 */
	public void log(String message) {
		StringBuffer logString = new StringBuffer(255);
		
		if ((message == null) || ("".equals(message))) {
			logString.append("");
		} else {
			logString.append(message).append(" ");
		}
		logString.append(timeElapsedMillis);

		logger.info(logString.toString());
	}

}
