package com.sitewhere.mule.emulator;

import org.apache.log4j.Logger;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.module.jersey.JerseyResourcesComponent;
import org.mule.processor.AbstractInterceptingMessageProcessor;

import com.sitewhere.spi.mule.IMuleProperties;
import com.sun.jersey.spi.container.ContainerResponse;

/**
 * Inspects the Jersey response and pulls out SiteWhere context if available.
 * 
 * @author dadams
 */
public class JerseyResponseProcessor extends AbstractInterceptingMessageProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(JerseyResponseProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.api.processor.MessageProcessor#process(org.mule.api.MuleEvent)
	 */
	public MuleEvent process(MuleEvent event) throws MuleException {
		ContainerResponse jersey = event.getMessage().getInvocationProperty(
				JerseyResourcesComponent.JERSEY_RESPONSE);
		if (jersey != null) {
			CommandResponse response = (CommandResponse) jersey.getEntity();
			if (response.getResult() == CommandResult.Success) {
				if (response.getContext() != null) {
					event.setFlowVariable(IMuleProperties.SITEWHERE_CONTEXT, response.getContext());
					return processNext(event);
				}
				LOGGER.error("Unable to process message. Emulator REST service did not set the SiteWhere context.");
			} else {
				LOGGER.error("Unable to process message. Emulator REST service call failed: "
						+ response.getMessage());
			}
		} else {
			LOGGER.error("Unable to process message. Emulator REST response not found.");
		}
		return event;
	}
}