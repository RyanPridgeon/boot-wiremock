# boot-wiremock
Hacked Wiremock into a Spring Boot application so that you can register mocks onto a discovery service.

I've got it configured to register to Consul right now, but it's pretty straightforward to change for Eureka etc. Just do the same thing you'd do with any Spring Boot application.

I've included an example in the example folder. Just start Consul on the default localhost:8500, run the run.sh script, and see what happens when you hit POST localhost:5000/test or GET localhost:5000/thing.

The port and service name are configured by command line arguments in run.sh, and the mock is configured as a regular Wiremock standalone server using the mappings and __files directories. See BootWiremockProperties.java or use the command line argument --help for all possible command line arguments.
