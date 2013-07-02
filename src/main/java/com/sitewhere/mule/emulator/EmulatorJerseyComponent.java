package com.sitewhere.mule.emulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.api.component.JavaComponent;
import org.mule.component.DefaultJavaComponent;
import org.mule.module.jersey.JerseyResourcesComponent;
import org.mule.object.SingletonObjectFactory;

import com.sitewhere.rest.service.SiteWhereClient;

/**
 * Extends standard Mule Jersey support to support the emulator REST service.
 * 
 * @author dadams
 */
public class EmulatorJerseyComponent extends JerseyResourcesComponent {

	public EmulatorJerseyComponent(SiteWhereClient client) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("client", client);
		SingletonObjectFactory factory = new SingletonObjectFactory(EmulatorRestService.class, properties);
		JavaComponent emulatorComponent = new DefaultJavaComponent(factory);
		List<JavaComponent> components = new ArrayList<JavaComponent>();
		components.add(emulatorComponent);
		setComponents(components);
		setExceptionMapper(new EmulatorExceptionMapper());
	}
}