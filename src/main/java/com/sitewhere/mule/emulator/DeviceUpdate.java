package com.sitewhere.mule.emulator;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DeviceUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceUpdate {

	/** Unique device hardware id */
	@XmlAttribute
	private String hardwareId;

	/** Device measurement updates */
	@XmlElement(name = "DeviceMeasurements")
	private List<EmulatorDeviceMeasurements> measurementsList;

	/** Device location updates */
	@XmlElement(name = "DeviceLocation")
	private List<EmulatorDeviceLocation> locationList;

	/** Device alert updates */
	@XmlElement(name = "DeviceAlert")
	private List<EmulatorDeviceAlert> alertList;

	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	public List<EmulatorDeviceMeasurements> getMeasurementsList() {
		return measurementsList;
	}

	public void setMeasurementsList(List<EmulatorDeviceMeasurements> measurementsList) {
		this.measurementsList = measurementsList;
	}

	public List<EmulatorDeviceLocation> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<EmulatorDeviceLocation> locationList) {
		this.locationList = locationList;
	}

	public List<EmulatorDeviceAlert> getAlertList() {
		return alertList;
	}

	public void setAlertList(List<EmulatorDeviceAlert> alertList) {
		this.alertList = alertList;
	}
}