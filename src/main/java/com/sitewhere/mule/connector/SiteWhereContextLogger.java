/*
 * SiteWhereContextLogger.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule.connector;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;

/**
 * Used to send SiteWhere context information to the console.
 * 
 * @author Derek
 */
public class SiteWhereContextLogger {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiteWhereContextLogger.class);

	/** Used for JSON conversion */
	private ObjectMapper jsonMapper = new ObjectMapper();

	/** Used for printing JSON output */
	private PrettyPrinter jsonPrinter = new DefaultPrettyPrinter();

	/**
	 * Show SiteWhereContext data in the console via logger.
	 * 
	 * @param context
	 * @throws JsonProcessingException
	 */
	public void showDebugOutput(ISiteWhereContext context) throws JsonProcessingException {
		if (context.getDevice() != null) {
			String deviceAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(context.getDevice());
			LOGGER.info("\n\n*** SITEWHERE DEVICE ***\n" + deviceAsJson);
		}
		LOGGER.info("\n\n*** UNSAVED DEVICE MEASUREMENTS ***\n");
		for (IDeviceMeasurementsCreateRequest measurements : context.getUnsavedDeviceMeasurements()) {
			String measurementsAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(measurements);
			LOGGER.info(measurementsAsJson + "\n");
		}
		LOGGER.info("\n\n*** UNSAVED DEVICE LOCATIONS ***\n");
		for (IDeviceLocationCreateRequest location : context.getUnsavedDeviceLocations()) {
			String locationAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(location);
			LOGGER.info(locationAsJson + "\n");
		}
		LOGGER.info("\n\n*** UNSAVED DEVICE ALERTS ***\n");
		for (IDeviceAlertCreateRequest alert : context.getUnsavedDeviceAlerts()) {
			String alertAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(alert);
			LOGGER.info(alertAsJson + "\n");
		}
		LOGGER.info("\n\n*** DEVICE MEASUREMENTS ***\n");
		for (IDeviceMeasurements measurements : context.getDeviceMeasurements()) {
			String measurementsAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(measurements);
			LOGGER.info(measurementsAsJson + "\n");
		}
		LOGGER.info("\n\n*** DEVICE LOCATIONS ***\n");
		for (IDeviceLocation location : context.getDeviceLocations()) {
			String locationAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(location);
			LOGGER.info(locationAsJson + "\n");
		}
		LOGGER.info("\n\n*** DEVICE ALERTS ***\n");
		for (IDeviceAlert alert : context.getDeviceAlerts()) {
			String alertAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(alert);
			LOGGER.info(alertAsJson + "\n");
		}
	}
}