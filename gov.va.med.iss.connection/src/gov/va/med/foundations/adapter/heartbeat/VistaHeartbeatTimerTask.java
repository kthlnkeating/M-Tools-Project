package gov.va.med.foundations.adapter.heartbeat;

import gov.va.med.foundations.adapter.record.VistaLinkFaultException;
import gov.va.med.foundations.adapter.record.VistaLinkResponseVO;
import gov.va.med.foundations.adapter.spi.VistaLinkManagedConnection;
import gov.va.med.foundations.utilities.ExceptionUtils;
import gov.va.med.foundations.utilities.FoundationsException;

import java.util.ArrayList;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * This class represents the TimerTask used to run the heart beat
 * <br> tasks on a schedule. The Timer object schedules this object at 
 * <br> a rate set by the HeartBeatTimerManager. 
 * 
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaHeartbeatTimerTask extends TimerTask {

	/**
	 * An array list to hold the managed connections that this 
	 * <br>TimerTask will execute heart beats on
	 */
	private ArrayList managedConnections;

	/**
	 * The rate at which the heart beat is scheduled
	 */
	private long heartBeatRate;

	/**
	 * The logger used for this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaHeartbeatTimerTask.class);

	/**
	 * Constructor for VistaHeartbeatTimerTask.
	 */
	public VistaHeartbeatTimerTask() {
		super();
		managedConnections = new ArrayList();
	}

	/**
	 * <br>Iterates through a list of managed Connections and executes
	 * <br>a heart beat on them.
	 * <br>If the heart beat fails on a managed connection, this method
	 * <br>notifies that managed connection and its evenet listeners
	 * <br>that an error has occurred
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		if(logger.isDebugEnabled()){
			logger.debug("Run");
		}
		
		Object[] array;

		synchronized (managedConnections) {
			array = managedConnections.toArray();
		}

		VistaHeartBeatTimerRequest req;
		try {
			req = new VistaHeartBeatTimerRequest();
		} catch (FoundationsException e) {

			if(logger.isEnabledFor(Priority.ERROR)){
				String errMsg = (new StringBuffer())
					.append(
					"could not create VistaHeartBeatTimerRequest")
					.append("\n\t")
					.append(ExceptionUtils
							.getFullStackTrace(e))
					.toString();
						
				logger.error(errMsg);

			}
			return;
		}

		VistaHeartBeatTimerResponseFactory resFactory =
			new VistaHeartBeatTimerResponseFactory();

		for (int i = 0; i < array.length; i++) {
			try {
				VistaLinkManagedConnection mc =
					(VistaLinkManagedConnection) array[i];

				if ((mc.isValid())
					&& ((System.currentTimeMillis()
						- mc.getLastInteractionTimeMillis())
						> (getHeartBeatRate()))) {

					if(logger.isDebugEnabled()){
						logger.debug(
						(new StringBuffer()).append(
							"sending heartbeat->").append(
							mc.toString()));
					}
					
					if (isHeartBeatSuccessful(mc
						.executeInteraction(req, resFactory))) {

						if(logger.isDebugEnabled()){
							logger.debug(
							(new StringBuffer())
								.append("HeartBeat successful->")
								.append(mc.toString())
								.append("->")
								.append(System.currentTimeMillis()));
						}
						
					} else {
						String errMsg =
							(new StringBuffer())
								.append("heartbeat failed->")
								.append(mc.toString())
								.append("->")
								.append(System.currentTimeMillis())
								.toString();

						HeartBeatFailedException e =
							new HeartBeatFailedException(errMsg);

						if(logger.isEnabledFor(Priority.ERROR)){
							
							errMsg = (new StringBuffer())
								.append(
								"could not execute heart beat")
								.append("\n\t")
								.append(ExceptionUtils
										.getFullStackTrace(e))
								.toString();
					
							logger.error(errMsg);
						}
						mc.notifyErrorOccurred(e);
					}

				}
			} catch (VistaLinkFaultException e) {
					
				if(logger.isEnabledFor(Priority.ERROR)){
					String errMsg = (new StringBuffer())
						.append(
						"VistaLinkFaultException occured during heartbeat.")
						.append("\n\t")
						.append(ExceptionUtils
								.getFullStackTrace(e))
						.toString();
						
					logger.error(errMsg);
				}
					
			} catch (FoundationsException e) {

				if(logger.isEnabledFor(Priority.ERROR)){
					String errMsg = (new StringBuffer())
						.append(
						"FoundationsException occured during heartbeat.")
						.append("\n\t")
						.append(ExceptionUtils
								.getFullStackTrace(e))
						.toString();
						
					logger.error(errMsg);
				}
			}
		}
	}

	/**
	 * Method addManagedConnection.
	 * <br>Adds a VistaLinkManagedConnection to managedConnections List
	 * <br>This method is synchronized on the managedConnections List
	 * @param mc - the managed connection to add
	 */
	public void addManagedConnection(VistaLinkManagedConnection mc) {
		if(logger.isDebugEnabled()){
			logger.debug("adding managed connection");
		}
		synchronized (managedConnections) {
			managedConnections.add(mc);
		}
	}

	/**
	 * Method removeManagedConnection.
	 * <br>Removes a VistaLinkManagedConnection from the managedConnections List
	 * <br>This method is synchronized on the managedConnections List
	 * @param mc
	 */
	public void removeManagedConnection(VistaLinkManagedConnection mc) {
		if(logger.isDebugEnabled()){
			logger.debug("remove managed connection");
		}
		synchronized (managedConnections) {
			managedConnections.remove(mc);
		}
	}

	/**
	 * Method isManagedConnectionListEmpty.
	 * <br>Returns whether or not the managedConnections List isEmpty()
	 * <br>This method is synchronized on the managedConnections List
	 * @return boolean
	 */
	public boolean isManagedConnectionListEmpty() {
		synchronized (managedConnections) {
			return managedConnections.isEmpty();
		}
	}

	/**
	 * Method isHeartBeatSuccessful.
	 * <br>Returns whether the heartbeat request was successful or not
	 * @param res
	 * @return boolean
	 */
	private static boolean isHeartBeatSuccessful(VistaLinkResponseVO res) {

		return ((VistaHeartBeatTimerResponse) res).isHeartBeatSuccessful();

	}

	/**
	 * Returns the heartBeatRate.
	 * @return long
	 */
	public long getHeartBeatRate() {
		return heartBeatRate;
	}

	/**
	 * Sets the heartBeatRate.
	 * @param heartBeatRate The heartBeatRate to set
	 */
	public void setHeartBeatRate(long heartBeatRate) {
		this.heartBeatRate = heartBeatRate;
	}

}
