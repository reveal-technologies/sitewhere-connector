package com.sitewhere.mule.emulator;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sitewhere.rest.model.device.DeviceAlert;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.AlertSource;

@XmlRootElement(name = "DeviceAlert")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmulatorDeviceAlert {

	/** Time at which the event occurred */
	@XmlAttribute
	private Calendar measurementTime;

	/** Alert metadata */
	@XmlElement(name = "Metadata")
	private List<EmulatorEventMetadata> metadata;

	@XmlAttribute
	public String type;

	/** Alert message */
	@XmlAttribute
	public String message;

	/** Alert acknowledged flag */
	@XmlAttribute
	private Boolean acknowledged;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	/**
	 * Creates the REST API version of device location.
	 * 
	 * @param assignment
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAlert createDeviceAlert(DeviceAssignment assignment) throws SiteWhereException {
		if (getType() == null) {
			throw new SiteWhereException("Type must be specified for an alert.");
		}
		if (getMessage() == null) {
			throw new SiteWhereException("Message must be specified for an alert.");
		}
		DeviceAlert alert = new DeviceAlert();
		alert.setSource(AlertSource.Device);
		alert.setSiteToken(assignment.getSiteToken());
		alert.setDeviceAssignmentToken(assignment.getToken());
		alert.setEventDate(getMeasurementTime());
		alert.setReceivedDate(Calendar.getInstance());
		alert.setType(getType());
		alert.setMessage(getMessage());
		alert.setAcknowledged((isAcknowledged() != null) ? isAcknowledged() : false);
		if (getMetadata() != null) {
			for (EmulatorEventMetadata metadata : getMetadata()) {
				alert.addOrReplaceMetadata(metadata.getName(), metadata.getValue());
			}
		}
		return alert;
	}
}
