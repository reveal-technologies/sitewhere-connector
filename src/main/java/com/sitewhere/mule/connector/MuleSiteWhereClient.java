/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mule.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.mule.api.annotations.rest.RestHttpClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sitewhere.rest.ISiteWhereWebConstants;
import com.sitewhere.rest.client.SiteWhereErrorHandler;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.batch.BatchOperation;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.request.SiteCreateRequest;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.AssetSearchResults;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.DeviceAlertSearchResults;
import com.sitewhere.rest.model.search.DeviceAssignmentSearchResults;
import com.sitewhere.rest.model.search.DeviceCommandSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupElementSearchResults;
import com.sitewhere.rest.model.search.DeviceGroupSearchResults;
import com.sitewhere.rest.model.search.DeviceLocationSearchResults;
import com.sitewhere.rest.model.search.DeviceSearchResults;
import com.sitewhere.rest.model.search.DeviceSpecificationSearchResults;
import com.sitewhere.rest.model.search.DeviceStreamSearchResults;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.ZoneSearchResults;
import com.sitewhere.rest.model.system.Version;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Implementation of {@link ISiteWhereClient} that fixes dependency problems with normal
 * client.
 * 
 * @author Derek
 */
public class MuleSiteWhereClient implements ISiteWhereClient {

	@RestHttpClient
	private RestTemplate client;

	/** Base URL used for REST calls */
	private String baseUrl = "http://localhost:8080/sitewhere/api/";

	/** Username used for REST calls */
	private String username = "admin";

	/** Password used for REST calls */
	private String password = "password";

	/** Tenant auth token used for REST calls */
	private String tenantAuthToken = "sitewhere1234567890";

	public MuleSiteWhereClient(String url, String username, String password, String tenantAuthToken,
			int connectTimeoutMs) {
		this.client = new RestTemplate();
		this.username = username;
		this.password = password;
		this.tenantAuthToken = tenantAuthToken;

		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		addMessageConverters(converters);
		client.setMessageConverters(converters);
		client.setErrorHandler(new SiteWhereErrorHandler());
		this.baseUrl = url;
	}

