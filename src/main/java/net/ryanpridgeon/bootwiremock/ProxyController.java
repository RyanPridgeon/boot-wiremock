package net.ryanpridgeon.bootwiremock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

/**
 * Configures and spins up a Wiremock server and provides proxy endpoints
 * to access it from this service. This was just a simple way to fire up Wiremock.
 *  
 * @author ryanpridgeon
 *
 */
@RestController
public class ProxyController {

	private WireMockServer server;

	@Value("${wiremock.port}")
	private int wireMockPort;
	
	@Value("${wiremock.directory}")
	private String wireMockDirectory;

	private String wireMockHost;

	@PostConstruct
	public void init() {
		wireMockHost = "http://localhost:" + wireMockPort;
		server = new WireMockServer(WireMockConfiguration
				.options()
				.port(wireMockPort)
				.usingFilesUnderDirectory(wireMockDirectory));
		
		System.out.println("Starting Wiremock server at " + wireMockHost);
		server.start();
	}

	@PreDestroy
	public void destroy() {
		server.stop();
	}
	
	@RequestMapping(value = "/**")
	public ResponseEntity<String> handleRequest(HttpServletRequest request,
			@RequestHeader MultiValueMap<String, String> headers, @RequestBody(required = false) String body) {
		logRequest(request, headers, body);
		
		HttpEntity<Object> requestEntity = body == null ? new HttpEntity<>(headers) : new HttpEntity<>(body, headers);

		RestTemplate rest = new RestTemplate();
		ResponseEntity<String> response;
		
		try {
			response = rest.exchange(wireMockHost + request.getRequestURI(),
					HttpMethod.resolve(request.getMethod()), requestEntity, String.class);
		} catch (Exception e) {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		logResponse(response);
		return response;
	}

	private void logRequest(HttpServletRequest request, MultiValueMap<String, String> headers, String body) {
		System.out.println(String.format("===== Sending %s request to %s\nWith headers:%s", request.getMethod(),
				request.getRequestURI(), headers));
		if (body != null) {
			System.out.println("With body:\n" + body);
		}
	}

	private void logResponse(ResponseEntity<String> response) {
		System.out.println(String.format("===== Received response\nWith headers: %s\nWith body: %s",
				response.getHeaders(), response.getBody()));
	}
}
