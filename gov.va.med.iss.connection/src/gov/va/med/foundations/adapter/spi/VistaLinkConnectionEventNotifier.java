package gov.va.med.foundations.adapter.spi;

import java.util.Vector;

import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;

import org.apache.log4j.Logger;

/**
 * <br>This class manages a list of event listeners and notifies each 
 * <br>of them when an event has been fired by a managed connection
 *
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaLinkConnectionEventNotifier {

	/**
	 * The logger used by this class
	 */
	private static final Logger logger =
		Logger.getLogger(VistaLinkConnectionEventNotifier.class);

	/**
	 * A vector of event listeners to whom this class will send events
	 */
	private Vector listeners; //a vector of event listeners

	/**
	 * Constructor for event notifier
	 */
	public VistaLinkConnectionEventNotifier() {
		if (logger.isDebugEnabled()) {
			logger.debug("Constructing");
		}
		listeners = new Vector();
	}

	/**
	 * Method connectionClosed.
	 * <br>Notifies listeners a connection closed event has been fired
	 * @param event
	 */
	public void connectionClosed(ConnectionEvent event) {

		if (logger.isDebugEnabled()) {
			logger.debug("Connection closed");
		}
		sendEvent(event, ConnectionEvent.CONNECTION_CLOSED);

	}

	/**
	 * Method connectionErrorOccurred.
	 * <br>Notifies listeners a connection error event has been fired
	 * @param event
	 */
	public void connectionErrorOccurred(ConnectionEvent event) {

		if (logger.isDebugEnabled()) {
			logger.debug("Connection Error");
		}
		sendEvent(event, ConnectionEvent.CONNECTION_ERROR_OCCURRED);
	}

	/**
	 * Method sendEvent.
	 * <br>Fires the events to the listeners
	 * @param ce
	 * @param eventType
	 */
	private void sendEvent(ConnectionEvent ce, int eventType) {

		Object[] list;

		synchronized (listeners) {
			list = listeners.toArray();
		}

		int size = list.length;
		for (int i = 0; i < size; i++) {
			ConnectionEventListener l = (ConnectionEventListener) list[i];
			switch (eventType) {
				case ConnectionEvent.CONNECTION_CLOSED :
					l.connectionClosed(ce);
					break;
				case ConnectionEvent.CONNECTION_ERROR_OCCURRED :
					l.connectionErrorOccurred(ce);
					break;
				default :
					if (logger.isDebugEnabled()) {
/* JLI 050228						logger.debug((new StringBuffer())
						.append("Event type not supported->")
						.append(eventType));
*/
					}
			}
		}
	}

	/**
	 * Method addConnectorListener.
	 * <br>Adds an event listener to local vector of event listeners
	 * @param l - a listener to add 
	 */
	public void addConnectorListener(ConnectionEventListener l) {
		if (logger.isDebugEnabled()) {
			logger.debug("addConnectionEventListener");
		}
		synchronized (listeners) {
			listeners.addElement(l);
		}
	}

	/**
	 * Method removeConnectorListener
	 * <br>Removes an event listener from local vector of 
	 * <br>event listeners.
	 * @param l - listener to remove
	 */
	public void removeConnectorListener(ConnectionEventListener l) {
		if (logger.isDebugEnabled()) {
			logger.debug("removeConnectionEventListener");
		}
		synchronized (listeners) {
			listeners.removeElement(l);
		}
	}

}
