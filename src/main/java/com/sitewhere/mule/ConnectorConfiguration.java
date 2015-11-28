/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule;

import org.apache.log4j.Logger;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Information used to connect to a SiteWhere instance.
 * 
 * @author Derek
 */
@ConnectionManagement(friendlyName = "Configuration")
public class ConnectorConfiguration {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ConnectorConfiguration.class);

	/** Number of milliseconds to wait for Hazelcast to connect */
	private static final int HAZELCAST_CONNECTION_TIMEOUT = 10000;

	/** Hazelcast address set in connect */
	private String hazelcastAddress;

	/** Hazelcast username set in connect */
	private String username;

	/** Hazelcast password set in connect */
	private String password;

	/** Tenant id set in connect */
	private String tenantId;

	/**
	 * Show extra debug information for SiteWhere components.
	 */
	@Configurable
	@Default("false")
	@FriendlyName("Enable SiteWhere Debugging")
	private Boolean debug = false;

	/** Indicates whether connected to Hazelcast */
	private boolean connected = false;

	/** Hazelcast client for SiteWhere */
	private HazelcastInstance hazelcast;

	/**
	 * Connect to Hazelcast.
	 * 
	 * @param hazelcastUrl
	 * @throws ConnectionException
	 */
	@Connect
	@TestConnectivity
	public synchronized void connect(
			@FriendlyName("Hazelcast Url") @Default("localhost:5701") String hazelcastAddress,
			@ConnectionKey @FriendlyName("Username") String username,
			@FriendlyName("Password") @Default("sitewhere") @Password String password,
			@FriendlyName("Tenant id") @Default("default") String tenantId) throws ConnectionException {
		if (connected) {
			return;
		}
		this.hazelcastAddress = hazelcastAddress;
		this.username = username;
		this.password = password;
		this.tenantId = tenantId;
		try {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(getUsername());
			clientConfig.getGroupConfig().setPassword(getPassword());

			clientConfig.getNetworkConfig().setConnectionTimeout(HAZELCAST_CONNECTION_TIMEOUT);
			clientConfig.getNetworkConfig().addAddress(getHazelcastAddress());
			clientConfig.getNetworkConfig().setSmartRouting(true);

			this.hazelcast = HazelcastClient.newHazelcastClient(clientConfig);
			LOGGER.info("Connected to SiteWhere Hazelcast cluster.");
			this.connected = true;
		} catch (Exception e) {
			throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "",
					"Unable to connect to SiteWhere Hazelcast cluster.", e);
		}
	}

	/**
	 * Get handle to Hazelcast instance.
	 * 
	 * @return
	 */
	public HazelcastInstance getHazelcast() {
		return hazelcast;
	}

	/**
	 * Get the device assignment distributed cache.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public IMap<String, IDeviceAssignment> getAssignmentCache() throws SiteWhereException {
		if (!isConnected()) {
			throw new SiteWhereException("Unable to get assignment cache. Not connected");
		}
		return getHazelcast().getMap(getTenantId() + "-assignmentCache");
	}

	/**
	 * Gets an assignment from the assignment cache by token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	public IDeviceAssignment getAssignmentByToken(String token) throws SiteWhereException {
		return getAssignmentCache().get(token);
	}

	/**
	 * Disconnnect from Hazelcast.
	 */
	@Disconnect
	public synchronized void disconnect() {
		if ((connected) && (hazelcast != null)) {
			hazelcast.shutdown();
		}
	}

	/**
	 * Checks whether connected to Hazelcast.
	 * 
	 * @return
	 */
	@ValidateConnection
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Used for debugging.
	 * 
	 * @return
	 */
	@ConnectionIdentifier
	public String connectionId() {
		return "001";
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getHazelcastAddress() {
		return hazelcastAddress;
	}

	public void setHazelcastAddress(String hazelcastAddress) {
		this.hazelcastAddress = hazelcastAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}
}