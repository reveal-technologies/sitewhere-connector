/**
 * This file was automatically generated by the Mule Development Kit
 */
package com.sitewhere.mule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.sitewhere.geo.zone.ZoneMatcher;
import com.sitewhere.mule.connector.SiteWhereContextLogger;
import com.sitewhere.mule.emulator.EmulatorPayloadParserDelegate;
import com.sitewhere.rest.model.SiteWhereContext;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.service.SiteWhereClient;
import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.mule.IMuleProperties;
import com.sitewhere.spi.mule.delegate.IPayloadParserDelegate;
import com.sitewhere.spi.mule.delegate.ISiteWhereDelegate;
import com.sitewhere.spi.mule.delegate.IZoneProcessingDelegate;
import com.sitewhere.spi.server.hazelcast.ISiteWhereHazelcast;

/**
 * Allows SiteWhere REST operations to be executed from within a Mule flow.
 * 
 * @author Derek Adams
 */
@Connector(name = "sitewhere", schemaVersion = "1.0", friendlyName = "SiteWhere")
public class SiteWhereConnector {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereConnector.class);

	/** SiteWhere client */
	private SiteWhereClient client;

	/** Used to log SiteWhereContext to console */
	private SiteWhereContextLogger contextLogger = new SiteWhereContextLogger();

	/** Classloader that gets around Mule bugs */
	private SiteWhereClassloader swClassLoader;

	/** Hazelcast client for SiteWhere */
	private HazelcastInstance hazelcast;

	/** Indicates whether connected to Hazelcast */
	private boolean connected = false;

	/**
	 * SiteWhere API URL.
	 */
	@Optional
	@Configurable
	@Default("http://localhost:8080/sitewhere/api/")
	@Placement(group = "REST")
	@FriendlyName("SiteWhere API URL")
	private String apiUrl;

	/**
	 * SiteWhere REST username.
	 */
	@Optional
	@Configurable
	@Default("admin")
	@Placement(group = "REST")
	@FriendlyName("Username")
	private String restUsername;

	/**
	 * SiteWhere REST password.
	 */
	@Optional
	@Configurable
	@Default("password")
	@Placement(group = "REST")
	@FriendlyName("Password")
	private String restPassword;

	/**
	 * SiteWhere Hazelcast username.
	 */
	@Optional
	@Configurable
	@Default("sitewhere")
	@Placement(group = "Hazelcast", order = 1)
	@FriendlyName("Username")
	private String hazelcastUsername;

	/**
	 * SiteWhere Hazelcast password.
	 */
	@Optional
	@Configurable
	@Default("sitewhere")
	@Placement(group = "Hazelcast", order = 2)
	@FriendlyName("Password")
	private String hazelcastPassword;

	/**
	 * SiteWhere Hazelcast address.
	 */
	@Optional
	@Configurable
	@Default("localhost:5701")
	@Placement(group = "Hazelcast", order = 3)
	@FriendlyName("Remote Address")
	private String hazelcastAddress;

	/**
	 * Hazelcast connection timeout.
	 */
	@Optional
	@Configurable
	@Default("30000")
	@Placement(group = "Hazelcast", order = 4)
	@FriendlyName("Connection Timeout")
	private int hzConnectionTimeout;

	/**
	 * Show extra debug information for SiteWhere components.
	 */
	@Optional
	@Configurable
	@Default("false")
	@FriendlyName("Enable SiteWhere Debugging")
	private Boolean debug = false;

	@Inject
	private MuleContext muleContext;

	@Start
	public void doStart() throws MuleException {
		client = new SiteWhereClient(getApiUrl(), getRestUsername(), getRestPassword());
		swClassLoader = new SiteWhereClassloader(muleContext);
		LOGGER.info("SiteWhere connector using base REST url: " + getApiUrl());
	}

	/**
	 * Connect to Hazelcast.
	 * 
	 * @throws MuleException
	 */
	protected synchronized void connect() throws MuleException {
		if (connected) {
			return;
		}
		try {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setConnectionTimeout(getHzConnectionTimeout());
			clientConfig.getGroupConfig().setName(getHazelcastUsername());
			clientConfig.getGroupConfig().setPassword(getHazelcastPassword());
			clientConfig.addAddress(getHazelcastAddress());
			clientConfig.setSmartRouting(true);

			this.hazelcast = HazelcastClient.newHazelcastClient(clientConfig);
			LOGGER.info("Connected to SiteWhere Hazelcast cluster.");
			this.connected = true;
		} catch (Exception e) {
			throw new DefaultMuleException("Unable to connect to SiteWhere Hazelcast cluster.", e);
		}
	}

	/**
	 * Populates a new SiteWhere context from information in the current Mule event.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml
	 * sitewhere:payload-to-sitewhere-context}
	 * 
	 * @param delegate
	 *            delegate implementing <code>IPayloadParserDelegate</code>
	 * @param event
	 *            current Mule event
	 * @return the resulting SiteWhere context.
	 * @throws SiteWhereException
	 *             if an exception is thrown from the delegate.
	 */
	@Inject
	@Processor
	public ISiteWhereContext payloadToSitewhereContext(
			@FriendlyName("Payload Parser Delegate") String delegate, MuleEvent event)
			throws SiteWhereException {
		SiteWhereContext context = new SiteWhereContext();
		event.setFlowVariable(IMuleProperties.SITEWHERE_CONTEXT, context);

		IPayloadParserDelegate delegateInstance = null;
		if (delegate != null) {
			delegateInstance = createDelegate(delegate, IPayloadParserDelegate.class);
			delegateInstance.initialize(event);
			String hardwareId = delegateInstance.getDeviceHardwareId();
			if (hardwareId == null) {
				throw new SiteWhereException("Payload parser delegate returned null for hardware id.");
			}

			DeviceAssignment assignment = client.getCurrentAssignmentForDevice(hardwareId);
			context.setDeviceAssignment(assignment);
			context.setUnsavedDeviceLocations(delegateInstance.getLocations());
			context.setUnsavedDeviceMeasurements(delegateInstance.getMeasurements());
			context.setUnsavedDeviceAlerts(delegateInstance.getAlerts());
			context.setReplyTo(delegateInstance.getReplyTo());
			return context;
		} else {
			throw new SiteWhereException("Payload parser delegate required but not specified.");
		}
	}

	/**
	 * Creates a SiteWhere context from the event payload with the assumption that the
	 * payload is a JSON string repesenting a {@link DeviceEventBatch} object.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:emulator}
	 * 
	 * @param event
	 *            current Mule event
	 * @return a SiteWhere context built from the payload
	 * @throws SiteWhereException
	 *             if there is an error creating the context
	 */
	@Inject
	@Processor
	public ISiteWhereContext emulator(MuleEvent event) throws SiteWhereException {
		return payloadToSitewhereContext(EmulatorPayloadParserDelegate.class.getName(), event);
	}

	/**
	 * Save the device measurements currently in the SiteWhereContext.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:save-device-events}
	 * 
	 * 
	 * @param event
	 *            current Mule event
	 * @return the updated SiteWhere context
	 * @throws SiteWhereException
	 *             if events or state can not be saved
	 */
	@Inject
	@Processor
	public ISiteWhereContext saveDeviceEvents(MuleEvent event) throws SiteWhereException {
		ISiteWhereContext context = getSiteWhereContext(event);

		// Send unsaved events in a batch to be saved.
		DeviceEventBatch batch = new DeviceEventBatch();
		batch.getMeasurements().addAll(context.getUnsavedDeviceMeasurements());
		batch.getLocations().addAll(context.getUnsavedDeviceLocations());
		batch.getAlerts().addAll(context.getUnsavedDeviceAlerts());
		DeviceEventBatchResponse response =
				client.addDeviceEventBatch(context.getDeviceAssignment().getDeviceHardwareId(), batch);

		// Clear out unsaved events and copy saved events from response.
		context.getUnsavedDeviceMeasurements().clear();
		context.getUnsavedDeviceLocations().clear();
		context.getUnsavedDeviceAlerts().clear();
		context.getDeviceMeasurements().addAll(response.getCreatedMeasurements());
		context.getDeviceLocations().addAll(response.getCreatedLocations());
		context.getDeviceAlerts().addAll(response.getCreatedAlerts());

		return context;
	}

	/**
	 * Saves the current {@link ISiteWhereContext} payload into a standard flow variable
	 * location.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:push-sitewhere-context}
	 * 
	 * @param event
	 *            injected Mule event
	 * @return the event after processing.
	 * @throws SiteWhereException
	 *             if payload is not a SiteWhere context
	 */
	@Inject
	@Processor()
	public MuleEvent pushSitewhereContext(MuleEvent event) throws SiteWhereException {
		if (event.getMessage().getPayload() instanceof ISiteWhereContext) {
			event.setFlowVariable(IMuleProperties.SITEWHERE_CONTEXT, event.getMessage().getPayload());
			return event;
		} else {
			throw new SiteWhereException("Payload does not implement " + ISiteWhereContext.class.getName());
		}
	}

	/**
	 * Pulls the {@link ISiteWhereContext} from the standard flow variable into the
	 * message payload.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:pop-sitewhere-context}
	 * 
	 * @param event
	 *            injected Mule event
	 * @return the event after processing.
	 * @throws SiteWhereException
	 *             if no SiteWhere context is in expected flow variable
	 */
	@Inject
	@Processor()
	public MuleEvent popSitewhereContext(MuleEvent event) throws SiteWhereException {
		ISiteWhereContext context = getSiteWhereContext(event);
		if (context != null) {
			event.getMessage().setPayload(context);
			return event;
		} else {
			throw new SiteWhereException("No SiteWhere context found in expected flow variable.");
		}
	}

	/**
	 * Builds an {@link ISiteWhereContext} around the {@link IDeviceEvent} in the current
	 * payload. Looks up details for the assignment associated with the event including
	 * detailed device information.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml
	 * sitewhere:build-context-for-event}
	 * 
	 * @param event
	 *            injected Mule event
	 * @return the event after processing.
	 * @throws SiteWhereException
	 *             if no SiteWhere context is in expected flow variable
	 */
	@Inject
	@Processor()
	public MuleEvent buildContextForEvent(MuleEvent event) throws SiteWhereException {
		if (event.getMessage().getPayload() instanceof IDeviceEvent) {
			IDeviceEvent de = (IDeviceEvent) event.getMessage().getPayload();
			IDeviceAssignment assignment = client.getDeviceAssignmentByToken(de.getDeviceAssignmentToken());
			SiteWhereContext context = new SiteWhereContext();
			context.setDeviceAssignment(assignment);
			context.addDeviceEvent(de);
			event.setFlowVariable(IMuleProperties.SITEWHERE_CONTEXT, context);
			return event;
		} else {
			throw new SiteWhereException("Payload does not implement " + IDeviceEvent.class.getName());
		}
	}

	/**
	 * Logs information about the current SiteWhere context to the console.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:log-sitewhere-context}
	 * 
	 * @param event
	 *            injected Mule event
	 * @return the event after processing.
	 */
	@Inject
	@Processor()
	public MuleEvent logSitewhereContext(MuleEvent event) {
		try {
			ISiteWhereContext context = getSiteWhereContext(event);
			try {
				contextLogger.showDebugOutput(context);
			} catch (Throwable e) {
				LOGGER.error("Unable to marshal SiteWhere context information.", e);
			}
			return event;
		} catch (SiteWhereException e) {
			LOGGER.error(e);
			return event;
		}
	}

	/**
	 * Check whether locations are within zones specified for the site.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:perform-zone-checks}
	 * 
	 * @param delegate
	 *            delegate that generates alerts based on zones.
	 * @param event
	 *            mule event
	 * @return a list of alerts create requests generated from the zone checks.
	 * @throws SiteWhereException
	 *             if processing fails
	 */
	@Inject
	@Processor
	public List<IDeviceAlertCreateRequest> performZoneChecks(@FriendlyName("Zone Delegate") String delegate,
			MuleEvent event) throws SiteWhereException {
		ISiteWhereContext context = getSiteWhereContext(event);
		IZoneProcessingDelegate delegateInstance = null;
		List<IDeviceAlertCreateRequest> results = new ArrayList<IDeviceAlertCreateRequest>();
		if (delegate != null) {
			delegateInstance = createDelegate(delegate, IZoneProcessingDelegate.class);
			SearchResults<Zone> zones = client.listZonesForSite(context.getDeviceAssignment().getSiteToken());
			LOGGER.info("Performing zone checks for " + zones.getNumResults() + " zones.");
			for (IDeviceLocation location : context.getDeviceLocations()) {
				ZoneMatcher matcher = new ZoneMatcher(location, zones.getResults());
				List<IDeviceAlertCreateRequest> alerts = delegateInstance.handleZoneResults(context, matcher);
				if (alerts != null) {
					results.addAll(alerts);
				}
			}
		}
		return results;
	}

	/**
	 * Executes a delegate class that has access to SiteWhere and Mule internals.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:sitewhere-delegate}
	 * 
	 * @param delegate
	 *            delegate class to invoke
	 * @param event
	 *            mule event
	 * @return the sitewhere context
	 * @throws SiteWhereException
	 *             if processing fails
	 */
	@Inject
	@Processor
	public Object sitewhereDelegate(@FriendlyName("SiteWhere Delegate") String delegate, MuleEvent event)
			throws SiteWhereException {
		ISiteWhereContext context = getSiteWhereContext(event);
		ISiteWhereDelegate delegateInstance = null;
		Object response = context;
		if (delegate != null) {
			delegateInstance = createDelegate(delegate, ISiteWhereDelegate.class);
			response = delegateInstance.process(context, client, event);
		}
		return response;
	}

	/**
	 * Subscribes to Hazelcast measurements device events from SiteWhere for processing in
	 * Mule.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:subscribe-measurements}
	 * 
	 * @param callback
	 *            needed to generate new Mule messages
	 * @throws MuleException
	 *             if not able to connect to Hazelcast.
	 */
	@Source
	public void subscribeMeasurements(final SourceCallback callback) throws MuleException {
		connect();
		ITopic<DeviceMeasurements> measurementsTopic =
				hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_MEASUREMENTS_ADDED);
		measurementsTopic.addMessageListener(new MeasurementsEventListener(callback));
		LOGGER.info("Registered for device measurement events from SiteWhere.");
	}

	/**
	 * Handles inbound measurements events.
	 * 
	 * @author Derek
	 */
	private class MeasurementsEventListener implements MessageListener<DeviceMeasurements> {

		/** Used to put data on the bus */
		private SourceCallback callback;

		public MeasurementsEventListener(SourceCallback callback) {
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceMeasurements> message) {
			DeviceMeasurements measurements = message.getMessageObject();
			try {
				LOGGER.debug("Received measurements for: " + measurements.getId());
				callback.process(measurements);
			} catch (Exception e) {
				LOGGER.error("Unable to process measurements device event.", e);
			}
		}
	}

	/**
	 * Subscribes to Hazelcast location device events from SiteWhere for processing in
	 * Mule.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:subscribe-locations}
	 * 
	 * @param callback
	 *            needed to generate new Mule messages
	 * @throws MuleException
	 *             if not able to connect to Hazelcast.
	 */
	@Source
	public void subscribeLocations(final SourceCallback callback) throws MuleException {
		connect();
		ITopic<DeviceLocation> locationsTopic = hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_LOCATION_ADDED);
		locationsTopic.addMessageListener(new LocationsEventListener(callback));
		LOGGER.info("Registered for device location events from SiteWhere.");
	}

	/**
	 * Handles inbound location events.
	 * 
	 * @author Derek
	 */
	private class LocationsEventListener implements MessageListener<DeviceLocation> {

		/** Used to put data on the bus */
		private SourceCallback callback;

		public LocationsEventListener(SourceCallback callback) {
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceLocation> message) {
			DeviceLocation location = message.getMessageObject();
			try {
				LOGGER.debug("Received location for: " + location.getId());
				callback.process(location);
			} catch (Exception e) {
				LOGGER.error("Unable to process location device event.", e);
			}
		}
	}

	/**
	 * Subscribes to Hazelcast {@link DeviceAlert} events from SiteWhere for processing in
	 * Mule.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml sitewhere:subscribe-alerts}
	 * 
	 * @param callback
	 *            needed to generate new Mule messages
	 * @throws MuleException
	 *             if not able to connect to Hazelcast.
	 */
	@Source
	public void subscribeAlerts(final SourceCallback callback) throws MuleException {
		connect();
		ITopic<DeviceAlert> measurementsTopic = hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_ALERT_ADDED);
		measurementsTopic.addMessageListener(new AlertsEventListener(callback));
		LOGGER.info("Registered for device alert events from SiteWhere.");
	}

	/**
	 * Handles inbound alert events.
	 * 
	 * @author Derek
	 */
	private class AlertsEventListener implements MessageListener<DeviceAlert> {

		/** Used to put data on the bus */
		private SourceCallback callback;

		public AlertsEventListener(SourceCallback callback) {
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceAlert> message) {
			DeviceAlert alert = message.getMessageObject();
			try {
				LOGGER.debug("Received alert for: " + alert.getId());
				callback.process(alert);
			} catch (Exception e) {
				LOGGER.error("Unable to process alert device event.", e);
			}
		}
	}

	/**
	 * Subscribes to Hazelcast {@link DeviceCommandInvocation} events from SiteWhere for
	 * processing in Mule.
	 * 
	 * {@sample.xml ../../../doc/sitewhere-connector.xml
	 * sitewhere:subscribe-command-invocations}
	 * 
	 * @param callback
	 *            needed to generate new Mule messages
	 * @throws MuleException
	 *             if not able to connect to Hazelcast.
	 */
	@Source
	public void subscribeCommandInvocations(final SourceCallback callback) throws MuleException {
		connect();
		ITopic<DeviceCommandInvocation> commandsTopic =
				hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_COMMAND_INVOCATION_ADDED);
		commandsTopic.addMessageListener(new CommandInvocationsEventListener(callback));
		LOGGER.info("Registered for device command invocation events from SiteWhere.");
	}

	/**
	 * Handles inbound {@link DeviceCommandInvocation} events.
	 * 
	 * @author Derek
	 */
	private class CommandInvocationsEventListener implements MessageListener<DeviceCommandInvocation> {

		/** Used to put data on the bus */
		private SourceCallback callback;

		public CommandInvocationsEventListener(SourceCallback callback) {
			this.callback = callback;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceCommandInvocation> message) {
			DeviceCommandInvocation invocation = message.getMessageObject();
			try {
				LOGGER.debug("Received command invocation for: " + invocation.getId());
				callback.process(invocation);
			} catch (Exception e) {
				LOGGER.error("Unable to process device command invocation event.", e);
			}
		}
	}

	/**
	 * Creates a {@link ISiteWhereContext} based on the given event.
	 * 
	 * @param event
	 * @return
	 * @throws SiteWhereException
	 */
	protected ISiteWhereContext getContextFor(IDeviceEvent event) throws SiteWhereException {
		SiteWhereContext context = new SiteWhereContext();
		DeviceAssignment assignment = client.getDeviceAssignmentByToken(event.getDeviceAssignmentToken());
		context.setDeviceAssignment(assignment);
		return context;
	}

	/**
	 * Get the SiteWhereContext from a pre-determined flow variable or message payload.
	 * 
	 * @param event
	 * @return
	 * @throws SiteWhereException
	 */
	protected ISiteWhereContext getSiteWhereContext(MuleEvent event) throws SiteWhereException {
		ISiteWhereContext context =
				(ISiteWhereContext) event.getFlowVariable(IMuleProperties.SITEWHERE_CONTEXT);
		if (context == null) {
			Object payload = event.getMessage().getPayload();
			if (payload instanceof ISiteWhereContext) {
				context = (ISiteWhereContext) payload;
			}
		}
		if (context == null) {
			throw new SiteWhereException(
					"SiteWhereContext not found in expected flow variable or message payload.");
		}
		return context;
	}

	@SuppressWarnings("unchecked")
	protected <T> T createDelegate(String classname, Class<T> classtype) throws SiteWhereException {
		try {
			Class<?> resolved = swClassLoader.loadClass(classname);
			if (!classtype.isAssignableFrom(resolved)) {
				throw new SiteWhereException("Delgate not an instance of " + classtype.getName());
			}
			Object created = resolved.newInstance();
			return (T) created;
		} catch (ClassNotFoundException e) {
			throw new SiteWhereException("Delegate class not found.", e);
		} catch (InstantiationException e) {
			throw new SiteWhereException("Could not create delegate class.", e);
		} catch (IllegalAccessException e) {
			throw new SiteWhereException("Could not access delegate class.", e);
		}
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getRestUsername() {
		return restUsername;
	}

	public void setRestUsername(String restUsername) {
		this.restUsername = restUsername;
	}

	public String getRestPassword() {
		return restPassword;
	}

	public void setRestPassword(String restPassword) {
		this.restPassword = restPassword;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public MuleContext getMuleContext() {
		return muleContext;
	}

	public void setMuleContext(MuleContext muleContext) {
		this.muleContext = muleContext;
	}

	public String getHazelcastUsername() {
		return hazelcastUsername;
	}

	public void setHazelcastUsername(String hazelcastUsername) {
		this.hazelcastUsername = hazelcastUsername;
	}

	public String getHazelcastPassword() {
		return hazelcastPassword;
	}

	public void setHazelcastPassword(String hazelcastPassword) {
		this.hazelcastPassword = hazelcastPassword;
	}

	public String getHazelcastAddress() {
		return hazelcastAddress;
	}

	public void setHazelcastAddress(String hazelcastAddress) {
		this.hazelcastAddress = hazelcastAddress;
	}

	public int getHzConnectionTimeout() {
		return hzConnectionTimeout;
	}

	public void setHzConnectionTimeout(int hzConnectionTimeout) {
		this.hzConnectionTimeout = hzConnectionTimeout;
	}
}