package com.sitewhere.mule.emulator;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.SiteWhereContext;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.service.SiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * REST service that accepts inputs for the SiteWhere emulator.
 * 
 * @author dadams
 * 
 */
@Path("/emulator")
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML })
public class EmulatorRestService {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(EmulatorRestService.class);

	/** SiteWhere client instance */
	private SiteWhereClient client = new SiteWhereClient();

	/**
	 * Process a device update.
	 * 
	 * @param update
	 * @return
	 * @throws SiteWhereException
	 */
	@POST
	@Path("/device/update")
	public CommandResponse updateDevice(DeviceUpdate update) throws SiteWhereException {
		// Verify that the device is valid and has an assignment.
		Device device = getClient().getDeviceByHardwareId(update.getHardwareId());
		if (device == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidHardwareId, ErrorLevel.ERROR);
		}
		if (device.getAssignment() == null) {
			throw new SiteWhereSystemException(ErrorCode.DeviceNotAssigned, ErrorLevel.ERROR);
		}

		// Create the REST model measurements if passed.
		List<IDeviceMeasurements> measurementsList = new ArrayList<IDeviceMeasurements>();
		if (update.getMeasurementsList() != null) {
			for (EmulatorDeviceMeasurements measurements : update.getMeasurementsList()) {
				measurementsList.add(measurements.createDeviceMeasurements(device.getAssignment()));
			}
		}

		// Create the REST model location if passed.
		List<IDeviceLocation> locationList = new ArrayList<IDeviceLocation>();
		if (update.getLocationList() != null) {
			for (EmulatorDeviceLocation location : update.getLocationList()) {
				locationList.add(location.createDeviceLocation(device.getAssignment()));
			}
		}

		// Create the REST model alert if passed.
		List<IDeviceAlert> alertList = new ArrayList<IDeviceAlert>();
		if (update.getAlertList() != null) {
			for (EmulatorDeviceAlert alert : update.getAlertList()) {
				alertList.add(alert.createDeviceAlert(device.getAssignment()));
			}
		}

		// Return context populated with objects.
		SiteWhereContext context = new SiteWhereContext();
		context.setDevice(device);
		context.setDeviceAssignment(device.getAssignment());
		context.setDeviceMeasurements(measurementsList);
		context.setDeviceLocations(locationList);
		context.setDeviceAlerts(alertList);
		CommandResponse response = new CommandResponse(CommandResult.Success);
		response.setContext(context);
		return response;
	}

	public SiteWhereClient getClient() {
		return client;
	}

	public void setClient(SiteWhereClient client) {
		this.client = client;
		LOGGER.info("SiteWhere Emulator using client with base URL: " + client.getBaseUrl());
	}
}