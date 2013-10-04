/*
 * SiteWhereClassloader.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule;

import org.mule.api.MuleContext;

/**
 * Wraps Mule classloader to make loadClass() a synchronized call.
 * 
 * @author Derek
 */
public class SiteWhereClassloader extends ClassLoader {

	public SiteWhereClassloader(MuleContext context) {
		super(context.getExecutionClassLoader());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}
}