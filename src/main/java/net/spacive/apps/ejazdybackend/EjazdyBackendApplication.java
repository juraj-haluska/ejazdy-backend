package net.spacive.apps.ejazdybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <h1>eJazdy Spring Boot API</h1>
 * This application is implementation of RESTful API
 * for eJazdy project, which is a lesson registration
 * system for driving school.
 *
 * @author  Juraj Haluska
 * @version 1.0
 */
@SpringBootApplication
public class EjazdyBackendApplication {

	/**
	 * This is the entry point of application.
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		SpringApplication.run(EjazdyBackendApplication.class, args);
	}
}
