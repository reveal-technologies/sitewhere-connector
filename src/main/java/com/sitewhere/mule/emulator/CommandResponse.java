package com.sitewhere.mule.emulator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.sitewhere.spi.ISiteWhereContext;

/**
 * Response returned to REST caller.
 * 
 * @author dadams
 */
@XmlRootElement(name = "CommandResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommandResponse {

	/** Indicates result of call */
	@XmlAttribute
	private CommandResult result;

	/** Response message */
	@XmlAttribute
	private String message;

	@XmlTransient
	private ISiteWhereContext context;

	/** Required for JAXB */
	public CommandResponse() {
	}

	public CommandResponse(CommandResult result) {
		this.result = result;
	}

	public CommandResult getResult() {
		return result;
	}

	public void setResult(CommandResult result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ISiteWhereContext getContext() {
		return context;
	}

	public void setContext(ISiteWhereContext context) {
		this.context = context;
	}
}