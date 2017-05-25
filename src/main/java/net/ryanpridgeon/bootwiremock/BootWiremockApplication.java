package net.ryanpridgeon.bootwiremock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import com.jenkov.cliargs.CliArgs;

import net.ryanpridgeon.bootwiremock.BootWiremockProperties.PossibleInput;

@EnableDiscoveryClient
@SpringBootApplication
public class BootWiremockApplication {

	public static void main(String[] args) {
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("--help") || argsList.contains("-H") || argsList.contains("-h")) {
			showHelp();
		} else {
			// Get configuration from command line arguments
			BootWiremockProperties input = new CliArgs(args).switchPojo(BootWiremockProperties.class);
			
			Map<String, Object> properties = input.buildSpringProperties();
			
			new SpringApplicationBuilder()
			    .sources(BootWiremockApplication.class)                
			    .properties(properties)
			    .run(args);
		}
	}
	
	private static void showHelp() {
		System.out.println("Wiremock with Consul discovery. Possible arguments;\n");
		FieldCallback fc = new HelpSwitchPrinter();
		ReflectionUtils.doWithFields(BootWiremockProperties.class, fc);
	}
	
	private static class HelpSwitchPrinter implements FieldCallback {
		/**
		 * Uses the annotations on a property field to print help information to the terminal.
		 */
		@Override
		public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
			PossibleInput description = AnnotationUtils.findAnnotation(field, PossibleInput.class);
			System.out.println(description.inputName() + "\n\t" + (description == null ? "(No description)" : description.inputValue()));
		}
	}
}
