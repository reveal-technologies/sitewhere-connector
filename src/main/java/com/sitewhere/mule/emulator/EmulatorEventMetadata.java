package com.sitewhere.mule.emulator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmulatorEventMetadata {

	/** Metadata name */
	@XmlAttribute
	private String name;

	/** Metadata value */
	@XmlAttribute
	private String value;

	/** Metadata type */
	@XmlAttribute
	private EventMetadataType type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public EventMetadataType getType() {
		return type;
	}

	public void setType(EventMetadataType type) {
		this.type = type;
	}
}