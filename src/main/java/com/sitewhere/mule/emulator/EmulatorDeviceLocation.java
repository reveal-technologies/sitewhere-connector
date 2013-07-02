package com.sitewhere.mule.emulator;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceLocation;
import com.sitewhere.spi.SiteWhereException;

@XmlRootElement(name = "DeviceLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmulatorDeviceLocation {

	/** Time at which the event occurred */
	@XmlAttribute
	private Calendar measurementTime;

	/** Latitude measurement */
	@XmlAttribute
	private Double latitude;

	/** Longitude measurement */
	@XmlAttribute
	private Double longitude;

	/** Elevation measurement */
	@XmlAttribute
	private Double elevation;

	@XmlElement(name = "Metadata")
	private List<EmulatorEventMetadata> metadata;

	public Calendar getMeasurementTime() {
		return measurementTime;
	}

	public void setMeasurementTime(Calendar measurementTime) {
		this.measurementTime = measurementTime;
	}

	public List<EmulatorEventMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<EmulatorEventMetadata> metadata) {
		this.metadata = metadata;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	/**
	 * Creates the REST API version of device location.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceLocation createDeviceLocation(DeviceAssignment assignment) throws SiteWhereException {
		if ((getLatitude() == null) || (getLongitude() == null)) {
			throw new SiteWhereException("Both latitude and longitude must be specified for a location.");
		}
		DeviceLocation location = new DeviceLocation();
		location.setSiteToken(assignment.getSiteToken());
		location.setDeviceAssignmentToken(assignment.getToken());
		location.setEventDate(getMeasurementTime());
		location.setReceivedDate(Calendar.getInstance());
		location.setLatitude(getLatitude());
		location.setLongitude(getLongitude());
		location.setElevation(getElevation());
		if (getMetadata() != null) {
			for (EmulatorEventMetadata metadata : getMetadata()) {
				location.addOrReplaceMetadata(metadata.getName(), metadata.getValue());
			}
		}
		return location;
	}
}