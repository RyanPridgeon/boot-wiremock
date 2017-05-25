package net.ryanpridgeon.bootwiremock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the list of properties that can be configured through the command line. The fields here
 * are public so that the POJO methods in CliArgs work correctly. Annotations describe each property.
 * The buildSpringProperties method is provided to build a spring properties map out of the values.
 * @author ryanpridgeon
 *
 */
public class BootWiremockProperties {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface PossibleInput {
		
		public String inputName() default "(No name)";
		public String inputValue() default "(No description)";
	}
	
	@PossibleInput(inputName = "name", 
			inputValue = "The name of the service, this will be used to register to discovery")
	public String name = "boot-wiremock-server";
	
	@PossibleInput(inputName = "port", 
			inputValue = "Port for this mock service.")
	public Integer port = 1234;
	
	@PossibleInput(inputName = "wiremockDir", 
			inputValue = "Directory where wiremock stub files will be found. Defaults to current directory")
	public String wiremockDir = ".";
	
	@PossibleInput(inputName = "wiremockPort", 
			inputValue = "Port for the Wiremock server to run on (defaults to port + 1)")
	public Integer wiremockPort = null;
	
	@PossibleInput(inputName = "managementPort", 
			inputValue = "Management port which exposes health endpoints (defaults to port + 2)")
	public Integer managementPort = null;
	
	@PossibleInput(inputName = "consulHost", 
			inputValue = "The consul server host. Defaults to localhost")
	public String consulHost = null;
	
	@PossibleInput(inputName = "consulPort", 
			inputValue = "The consul server port. Defaults to 8500")
	public Integer consulPort = null;
	
	public Map<String, Object> buildSpringProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("spring.application.name", this.name);
		properties.put("server.port", this.port);
		properties.put("wiremock.directory", this.wiremockDir);
		properties.put("wiremock.port", valueOrDefault(this.wiremockPort, this.port + 1));
		properties.put("management.port", valueOrDefault(this.managementPort, this.port + 2));
		properties.put("spring.cloud.consul.host", valueOrDefault(this.consulHost, "localhost"));
		properties.put("spring.cloud.consul.port", valueOrDefault(this.consulPort, 8500));
		return properties;
	}

	private static <T> T valueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}
}
