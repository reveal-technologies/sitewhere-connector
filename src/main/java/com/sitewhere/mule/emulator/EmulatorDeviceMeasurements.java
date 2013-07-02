package com.sitewhere.mule.emulator;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceMeasurements;

@XmlRootElement(name = "DeviceMeasurements")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmulatorDeviceMeasurements {

	/** Time at which the measurements were taken */
	@XmlAttribute
	private Calendar measurementTime;

	@XmlElement(name = "DeviceMeasurement")
	private List<EmulatorEventMetadata> measurements;

	@XmlElement(name = "Metadata")
	private List<EmulatorEventMetadata> metadata;

	public Calendar getMeasurementTime() {
		return measurementTime;
	}

	public void setMeasurementTime(Calendar measurementTime) {
		this.measurementTime = measurementTime;
	}

	public List<EmulatorEventMetadata> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<EmulatorEventMetadata> measurements) {
		this.measurements = measurements;
	}

	public List<EmulatorEventMetadata> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<EmulatorEventMetadata> metadata) {
		this.metadata = metadata;
	}

	/**
	 * Creates the REST API version of device measurements.
	 * 
	 * @return
	 */
	public DeviceMeasurements createDeviceMeasurements(DeviceAssignment assignment) {
		DeviceMeasurements measurements = new DeviceMeasurements();
		measurements.setSiteToken(assignment.getSiteToken());
		measurements.setDeviceAssignmentToken(assignment.getToken());
		measurements.setEventDate(getMeasurementTime());
		measurements.setReceivedDate(Calendar.getInstance());
		if (getMeasurements() != null) {
			for (EmulatorEventMetadata measurement : getMeasurements()) {
				measurements.getMeasurementsMetadata().addOrReplaceMetadata(measurement.getName(),
						measurement.getValue());
			}
		}
		if (getMetadata() != null) {
			for (EmulatorEventMetadata metadata : getMetadata()) {
				measurements.addOrReplaceMetadata(metadata.getName(), metadata.getValue());
			}
		}
		return measurements;
	}
}