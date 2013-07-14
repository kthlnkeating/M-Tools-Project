package gov.va.med.foundations.adapter.heartbeat;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.spi.VistaLinkManagedConnection;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import java.util.Timer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *<br>This class manages the VistaHeartBeatTimerTask to 
 *<br>synchronize all access to the Timer and the TimerTask.
 *<br>All public methods are synchronized(this)
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaHeartBeatTimerManager {

	/**
	 * The timer task used to run the scheduled heart beats
	 */
	private VistaHeartbeatTimerTask timerTask;


	/**
	 * The timer used to schedule the heart beats
	 */
	private Timer heartBeatTimer;

	/**
	 * The rate at which the heart beat will be scheduled
	 */
	private long heartBeatRate;


	/**
	 * Identifies whether or not the heart beat timer is active
	 */
	private boolean heartBeatActive;

	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaHeartBeatTimerManager.class);

	/**
	 * Constructor for VistaHeartBeatTimerManager.
	 * Creates a new VistaHeartBeatTimerTask, and Timer
	 */
	public VistaHeartBeatTimerManager() {
		super();
		timerTask = new VistaHeartbeatTimerTask();
		heartBeatTimer = new Timer(true);
		setHeartBeatActive(false);
	}

	/**
	 * Method addManagedConnection.
	 * <br>Adds a managed connection to perform heartbeat on
	 * <br>This method will activate the timer if it has not 
	 * <br>already been started
	 * @param mc - the managed connection to add
	 * @throws HeartBeatInitializationFailedException
	 */
	public void addManagedConnection(VistaLinkManagedConnection mc)
		throws HeartBeatInitializationFailedException {

		synchronized (this) {

			timerTask.addManagedConnection(mc);


			if(logger.isDebugEnabled()){
				logger.debug("Managed connection added");
			}

			if (!isHeartBeatActive()) {
				try {

					executeHeartBeatInteraction(mc);

				} catch (HeartBeatInitializationFailedException e) {
						
					if(logger.isEnabledFor(Level.ERROR)){

						String errMsg = (new StringBuffer())
							.append(
							"Error getting heart beat rate")
							.append("\n\t")
							.append(ExceptionUtils
									.getFullStackTrace(e))
							.toString();
					
						logger.error(errMsg);
					}
					throw e;
				}
			}

		}
	}

	/**
	 * Method executeHeartBeatInteraction.
	 * <br>Executes the interaction with M to retrieve the rate at 
	 * <br>which the timer executes the heart beat.
	 * <br>The timer is started with the retrieved rate. 
	 * @param mc - the managed connection to perform the interaction with
	 * @throws HeartBeatInitializationFailedException
	 */
	private void executeHeartBeatInteraction(VistaLinkManagedConnection mc)
		throws HeartBeatInitializationFailedException {

		try {

			if(logger.isDebugEnabled()){
				logger.debug("Executing heartBeat interaction");
			}
			if(logger.isDebugEnabled()){
				logger.debug("argument mc = "+mc.toString());
			}
			VistaHeartBeatTimerRequest req = new VistaHeartBeatTimerRequest();
			if(logger.isDebugEnabled()){
				logger.debug("req = "+req.toString());
			}

			VistaHeartBeatTimerResponseFactory resFactory =
				new VistaHeartBeatTimerResponseFactory();
			if(logger.isDebugEnabled()){
				logger.debug("resFactory = "+resFactory.toString());
			}

			VistaHeartBeatTimerResponse resp =
				(VistaHeartBeatTimerResponse) mc.executeInteraction(
					req,
					resFactory);
			if(logger.isDebugEnabled()){
				logger.debug("resp = "+resp.toString());
			}

			startTimer(resp.getHeartBeatRateMillis());
			if(logger.isDebugEnabled()){
				logger.debug("Returned from startTimer");
			}

		} catch (VistaLinkFaultException e) {
			throw new HeartBeatInitializationFailedException(
				"VistaLinkFaultException in executeHeartBeatInteraction.",
				e);
		} catch (FoundationsException e) {
			throw new HeartBeatInitializationFailedException(
				"FoundationsException in executeHeartBeatInteraction.",
				e);
		}

	}

	/**
	 * Method removeManagedConnection.
	 * <br>removes a managed connection from the heart beat task.
	 * <br>The timer is cancelled if there are no managed connections
	 * <br>in the list.
	 * @param mc -the managed connection to be removed from the list
	 */
	public void removeManagedConnection(VistaLinkManagedConnection mc) {

		synchronized (this) {
			timerTask.removeManagedConnection(mc);
			if(logger.isDebugEnabled()){
				logger.debug("Removed managed connection");
			}
			
			if (timerTask.isManagedConnectionListEmpty()) {
				if(logger.isDebugEnabled()){
					logger.debug("cancelling timer");
				}

				cancelTimer();

			}
		}
	}

	/**
	 * Method startTimer.
	 * <br>Starts the timer with the specified rate
	 * @param rate - the rate at which the timer 
	 * <br>will execute the timer task
	 */
	private void startTimer(long rate) {
		if (!isHeartBeatActive()) {

			setHeartBeatRate(rate);
			heartBeatTimer.schedule(timerTask, rate, rate);
			setHeartBeatActive(true);

			if(logger.isDebugEnabled()){
				logger.debug("Timer started");
			}
		} else {
			if(logger.isDebugEnabled()){
				logger.debug("Timer is already running");
			}
		}
	}

	/**
	 * Method cancelTimer.
	 * <br>Cancels the timer
	 */
	private void cancelTimer() {
		heartBeatTimer.cancel();
		setHeartBeatActive(false);
		if(logger.isDebugEnabled()){
			logger.debug("Timer cancelled");
		}
	}

	/**
	 * Returns the heartBeatActive.
	 * @return boolean
	 */
	public boolean isHeartBeatActive() {
		synchronized (this) {
			return heartBeatActive;
		}
	}

	/**
	 * Returns the heartBeatRate.
	 * @return long
	 */
	public long getHeartBeatRate() {
		synchronized (this) {
			return heartBeatRate;
		}
	}

	/**
	 * Sets the heartBeatActive.
	 * @param heartBeatActive The heartBeatActive to set
	 */
	private void setHeartBeatActive(boolean heartBeatActive) {
		this.heartBeatActive = heartBeatActive;
	}

	/**
	 * Sets the heartBeatRate.
	 * @param heartBeatRate The heartBeatRate to set
	 */
	private void setHeartBeatRate(long heartBeatRate) {
		this.heartBeatRate = heartBeatRate;
	}

}
