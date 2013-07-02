package com.sitewhere.mule.emulator;

import org.mule.api.MuleContext;
import org.mule.construct.AbstractFlowConstruct;

/**
 * Placeholder flow construct.
 * 
 * @author dadams
 */
public class EmulatorFlowConstruct extends AbstractFlowConstruct {

	public EmulatorFlowConstruct(MuleContext muleContext) {
		super("Emulator", muleContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.construct.AbstractFlowConstruct#getConstructType()
	 */
	public String getConstructType() {
		return "Emulator";
	}
}