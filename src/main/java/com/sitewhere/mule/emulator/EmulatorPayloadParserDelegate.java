/*
 * EmulatorPayloadParserDelegate.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule.emulator;

import java.io.IOException;
import java.util.List;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.rest.model.device.DeviceEventBatch;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.mule.delegate.IPayloadParserDelegate;

/**
 * Payload parser that expects a JSON string containing a {@link DeviceEventBatch}.
 * 
 * @author Derek
 */
public class EmulatorPayloadParserDelegate implements IPayloadParserDelegate {

	/** Batch information marshaled from payload */
	protected DeviceEventBatch batch;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.mule.delegate.IPayloadParserDelegate#initialize(org.mule.api.MuleEvent)
	 */
	public void initialize(MuleEvent event) throws SiteWhereException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String payload = event.getMessageAsString();
			batch = mapper.readValue(payload, DeviceEventBatch.class);
		} catch (MuleException e) {
			throw new SiteWhereException("Error accessing payload of Mule event.", e);
		} catch (JsonParseException e) {
			throw new SiteWhereException("Unable to parse emulator payload.", e);
		} catch (JsonMappingException e) {
			throw new SiteWhereException("Unable to map emulator payload.", e);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to process emulator payload.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.mule.delegate.IPayloadParserDelegate#getDeviceHardwareId()
	 */
	public String getDeviceHardwareId() throws SiteWhereException {
		return batch.getHardwareId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.mule.delegate.IPayloadParserDelegate#getLocations()
	 */
	public List<IDeviceLocationCreateRequest> getLocations() throws SiteWhereException {
		return batch.getLocations();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.mule.delegate.IPayloadParserDelegate#getMeasurements()
	 */
	public List<IDeviceMeasurementsCreateRequest> getMeasurements() throws SiteWhereException {
		return batch.getMeasurements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.mule.delegate.IPayloadParserDelegate#getAlerts()
	 */
	public List<IDeviceAlertCreateRequest> getAlerts() throws SiteWhereException {
		return batch.getAlerts();
	}
}