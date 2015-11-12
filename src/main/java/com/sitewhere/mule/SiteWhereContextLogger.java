/*
 * SiteWhereContextLogger.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.mule.util.StringMessageUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

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
		List<String> messages = new ArrayList<String>();
		messages.add("Information from SiteWhere context flow variable:");
		messages.add("");
		if (context.getDeviceAssignment() != null) {
			String assignmentAsJson =
					jsonMapper.writer(jsonPrinter).writeValueAsString(context.getDeviceAssignment());
			messages.add("Device Assignment Information:");
			messages.addAll(getJsonAsStringList(assignmentAsJson));
		} else {
			messages.add("Device Information: No device found in context.");
		}
		messages.add("");
		if (context.getUnsavedDeviceMeasurements().size() > 0) {
			messages.add("--- Unsaved Device Measurements ---");
			for (IDeviceMeasurementsCreateRequest measurements : context.getUnsavedDeviceMeasurements()) {
				String measurementsAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(measurements);
				messages.addAll(getJsonAsStringList(measurementsAsJson));
				messages.add("");
			}
		}
		if (context.getUnsavedDeviceLocations().size() > 0) {
			messages.add("--- Unsaved Device Locations ---");
			for (IDeviceLocationCreateRequest location : context.getUnsavedDeviceLocations()) {
				String locationAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(location);
				messages.addAll(getJsonAsStringList(locationAsJson));
				messages.add("");
			}
		}
		if (context.getUnsavedDeviceAlerts().size() > 0) {
			messages.add("--- Unsaved Device Alerts ---");
			for (IDeviceAlertCreateRequest alert : context.getUnsavedDeviceAlerts()) {
				String alertAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(alert);
				messages.addAll(getJsonAsStringList(alertAsJson));
				messages.add("");
			}
		}
		if (context.getDeviceMeasurements().size() > 0) {
			messages.add("--- Device Measurements ---");
			for (IDeviceMeasurements measurements : context.getDeviceMeasurements()) {
				String measurementsAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(measurements);
				messages.addAll(getJsonAsStringList(measurementsAsJson));
				messages.add("");
			}
		}
		if (context.getDeviceLocations().size() > 0) {
			messages.add("--- Device Locations ---");
			for (IDeviceLocation location : context.getDeviceLocations()) {
				String locationAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(location);
				messages.addAll(getJsonAsStringList(locationAsJson));
				messages.add("");
			}
		}
		if (context.getDeviceAlerts().size() > 0) {
			messages.add("--- Device Alerts ---");
			for (IDeviceAlert alert : context.getDeviceAlerts()) {
				String alertAsJson = jsonMapper.writer(jsonPrinter).writeValueAsString(alert);
				messages.addAll(getJsonAsStringList(alertAsJson));
				messages.add("");
			}
		}
		if (context.getDeviceCommandInvocations().size() > 0) {
			messages.add("--- Device Command Invocations ---");
			for (IDeviceCommandInvocation invocation : context.getDeviceCommandInvocations()) {
				String asJson = jsonMapper.writer(jsonPrinter).writeValueAsString(invocation);
				messages.addAll(getJsonAsStringList(asJson));
				messages.add("");
			}
		}
		if (context.getDeviceCommandResponses().size() > 0) {
			messages.add("--- Device Command Responses ---");
			for (IDeviceCommandResponse response : context.getDeviceCommandResponses()) {
				String asJson = jsonMapper.writer(jsonPrinter).writeValueAsString(response);
				messages.addAll(getJsonAsStringList(asJson));
				messages.add("");
			}
		}
		if (context.getReplyTo() == null) {
			messages.add("No 'reply to' information passed by originator.");
		} else {
			messages.add("Originator 'reply to' information: " + context.getReplyTo());
		}
		String message = StringMessageUtils.getBoilerPlate(messages, '*', 100);
		LOGGER.info("\n" + message + "\n");
	}

	/**
	 * Convert JSON into a list of strings.
	 * 
	 * @param json
	 * @return
	 */
	public List<String> getJsonAsStringList(String json) {
		List<String> results = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(json, "\n");
		while (tokenizer.hasMoreTokens()) {
			results.add(tokenizer.nextToken().replaceAll("\\s+$", ""));
		}
		return results;
	}
}