	protected void addMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter());
		converters.add(new ByteArrayHttpMessageConverter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereClient#getSiteWhereVersion()
	 */
	@Override
	public Version getSiteWhereVersion() throws SiteWhereException {
		Map<String, String> vars = new HashMap<String, String>();
		return sendRest(getBaseUrl() + "system/version", HttpMethod.GET, null, Version.class, vars);
	}

	@Override
	public DeviceSpecification createDeviceSpecification(DeviceSpecificationCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceSpecification updateDeviceSpecification(String token,
			DeviceSpecificationCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceSpecificationSearchResults listDeviceSpecifications(boolean includeDeleted,
			boolean includeDetailedAssetInfo, SearchCriteria criteria) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceSpecification deleteDeviceSpecification(String token, boolean deletePermanently)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceCommand createDeviceCommand(String specificationToken, DeviceCommandCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceCommandSearchResults listDeviceCommands(String specificationToken, boolean includeDeleted)
			throws SiteWhereException {
		return null;
	}

	@Override
	public Site createSite(SiteCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public Site getSiteByToken(String token) throws SiteWhereException {
		return null;
	}

	@Override
	public Zone createZone(String siteToken, ZoneCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public ZoneSearchResults listZonesForSite(String siteToken) throws SiteWhereException {
		return null;
	}

	@Override
	public Device createDevice(DeviceCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public Device getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
		return null;
	}

	@Override
	public Device updateDevice(String hardwareId, DeviceCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceSearchResults listDevices(boolean includeDeleted, boolean excludeAssigned,
			boolean populateSpecification, boolean populateAssignment, DateRangeSearchCriteria criteria)
			throws SiteWhereException {
		return null;
	}

	@Override
	public Device deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignment getCurrentAssignmentForDevice(String hardwareId) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignmentSearchResults listDeviceAssignmentHistory(String hardwareId)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignmentSearchResults getAssignmentsForAsset(String siteToken, String assetModuleId,
			String assetId, DeviceAssignmentStatus status, SearchCriteria criteria) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceEventBatchResponse addDeviceEventBatch(String hardwareId, DeviceEventBatch batch)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.ISiteWhereClient#getDeviceAssignmentByToken(java.lang.String)
	 */
	@Override
	public DeviceAssignment getDeviceAssignmentByToken(String assignmentToken) throws SiteWhereException {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("assignmentToken", assignmentToken);
		return sendRest(getBaseUrl() + "assignments/{assignmentToken}", HttpMethod.GET, null,
				DeviceAssignment.class, vars);
	}

	@Override
	public DeviceAssignmentSearchResults listAssignmentsForSite(String token) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignment deleteDeviceAssignment(String assignmentToken, boolean force)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAssignment updateDeviceAssignmentMetadata(String token, MetadataProvider metadata)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceMeasurements createDeviceMeasurements(String assignmentToken,
			DeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		return null;
	}

	@Override
	public SearchResults<DeviceMeasurements> listDeviceMeasurements(String assignmentToken,
			DateRangeSearchCriteria searchCriteria) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceLocation createDeviceLocation(String assignmentToken, DeviceLocationCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceLocationSearchResults listDeviceLocations(String assignmentToken, int maxCount)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAlert createDeviceAlert(String assignmentToken, DeviceAlertCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceAlertSearchResults listDeviceAlerts(String assignmentToken, int maxCount)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceStream createDeviceStream(String assignmentToken, DeviceStreamCreateRequest request)
			throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceStreamSearchResults listDeviceStreams(String assignmentToken,
			DateRangeSearchCriteria criteria) throws SiteWhereException {
		return null;
	}

	@Override
	public void addDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber, byte[] data)
			throws SiteWhereException {
	}

	@Override
	public byte[] getDeviceStreamData(String assignmentToken, String streamId, long sequenceNumber)
			throws SiteWhereException {
		return null;
	}

	@Override
	public byte[] listDeviceStreamData(String assignmentToken, String streamId,
			DateRangeSearchCriteria criteria) throws SiteWhereException {
		return null;
	}

	@Override
	public BatchOperation createBatchCommandInvocation(String batchToken, String commandToken,
			Map<String, String> parameters, List<String> hardwareIds) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroup createDeviceGroup(DeviceGroupCreateRequest request) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroup deleteDeviceGroup(String token) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroupSearchResults listDeviceGroups(String role, SearchCriteria criteria,
			boolean includeDeleted) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroupElementSearchResults addDeviceGroupElements(String groupToken,
			List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroupElementSearchResults listDeviceGroupElements(String groupToken, boolean includeDetails,
			SearchCriteria criteria) throws SiteWhereException {
		return null;
	}

	@Override
	public DeviceGroupElementSearchResults deleteDeviceGroupElements(String groupToken,
			List<DeviceGroupElementCreateRequest> elements) throws SiteWhereException {
		return null;
	}

	@Override
	public AssetSearchResults getAssetsByModuleId(String moduleId, String criteria) throws SiteWhereException {
		return null;
	}

	protected String getAuthHeader() {
		String token = getUsername() + ":" + getPassword();
		String encoded = new String(Base64.encodeBase64(token.getBytes()));
		return "Basic " + encoded;
	}

	protected <S, T> S sendRest(String url, HttpMethod method, T input, Class<S> clazz,
			Map<String, String> vars) throws SiteWhereSystemException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", getAuthHeader());
			headers.add(ISiteWhereWebConstants.HEADER_TENANT_TOKEN, getTenantAuthToken());
			HttpEntity<T> entity = new HttpEntity<T>(input, headers);
			ResponseEntity<S> response = getClient().exchange(url, method, entity, clazz, vars);
			return response.getBody();
		} catch (ResourceAccessException e) {
			if (e.getCause() instanceof SiteWhereSystemException) {
				throw (SiteWhereSystemException) e.getCause();
			}
			throw new RuntimeException(e);
		}
	}

	public RestTemplate getClient() {
		return client;
	}

	public void setClient(RestTemplate client) {
		this.client = client;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
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

	public String getTenantAuthToken() {
		return tenantAuthToken;
	}

	public void setTenantAuthToken(String tenantAuthToken) {
		this.tenantAuthToken = tenantAuthToken;
	}
}